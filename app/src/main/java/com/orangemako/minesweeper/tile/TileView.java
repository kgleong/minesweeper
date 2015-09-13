package com.orangemako.minesweeper.tile;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.LevelListDrawable;
import android.view.View;
import android.widget.Toast;

import com.orangemako.minesweeper.R;
import com.orangemako.minesweeper.board.BoardSquare;
import com.orangemako.minesweeper.exceptions.InvalidArgumentException;
import com.orangemako.minesweeper.game.Game;
import com.orangemako.minesweeper.utilities.GraphicsUtils;

import java.util.HashMap;
import java.util.Map;

public class TileView extends View {
    // Board Square states
    public static final int COVERED = 0;
    public static final int FLAGGED_AS_MINE = 1;
    public static final int UNCOVERED = 2;

    private TileViewParent mParent;
    private LevelListDrawable mDrawableContainer;
    private BoardSquare mBoardSquare;
    private TileViewListener mListener;
    private Game mGame;
    private boolean mDoesContainMine;
    private int mXCoordinate;
    private int mYCoordinate;
    private int mAdjacentMineCount;

    static Map<Integer, Integer> sMineCountToColorMap = new HashMap<>();

    static {
        sMineCountToColorMap.put(1, Color.RED);
        sMineCountToColorMap.put(2, Color.BLUE);
        sMineCountToColorMap.put(3, Color.GREEN);
        sMineCountToColorMap.put(4, Color.DKGRAY);
        sMineCountToColorMap.put(5, Color.MAGENTA);
        sMineCountToColorMap.put(6, Color.CYAN);
        sMineCountToColorMap.put(7, Color.YELLOW);
        sMineCountToColorMap.put(8, Color.RED);
    }

    public TileView(Context context, TileViewParent parent, Game game, int x, int y) throws InvalidArgumentException {
        super(context);

        mParent = parent;
        mListener = game;
        mGame = game;
        mBoardSquare = game.getBoard().getBoardGrid()[y][x];
        mXCoordinate = x;
        mYCoordinate = y;
        setDoesContainMine();
        setAdjacentMineCount();

        setupBackgrounds();
        setupListeners();
    }

    private void setDoesContainMine() {
        if(mBoardSquare != null) {
            mDoesContainMine = mBoardSquare.doesContainMine();
        }
        else {
            mDoesContainMine = false;
        }
    }

    private void setAdjacentMineCount() {
        if(mBoardSquare == null) {
            mAdjacentMineCount = 0;
        }
        else {
            mAdjacentMineCount = mBoardSquare.getAdjacentMinesCount();
        }
    }

    private void setupListeners() {
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mGame.isGameEnded()) {
                    // Toggle mine flag.  The drawable container level is equivalent to view state.
                    switch (mDrawableContainer.getLevel()) {
                        case COVERED:
                            if (mListener.flagTileRequested()) {
                                mDrawableContainer.setLevel(FLAGGED_AS_MINE);
                            } else {
                                String errorMessage = getContext().getResources().getString(R.string.over_flag_limit_error);
                                Toast.makeText(view.getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                            }
                            break;

                        case FLAGGED_AS_MINE:
                            mListener.unflagTileRequested();
                            mDrawableContainer.setLevel(COVERED);
                    }
                }
            }
        });

        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (!mGame.isGameEnded()) {
                    // Uncover tile.
                    switch (mDrawableContainer.getLevel()) {
                        case COVERED:
                            mListener.uncoverTileRequested(mDoesContainMine);

                            if (mDoesContainMine) {
                                mGame.setIsGameEnded(true);
                            }
                            else if (TileView.this.mAdjacentMineCount == 0) {
                                 mParent.uncoverAdjacentBlankTiles(TileView.this);
                            }

                            mDrawableContainer.setLevel(UNCOVERED);

                            break;
                        case FLAGGED_AS_MINE:
                            String errorMessage = getContext().getResources().getString(R.string.uncover_tile_error);
                            Toast.makeText(view.getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                    }
                }

                // Return true to consume event.
                return true;
            }
        });
    }

    private void setupBackgrounds() throws InvalidArgumentException {
        Drawable coveredTile = setupCoveredTile();
        Drawable uncoveredTile = setupUncoveredTile();
        LayerDrawable flaggedMineDrawable = new LayerDrawable(new Drawable[]{coveredTile, new ConcentricCirclesDrawable()});

        mDrawableContainer = new LevelListDrawable();
        mDrawableContainer.addLevel(0, COVERED, coveredTile);
        mDrawableContainer.addLevel(0, FLAGGED_AS_MINE, flaggedMineDrawable);
        mDrawableContainer.addLevel(0, UNCOVERED, uncoveredTile);

        setBackground(mDrawableContainer);
    }

    private Drawable setupCoveredTile() throws InvalidArgumentException {
        // TODO: Move this to a theme
        int colorInner = GraphicsUtils.getColor(getContext(), R.color.blue_grey_200);
        int colorTop = GraphicsUtils.getColor(getContext(), R.color.blue_grey_300);
        int colorLeft = GraphicsUtils.getColor(getContext(), R.color.blue_grey_400);
        int colorBottom = GraphicsUtils.getColor(getContext(), R.color.blue_grey_500);
        int colorRight = GraphicsUtils.getColor(getContext(), R.color.blue_grey_600);

        int[] tileColors = new int[]{colorInner, colorLeft, colorTop, colorRight, colorBottom};

        return new BeveledTileDrawable(tileColors);
    }

    private Drawable setupUncoveredTile() {
        Drawable uncoveredDrawable;

        if(mDoesContainMine) {
            uncoveredDrawable = new ConcentricCirclesDrawable(new int[]{Color.RED, Color.BLACK}, 0.50f);
        }
        else {
            String adjacentMineCountText = "";
            int textColor = 0;

            if(mBoardSquare != null) {
                if(mAdjacentMineCount > 0) {
                    textColor = sMineCountToColorMap.get(mAdjacentMineCount);
                    adjacentMineCountText = String.valueOf(mAdjacentMineCount);
                }
            }

            uncoveredDrawable = new TextDrawable(adjacentMineCountText, textColor);
        }
        return uncoveredDrawable;
    }

    public int getXCoordinate() {
        return mXCoordinate;
    }

    public int getYCoordinate() {
        return mYCoordinate;
    }

    public int getAdjacentMineCount() {
        return mAdjacentMineCount;
    }

    public int getState() {
        return mDrawableContainer.getLevel();
    }

    public boolean doesContainMine() {
        return mDoesContainMine;
    }

    public LevelListDrawable getDrawableContainer() {
        return mDrawableContainer;
    }

    public interface TileViewListener {
        void uncoverTileRequested(boolean doesContainMine);
        boolean flagTileRequested();
        void unflagTileRequested();
    }

    public interface TileViewParent {
        void uncoverAdjacentBlankTiles(TileView tileView);
    }
}
