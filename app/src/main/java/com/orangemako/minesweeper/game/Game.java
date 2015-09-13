package com.orangemako.minesweeper.game;

import com.orangemako.minesweeper.board.Board;
import com.orangemako.minesweeper.exceptions.InitializationException;
import com.orangemako.minesweeper.exceptions.InvalidArgumentException;
import com.orangemako.minesweeper.exceptions.OutOfOrderException;
import com.orangemako.minesweeper.tile.TileView;

public class Game implements TileView.TileViewListener {
    private long mStartTime;
    private int mTotalTime = 0;
    private GameListener mListener;

    // Game state
    private int mFlagsRemaining;

    // TODO: delegate methods like getDimension to mBoard so
    // associated objects will only need a reference to a Game object.
    Board mBoard;

    public Game(int dimension, int numMines, GameListener listener) throws
            InvalidArgumentException,  OutOfOrderException, InitializationException {

        this.mBoard =
            new Board.Builder()
                .dimension(dimension)
                .numMines(numMines).build();

        mFlagsRemaining = mBoard.getNumMines();
        mListener = listener;
        mListener.updateFlagsRemainingCount(mFlagsRemaining);
        mListener.updateTimeElapsed(mTotalTime);

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

    @Override
    public void uncoverTileRequested(boolean doesContainMine) {
        if(doesContainMine) {
            mListener.onLoss();
        }
    }

    @Override
    public boolean flagTileRequested() {
        boolean reply = false;

        if(mFlagsRemaining > 0) {
            reply = true;
            mListener.updateFlagsRemainingCount(--mFlagsRemaining);
        }
        return reply;
    }

    @Override
    public void unflagTileRequested() {
        mListener.updateFlagsRemainingCount(++mFlagsRemaining);
    }

    public interface GameListener {
        void updateTimeElapsed(int totalTime);
        void updateFlagsRemainingCount(int flagsRemaining);
        void onLoss();
        void onWin();
    }
}
