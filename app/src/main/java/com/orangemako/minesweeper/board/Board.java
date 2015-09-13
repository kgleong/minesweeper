package com.orangemako.minesweeper.board;

import com.orangemako.minesweeper.exceptions.InitializationException;
import com.orangemako.minesweeper.exceptions.InvalidArgumentException;
import com.orangemako.minesweeper.exceptions.OutOfOrderException;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Board {
    public static final int DEFAULT_DIMENSION = 8;
    public static final int DEFAULT_NUM_MINES = 10;

    // Board creation phase states
    final static int BOARD_CREATED = 0;
    final static int GRID_CREATED = 1;
    final static int MINES_PLACED = 2;
    final static int GRID_POPULATED = 3;

    private int mCurrentState = BOARD_CREATED;
    private int mNumMines;
    private int mDimension;
    private BoardSquare[][] mBoardGrid = null;
    private Set<BoardSquare> mMineSquares;

    private Board(int dimension, int numMines) {
        mDimension = dimension;
        mNumMines = numMines;
    }

    private void init() throws OutOfOrderException, InitializationException {
        initBoardGrid();
        initAndPlaceMines();
        calculateNumberedSquares();
    }

    private void initBoardGrid() throws OutOfOrderException {
        if(mCurrentState == BOARD_CREATED) {
            mBoardGrid = new BoardSquare[mDimension][mDimension];
            mCurrentState = GRID_CREATED;
        }
        else {
            throw new OutOfOrderException("Grid creation must follow board creation.");
        }
    }

    private void initAndPlaceMines() throws OutOfOrderException, InitializationException {
        if(mCurrentState == GRID_CREATED) {
            mMineSquares = new HashSet<>(mNumMines);

            // Create mines
            for (int i = 0; i < mNumMines; i++) {
                BoardSquare square;

                do {
                    int x = new Random().nextInt(mDimension);
                    int y = new Random().nextInt(mDimension);

                    square = new BoardSquare(x, y);
                    square.mContainsMine = true;
                } while (!mMineSquares.add(square));
            }

            // Place mines
            for(BoardSquare square : mMineSquares) {
                mBoardGrid[square.y][square.x] = square;
            }

            if(mNumMines == mMineSquares.size()) {
                mCurrentState = MINES_PLACED;
            }
            else {
                throw new InitializationException("Number of mines specified and actual mines are unequal.");
            }
        }
        else {
            throw new OutOfOrderException("Mine placement must follow grid creation");
        }
    }

    private void calculateNumberedSquares() throws OutOfOrderException {
        if (mCurrentState == MINES_PLACED) {
            for(BoardSquare mine : mMineSquares) {
                int x = mine.x;
                int y = mine.y;

                int startingY = Math.max(0, y - 1);
                int startingX = Math.max(0, x - 1);

                for(int i = startingY; i < mDimension && i <= y + 1; i++) {
                    for(int j = startingX; j < mDimension && j <= x + 1; j++) {

                        BoardSquare square = mBoardGrid[i][j];

                        if(square == null) {
                            mBoardGrid[i][j] = square = new BoardSquare(j, i);
                        }
                        else if(square.doesContainMine()) {
                            // Don't need to calculate adjacent mines count for
                            // squares containing mines.
                            continue;
                        }
                        square.mAdjacentMinesCount++;
                    }
                }
            }
            mCurrentState = GRID_POPULATED;
        }
        else {
            throw new OutOfOrderException("Calculation of numbered squares requires mines to be placed");
        }
    }

    public BoardSquare[][] getBoardGrid() {
        return mBoardGrid;
    }

    public int getDimension() {
        return mDimension;
    }

    public int getNumMines() {
        return mNumMines;
    }

    public static class Builder {
        int mDimension = DEFAULT_DIMENSION;
        int mNumMines = DEFAULT_NUM_MINES;

        public Builder dimension(int dimension) throws InvalidArgumentException {
            if(dimension > 0) {
                mDimension = dimension;
            }
            else {
                throw new InvalidArgumentException("Dimension must be greater than 0.");
            }
            return this;
        }

        public Builder numMines(int numMines) throws InvalidArgumentException {
            if(numMines > 0) {
                mNumMines = numMines;
            }
            else {
                throw new InvalidArgumentException("Mine count must be greater than 0.");
            }
            return this;
        }

        public Board build() throws InvalidArgumentException, OutOfOrderException, InitializationException {
            if(mDimension > 0 && mNumMines > 0) {
                Board board = new Board(mDimension, mNumMines);
                board.init();

                return board;
            }
            else {
                throw new InvalidArgumentException("Dimension and mine count must be greater than 0.");
            }
        }
    }
}
