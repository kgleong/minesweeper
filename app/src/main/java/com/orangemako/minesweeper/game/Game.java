package com.orangemako.minesweeper.game;

import android.util.Log;

import com.orangemako.minesweeper.MinesweeperApplication;
import com.orangemako.minesweeper.board.Board;
import com.orangemako.minesweeper.board.BoardSquare;
import com.orangemako.minesweeper.exceptions.InitializationException;
import com.orangemako.minesweeper.board.TileView;
import com.squareup.otto.Subscribe;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class Game {
    private GameManager mGameManager;

    // Board state
    private Board mBoard;
    private BoardSquare[][] mBoardSquaresGrid;
    private TileView[][] mTileViewsGrid;

    // Game state
    private boolean mIsGameFinished = false;
    private long mStartTime;
    private long mElapsedTime = 0;

    private int mMineFlagsRemainingCount;

    public Game(GameManager gameManager, Board board) throws InitializationException {
        if(gameManager != null && board != null) {
            mBoard = board;
            mBoardSquaresGrid = board.getBoardGrid();
            mGameManager = gameManager;

            init();
        }
        else {
            Log.e(this.getClass().getName(), "Game manager and board square grid required.");

            throw new InitializationException();
        }
    }

    private void init() {
        int dimension = mBoard.getDimension();

        mMineFlagsRemainingCount = mBoard.getNumMines();
        mTileViewsGrid = new TileView[dimension][dimension];

        // Register to receive game state change events
        MinesweeperApplication.getGameBus().register(this);

        // Publish initial stats
        mGameManager.publishFlagsRemainingCount(mMineFlagsRemainingCount);
        mGameManager.publishElapsedTime(mElapsedTime);
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

    // Called from the Game Manager
    public void finishGame() {
        boolean didWin = true;

        int dimension = mBoardSquaresGrid.length;

        for(int i = 0; i < dimension; i++) {
            for(int j = 0; j < dimension; j++) {
                TileView tileView = mTileViewsGrid[i][j];
                BoardSquare boardSquare = mBoardSquaresGrid[i][j];

                int state = tileView.getState();
                boolean doesContainMine = boardSquare == null ? false : boardSquare.doesContainMine();

                if(state == TileView.COVERED) {
                    // User did not place a flag over a mine.
                    if(doesContainMine) {
                        didWin = false;
                    }

                    // Uncover all tiles without flags.
                    tileView.setState(TileView.UNCOVERED);
                }
                else if(state == TileView.FLAGGED_AS_MINE) {
                    if(!doesContainMine) {
                        // Uncover flags not covering mines.
                        tileView.setState(TileView.UNCOVERED);
                    }
                }
            }
        }
        publishGameResult(didWin);
    }

    private void publishGameResult(boolean didWin) {
        if(!mIsGameFinished) {
            mIsGameFinished = true;
            mGameManager.publishGameFinished();

            if (didWin) {
                mGameManager.publishWin();
            } else {
                mGameManager.publishLoss();
            }
        }
    }

    public void uncoverAdjacentBlankTileViews(TileView tileView) {
        int x = tileView.getXGridCoordinate();
        int y = tileView.getYGridCoordinate();

        BoardSquare boardSquare = mBoardSquaresGrid[y][x];

        // A null board square indicates no adjacent mines.
        if(boardSquare == null) {
            // Iterative BFS search
            Set<TileView> visited = new HashSet<>();
            Stack<TileView> queue = new Stack<>();

            visited.add(tileView);
            queue.add(tileView);

            int dimension = mBoardSquaresGrid.length;

            while(!queue.empty()) {
                TileView currentTile = queue.pop();

                x = currentTile.getXGridCoordinate();
                y = currentTile.getYGridCoordinate();

                int startingX = Math.max(0, x - 1);
                int startingY = Math.max(0, y - 1);

                for(int i = startingX; i < dimension && i <= x + 1; i++) {
                    for (int j = startingY; j < dimension && j <= y + 1; j++) {
                        TileView adjacentTile = mTileViewsGrid[j][i];
                        BoardSquare adjacentBoardSquare = mBoardSquaresGrid[j][i];

                        boolean added = visited.add(adjacentTile);

                        if(added && adjacentBoardSquare == null) {
                            adjacentTile.setState(TileView.UNCOVERED);
                            queue.add(adjacentTile);
                        }
                    }
                }
            }
        }
    }

    public void unregisterFromEventBus() {
        MinesweeperApplication.getGameBus().unregister(this);
    }

    public long getElapsedTime() {
        // If the timer has been started, then add the time since it was started
        // to the saved elapsed time.
        long additionalRealTime = 0;

        if(mStartTime > 0) {
            additionalRealTime = System.currentTimeMillis() - mStartTime;
        }
        return mElapsedTime + additionalRealTime;
    }

    public int getMineFlagsRemainingCount() {
        return mMineFlagsRemainingCount;
    }

    @Subscribe
    public void onTileCreated(TileViewCreatedEvent event) {
        TileView tileView = event.mTileView;

        int x = tileView.getXGridCoordinate();
        int y = tileView.getYGridCoordinate();

        mTileViewsGrid[y][x] = tileView;

        // Set the uncovered graphic for the TileView.
        tileView.setupUncoveredTileDrawable(mBoardSquaresGrid[y][x]);
    }

    public static class TileViewCreatedEvent {
        TileView mTileView;

        public TileViewCreatedEvent(TileView tileView) {
            mTileView = tileView;
        }
    }

    @Subscribe
    public void onTileViewAction(TileViewActionEvent event) {
        if(!mIsGameFinished) {
            boolean isAllowed = false;

            int action = event.mAction;
            TileView tileView = event.mTileView;

            int state = -1;

            switch (action) {
                // Toggling mine flag on a tile
                case TileView.CLICK:
                    switch(tileView.getState()) {
                        case TileView.COVERED:
                            // Add a flag
                            if(mMineFlagsRemainingCount > 0) {
                                state = TileView.FLAGGED_AS_MINE;
                                isAllowed = true;

                                mGameManager.publishFlagsRemainingCount(--mMineFlagsRemainingCount);
                            }
                            break;
                        case TileView.FLAGGED_AS_MINE:
                            // Remove a flag
                            state = TileView.COVERED;
                            isAllowed = true;

                            mGameManager.publishFlagsRemainingCount(++mMineFlagsRemainingCount);
                            break;
                    }
                    break;

                // Uncovering a tile
                case TileView.LONG_CLICK:
                    if(tileView.getState() == TileView.COVERED) {
                        // Even if a player loses, uncover the tile.
                        state = TileView.UNCOVERED;
                        isAllowed = true;

                        // Get corresponding board square
                        int x = tileView.getXGridCoordinate();
                        int y = tileView.getYGridCoordinate();
                        BoardSquare boardSquare = mBoardSquaresGrid[y][x];

                        // null BoardSquare means no adjacent mines.
                        if(boardSquare == null) {
                            uncoverAdjacentBlankTileViews(tileView);
                        }
                        else {
                            // If tile is over a square that contains a mine, player loses.
                            if(boardSquare.doesContainMine()) {
                                publishGameResult(false);
                            }
                        }
                    }
                    break;
            }

            if (isAllowed) {
                tileView.setState(state);
            }
        }
    }

    public static class TileViewActionEvent {
        TileView mTileView;
        int mAction;

        public TileViewActionEvent(TileView tileView, int action) {
            mTileView = tileView;
            mAction = action;
        }
    }
}
