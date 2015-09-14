package com.orangemako.minesweeper.game;

import android.util.Log;

import com.orangemako.minesweeper.MinesweeperApplication;
import com.orangemako.minesweeper.board.Board;
import com.orangemako.minesweeper.board.BoardSquare;
import com.orangemako.minesweeper.exceptions.InitializationException;
import com.orangemako.minesweeper.tile.TileView;
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
    private int mMineFlagsRemaining;

    public Game(GameManager gameManager, Board board) throws InitializationException {
        if(mGameManager != null && mBoardSquaresGrid != null) {
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

        mMineFlagsRemaining = mBoard.getNumMines();
        mTileViewsGrid = new TileView[dimension][dimension];

        // Register to receive game state change events
        MinesweeperApplication.getGameBus().register(this);
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

                if(tileView.getState() == TileView.COVERED) {

                    // User did not place a flag over a mine.
                    if(boardSquare.doesContainMine()) {
                        didWin = false;
                    }

                    // Uncover all tiles without flags.
                    tileView.setState(TileView.UNCOVERED);
                }
            }
        }
        publishGameResult(didWin);
    }

    private void publishGameResult(boolean didWin) {
        if(!mIsGameFinished) {
            mIsGameFinished = true;

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

        BoardSquare boardSquare = mBoardSquaresGrid[x][y];

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
                        TileView adjacentTile = mTileViewsGrid[i][j];
                        BoardSquare adjacentBoardSquare = mBoardSquaresGrid[i][j];

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

    public long getElapsedTime() {
        return mElapsedTime;
    }

    @Subscribe
    public void onTileCreated(TileViewCreatedEvent event) {
        TileView tileView = event.mTileView;

        int x = tileView.getXGridCoordinate();
        int y = tileView.getYGridCoordinate();

        mTileViewsGrid[x][y] = tileView;

        // Set the uncovered graphic for the TileView.
        tileView.setupUncoveredTileDrawable(mBoardSquaresGrid[x][y]);
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
                            if(mMineFlagsRemaining < mBoard.getNumMines()) {
                                state = TileView.FLAGGED_AS_MINE;
                                isAllowed = true;

                                mGameManager.publishFlagsRemainingCount(--mMineFlagsRemaining);
                            }
                            break;
                        case TileView.FLAGGED_AS_MINE:
                            // Remove a flag
                            state = TileView.COVERED;
                            isAllowed = true;

                            mGameManager.publishFlagsRemainingCount(++mMineFlagsRemaining);
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
                        BoardSquare boardSquare = mBoardSquaresGrid[x][y];

                        // If tile is over a square that contains a mine, player loses.
                        if(boardSquare.doesContainMine()) {
                            publishGameResult(false);
                        }
                        else if(boardSquare.getAdjacentMinesCount() == 0){
                            uncoverAdjacentBlankTileViews(tileView);
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
