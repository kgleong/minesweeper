package com.orangemako.minesweeper.game;

import android.util.Log;

import com.orangemako.minesweeper.MinesweeperApplication;
import com.orangemako.minesweeper.board.BoardSquare;
import com.orangemako.minesweeper.exceptions.InitializationException;
import com.orangemako.minesweeper.tile.TileView;
import com.squareup.otto.Subscribe;

public class GameState {
    private Game mGame;

    private BoardSquare[][] mBoardSquaresGrid;
    private TileView[][] mTileViewsGrid;

    private boolean mIsGameFinished = false;

    public GameState(Game game, BoardSquare[][] boardSquaresGrid) {
        mBoardSquaresGrid = boardSquaresGrid;
        mGame = game;

        init();
    }

    private void init() {
        int dimension = mBoardSquaresGrid.length;
        mTileViewsGrid = new TileView[dimension][dimension];

        // Register to receive game state change events
        MinesweeperApplication.getGameStateBus().register(this);
    }

    @Subscribe
    public boolean onTileStateChanged(TileStateChangedEvent event) {
        boolean isAllowed = false;

        return isAllowed;
    }

    public boolean finishGame() {
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
                    tileView.getDrawableContainer().setLevel(TileView.UNCOVERED);

                    if(!didWin){
                        break;
                    }
                }
            }
        }
        mIsGameFinished = true;

        return didWin;
    }

    public class Builder {
        private Game mGame;
        private BoardSquare[][] mBoardSquaresGrid;

        public Builder game(Game game) {
            mGame = game;

            return this;
        }

        public Builder boardSquaresGrid(BoardSquare[][] boardSquaresGrid) {
            mBoardSquaresGrid = boardSquaresGrid;

            return this;
        }

        public GameState build() throws InitializationException {
            GameState newInstance;
            if(mGame != null && mBoardSquaresGrid != null) {
                newInstance = new GameState(mGame, mBoardSquaresGrid);
            }
            else {
                Log.e(this.getClass().getName(), "Game and board square grid required.");

                throw new InitializationException();
            }
            return newInstance;
        }
    }

    public class TileStateChangedEvent {
        int xCoordinate;
        int yCoordinate;
        int newState;
    }
}
