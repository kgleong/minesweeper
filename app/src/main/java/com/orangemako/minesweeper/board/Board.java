package com.orangemako.minesweeper.board;

import com.orangemako.minesweeper.exceptions.InvalidArgumentException;
import com.orangemako.minesweeper.exceptions.OutOfOrderException;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Board {
    final static int DEFAULT_NUM_MINES = 10;

    // Board creation phase states
    final static int BOARD_CREATED = 0;
    final static int GRID_CREATED = 1;
    final static int MINES_PLACED = 2;
    final static int GRID_POPULATED = 3;

    private int currentState = BOARD_CREATED;
    private int numMines;
    private int dimension;
    private BoardSquare[][] boardGrid = null;
    private Set<BoardSquare> mineSquares;

    private Board(int dimension, int numMines) {
        this.dimension = dimension;
        this.numMines = numMines;
    }

    private void init() throws OutOfOrderException {
        initBoardGrid();
        initAndPlaceMines();
        calculateNumberedSquares();
    }

    private void initBoardGrid() throws OutOfOrderException {
        if(currentState == BOARD_CREATED) {
            boardGrid = new BoardSquare[dimension][dimension];
            currentState = GRID_CREATED;
        }
        else {
            throw new OutOfOrderException("Grid creation must follow board creation.");
        }
    }

    private void initAndPlaceMines() throws OutOfOrderException {
        if(currentState == GRID_CREATED) {
            mineSquares = new HashSet<>(numMines);

            // Create mines
            for (int i = 0; i < numMines; i++) {
                BoardSquare square;

                do {
                    int x = new Random().nextInt(dimension);
                    int y = new Random().nextInt(dimension);

                    square = new BoardSquare(x, y);
                    square.containsMine = true;
                } while (!mineSquares.add(square));
            }

            // Place mines
            for(BoardSquare square : mineSquares) {
                boardGrid[square.y][square.x] = square;
            }

            currentState = MINES_PLACED;
        }
        else {
            throw new OutOfOrderException("Mine placement must follow grid creation");
        }
    }

    private void calculateNumberedSquares() throws OutOfOrderException {
        if (currentState == MINES_PLACED) {
            for(BoardSquare mine : mineSquares) {
                int x = mine.x;
                int y = mine.y;

                int startingY = Math.max(0, y - 1);
                int startingX = Math.max(0, x - 1);

                for(int i = startingY; i < dimension && i <= y + 1; i++) {
                    for(int j = startingX; j < dimension && j <= x + 1; j++) {

                        BoardSquare square = boardGrid[i][j];

                        if(square == null) {
                            boardGrid[i][j] = square = new BoardSquare(j, i);
                        }
                        else if(square.doesContainMine()) {
                            // Don't need to calculate adjacent mines count for
                            // squares containing mines.
                            continue;
                        }
                        square.adjacentMinesCount++;
                    }
                }
            }
            currentState = GRID_POPULATED;
        }
        else {
            throw new OutOfOrderException("Calculation of numbered squares requires mines to be placed");
        }
    }

    public BoardSquare[][] getBoardGrid() {
        return boardGrid;
    }

    public Set<BoardSquare> getMineSquares() {
        return mineSquares;
    }

    public static class Builder {
        int dimension;
        int numMines = DEFAULT_NUM_MINES;

        public Builder dimension(int dimension) throws InvalidArgumentException {
            if(dimension > 0) {
                this.dimension = dimension;
            }
            else {
                throw new InvalidArgumentException("Dimension must be greater than 0.");
            }
            return this;
        }

        public Builder numMines(int numMines) throws InvalidArgumentException {
            if(numMines > 0) {
                this.numMines = numMines;
            }
            else {
                throw new InvalidArgumentException("Mine count must be greater than 0.");
            }
            return this;
        }

        public Board build() throws InvalidArgumentException, OutOfOrderException {
            if(dimension > 0 && numMines > 0) {
                Board board = new Board(dimension, numMines);
                board.init();

                return board;
            }
            else {
                throw new InvalidArgumentException("Dimension and mine count must be greater than 0.");
            }
        }
    }
}
