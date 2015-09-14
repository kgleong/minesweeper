package com.orangemako.minesweeper.board;

import android.util.Log;

import com.orangemako.minesweeper.exceptions.InitializationException;
import com.orangemako.minesweeper.exceptions.InvalidArgumentException;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Board {
    public static final int DEFAULT_DIMENSION = 8;
    public static final int DEFAULT_NUM_MINES = 10;
    static final String TAG = Board.class.getName();

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

    private void init() throws InitializationException {
        initBoardGrid();
        initAndPlaceMines();
        calculateNumberedSquares();
    }

    private void initBoardGrid() throws InitializationException {
        if(mCurrentState == BOARD_CREATED) {
            mBoardGrid = new BoardSquare[mDimension][mDimension];
            mCurrentState = GRID_CREATED;
        }
        else {
            Log.e(TAG, "Grid creation must follow board creation.");
            throw new InitializationException();
        }
    }

    private void initAndPlaceMines() throws InitializationException {
        if(mCurrentState == GRID_CREATED) {
            mMineSquares = new HashSet<>(mNumMines);

            // Create mines
            for (int i = 0; i < mNumMines; i++) {
                BoardSquare square;

                do {
                    // Randomly assign mines
                    int x = new Random().nextInt(mDimension);
                    int y = new Random().nextInt(mDimension);

                    square = new BoardSquare(x, y);
                    square.setContainsMine(true);
                } while (!mMineSquares.add(square));
            }

            // Place mines
            for(BoardSquare square : mMineSquares) {
                mBoardGrid[square.getXGridCoordinate()][square.getYGridCoordinate()] = square;
            }

            if(mNumMines == mMineSquares.size()) {
                mCurrentState = MINES_PLACED;
            }
            else {
                Log.e(TAG, "Number of mines specified and actual mines are unequal.");
                throw new InitializationException();
            }
        }
        else {
            Log.e(TAG, "Mine placement must follow grid creation");
            throw new InitializationException();
        }
    }

    private void calculateNumberedSquares() throws InitializationException {
        if (mCurrentState == MINES_PLACED) {
            for(BoardSquare mine : mMineSquares) {
                int x = mine.getXGridCoordinate();
                int y = mine.getYGridCoordinate();

                int startingX = Math.max(0, x - 1);
                int startingY = Math.max(0, y - 1);

                for(int i = startingX; i < mDimension && i <= x + 1; i++) {
                    for(int j = startingY; j < mDimension && j <= y + 1; j++) {

                        BoardSquare square = mBoardGrid[i][j];

                        if(square == null) {
                            mBoardGrid[i][j] = square = new BoardSquare(i, j);
                        }
                        else if(square.doesContainMine()) {
                            // Don't need to calculate adjacent mines count for
                            // squares containing mines.
                            continue;
                        }
                        int newAdjacentMinesCount = square.getAdjacentMinesCount() + 1;
                        square.setAdjacentMinesCount(newAdjacentMinesCount);
                    }
                }
            }
            mCurrentState = GRID_POPULATED;
        }
        else {
            Log.e(TAG, "Calculation of numbered squares requires mines to be placed");
            throw new InitializationException();
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
        public static final String TAG = Builder.class.getName();

        int mDimension = DEFAULT_DIMENSION;
        int mNumMines = DEFAULT_NUM_MINES;

        public Builder dimension(int dimension) throws InvalidArgumentException {
            if(dimension > 0) {
                mDimension = dimension;
            }
            else {
                Log.e(TAG, "Dimension must be greater than 0.");
                throw new InvalidArgumentException();
            }
            return this;
        }

        public Builder numMines(int numMines) throws InvalidArgumentException {
            if(numMines > 0) {
                mNumMines = numMines;
            }
            else {
                Log.e(TAG, "Mine count must be greater than 0.");
                throw new InvalidArgumentException();
            }
            return this;
        }

        public Board build() throws InitializationException {
            Board board = new Board(mDimension, mNumMines);
            board.init();

            return board;
        }
    }
}
