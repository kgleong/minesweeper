package com.orangemako.minesweeper.game;

import android.util.Log;

import com.orangemako.minesweeper.board.Board;
import com.orangemako.minesweeper.board.BoardLayoutView;
import com.orangemako.minesweeper.exceptions.InitializationException;
import com.orangemako.minesweeper.exceptions.InvalidArgumentException;

public class GameManager{
    private Listener mListener;
    private BoardLayoutView mBoardLayoutView;
    private Game mGame;

    public GameManager(int dimension, int numMines, BoardLayoutView boardLayoutView, Listener listener) throws
            InvalidArgumentException, InitializationException {

        mBoardLayoutView = boardLayoutView;
        mListener = listener;

        initGame(dimension, numMines);
    }

    public void initGame(int dimension, int numMines) throws InvalidArgumentException, InitializationException {
        // Ensure that old games don't receive game events.
        if(mGame != null) {
           mGame.unregisterFromEventBus();
        }

        // Pass a new Board with new mines placement to a new Game.
        Board board = new Board.Builder().dimension(dimension).numMines(numMines).build();
        mGame = new Game(this, board);

        // The BoardLayoutView posts events to the Game during setup, so there must
        // be an initialized Game before setup occurs.
        mBoardLayoutView.setupBoard(board);
    }

    public void publishWin() {
        mListener.onWin();
    }

    public void publishLoss() {
        mListener.onLoss();
    }

    public void publishFlagsRemainingCount(int flagsRemaining) {
        mListener.updateMineFlagsRemainingCount(flagsRemaining);
    }

    public void publishElapsedTime(long elapsedTime) {
        mListener.updateTimeElapsed(elapsedTime);
    }

    // Delegate methods to Game object
    public void finishGame() {
        mGame.finishGame();
    }

    public void startTimer() {
        mGame.startTimer();
    }

    public void stopTimer() {
        mGame.stopTimer();
    }

    public long getElapsedTime() {
        return mGame.getElapsedTime();
    }

    public int getMineFlagsRemainingCount() {
        return mGame.getMineFlagsRemainingCount();
    }

    // End delegated methods
    public interface Listener {
        void updateTimeElapsed(long elapsedTime);
        void updateMineFlagsRemainingCount(int flagsRemaining);
        void onLoss();
        void onWin();
    }

    public static class Builder {
        public static final String TAG = Builder.class.getName();

        int mDimension = Board.DEFAULT_DIMENSION;
        int mNumMines = Board.DEFAULT_NUM_MINES;
        Listener mListener;
        BoardLayoutView mBoardLayoutView;

        public Builder dimension(int dimension) throws InvalidArgumentException {
            if(dimension > 0) {
                mDimension = dimension;
            }
            else {
                Log.e(TAG, "Dimension must be greater than 0");
                throw new InvalidArgumentException();
            }

            return this;
        }

        public Builder numMines(int numMines) throws InvalidArgumentException {
            if(numMines > 0) {
                mNumMines = numMines;
            }
            else {
                Log.e(TAG, "Number of mines must be greater than 0");
                throw new InvalidArgumentException();
            }
            return this;
        }

        public Builder listener(Listener listener) {
            mListener = listener;

            return this;
        }

        public Builder boardLayotuView(BoardLayoutView boardLayoutView) {
            mBoardLayoutView = boardLayoutView;

            return this;
        }

        public GameManager build() throws InitializationException, InvalidArgumentException {
            if(mListener == null || mBoardLayoutView == null) {
                Log.e(this.getClass().getName(), "Game manager listener and board layout view required");
                throw new InitializationException();
            }
            else {
                return new GameManager(mDimension, mNumMines, mBoardLayoutView, mListener);
            }
        }
    }
}
