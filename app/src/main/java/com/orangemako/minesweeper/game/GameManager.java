package com.orangemako.minesweeper.game;

import android.util.Log;

import com.orangemako.minesweeper.board.Board;
import com.orangemako.minesweeper.board.BoardLayoutView;
import com.orangemako.minesweeper.exceptions.InitializationException;
import com.orangemako.minesweeper.exceptions.InvalidArgumentException;
import com.orangemako.minesweeper.tile.TileView;

public class GameManager implements TileView.TileViewListener {
    private static boolean mIsGameEnded = false;

    private long mStartTime;
    private int mElapsedTime = 0;
    private GameManagerListener mListener;

    // GameManager state
    private int mFlagsRemaining;

    // TODO: delegate methods like getDimension to mBoard so
    // associated objects will only need a reference to a GameManager object.
    Board mBoard;

    public GameManager(int dimension, int numMines, GameManagerListener listener, BoardLayoutView boardLayoutView) throws
            InvalidArgumentException,  OutOfOrderException, InitializationException {


        mFlagsRemaining = mBoard.getNumMines();
        mListener = listener;
        mListener.updateFlagsRemainingCount(mFlagsRemaining);
        mListener.updateTimeElapsed(mElapsedTime);

        startTimer();
    }

    private void init(int dimension, int numMines) throws InvalidArgumentException, OutOfOrderException, InitializationException {
        Board board = new Board.Builder().dimension(dimension).numMines(numMines).build();
    }

    public void publishWin() {
        mListener.onWin();
    }

    public void publishLoss() {
        mListener.onLoss();
    }

    public Board getBoard() {
        return mBoard;
    }

    public void startTimer() {
        mStartTime = System.currentTimeMillis();
    }

    public void stopTimer() {
        if(mStartTime > 0) {
            mElapsedTime += System.currentTimeMillis() - mStartTime;

            // Reset timer
            mStartTime = 0;
        }
    }

    public boolean isGameEnded() {
        return mIsGameEnded;
    }

    public static void setIsGameEnded(boolean isGameEnded) {
        GameManager.mIsGameEnded = mIsGameEnded;
    }

    public int getElapsedTime() {
        return mElapsedTime;
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

    public interface GameManagerListener {
        void updateTimeElapsed(int elapsedTime);
        void updateFlagsRemainingCount(int flagsRemaining);
        void onLoss();
        void onWin();
    }

    public static class Builder {
        int mDimension = Board.DEFAULT_DIMENSION;
        int mNumMines = Board.DEFAULT_NUM_MINES;
        GameManagerListener mGameManagerListener;
        BoardLayoutView mBoardLayoutView;

        public GameManager build() throws InitializationException, InvalidArgumentException {
            if(mGameManagerListener == null || mBoardLayoutView == null) {
                Log.e(this.getClass().getName(), "Game manager listener and board layout view required");
                throw new InitializationException();
            }
            else {
                return new GameManager(mDimension, mNumMines, mGameManagerListener, mBoardLayoutView);
            }
        }
    }
}
