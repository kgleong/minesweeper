package com.orangemako.minesweeper.game;

import com.orangemako.minesweeper.board.Board;
import com.orangemako.minesweeper.exceptions.InitializationException;
import com.orangemako.minesweeper.exceptions.InvalidArgumentException;
import com.orangemako.minesweeper.exceptions.OutOfOrderException;

public class Game {
    long mStartTime;

    long mTotalTime = 0;

    // TODO: delegate methods like getDimension to mBoard so
    // associated objects will only need a reference to a Game object.
    Board mBoard;

    public Game(int dimension, int numMines) throws
            InvalidArgumentException,  OutOfOrderException, InitializationException {

        this.mBoard =
            new Board.Builder()
                .dimension(dimension)
                .numMines(numMines).build();

        startTimer();
    }

    public Board getBoard() {
        return mBoard;
    }

    public void startTimer() {
        mStartTime = System.currentTimeMillis();
    }

    public void stopTimer() {
        if(mStartTime > 0) {
            mTotalTime += System.currentTimeMillis() - mStartTime;

            // Reset timer
            mStartTime = 0;
        }
    }

    public long getTotalTime() {
        return mTotalTime;
    }
}
