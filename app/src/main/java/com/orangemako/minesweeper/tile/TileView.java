package com.orangemako.minesweeper.tile;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.LevelListDrawable;
import android.view.View;

import com.orangemako.minesweeper.MinesweeperApplication;
import com.orangemako.minesweeper.R;
import com.orangemako.minesweeper.exceptions.InvalidArgumentException;
import com.orangemako.minesweeper.game.Game;
import com.orangemako.minesweeper.utilities.GraphicsUtils;
import com.squareup.otto.Bus;

import java.util.HashMap;
import java.util.Map;

public class TileView extends View {
    // Board Square states
    public static final int COVERED = 0;
    public static final int FLAGGED_AS_MINE = 1;
    public static final int UNCOVERED = 2;

    // User gestures
    public static final int CLICK = 0;
    public static final int LONG_CLICK = 1;

    private LevelListDrawable mDrawableContainer;
    private int mXGridCoordinate;
    private int mYGridCoordinate;

    private Bus mGameBus;

    static Map<Integer, Integer> sAdjacentMineCountToColorMap = new HashMap<>();

    // Colors for adjacent mines count
    static {
        sAdjacentMineCountToColorMap.put(1, Color.RED);
        sAdjacentMineCountToColorMap.put(2, Color.BLUE);
        sAdjacentMineCountToColorMap.put(3, Color.GREEN);
        sAdjacentMineCountToColorMap.put(4, Color.DKGRAY);
        sAdjacentMineCountToColorMap.put(5, Color.MAGENTA);
        sAdjacentMineCountToColorMap.put(6, Color.CYAN);
        sAdjacentMineCountToColorMap.put(7, Color.YELLOW);
        sAdjacentMineCountToColorMap.put(8, Color.RED);
    }

    public TileView(Context context, int xGridCoordinate, int yGridCoordinate) throws InvalidArgumentException {
        super(context);

        mXGridCoordinate = xGridCoordinate;
        mYGridCoordinate = yGridCoordinate;

        init();
    }

    private void init() throws InvalidArgumentException {
        mGameBus = MinesweeperApplication.getGameBus();

        setupDrawableBackgrounds();
        setupListeners();
    }

    private void setupListeners() {
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mGameBus.post(new Game.TileViewActionEvent(TileView.this, CLICK));
            }
        });

        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mGameBus.post(new Game.TileViewActionEvent(TileView.this, LONG_CLICK));

                // Return true to consume event.
                return true;
            }
        });
    }

    private void setupDrawableBackgrounds() throws InvalidArgumentException {
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
                    textColor = sAdjacentMineCountToColorMap.get(mAdjacentMineCount);
                    adjacentMineCountText = String.valueOf(mAdjacentMineCount);
                }
            }

            uncoveredDrawable = new TextDrawable(adjacentMineCountText, textColor);
        }
        return uncoveredDrawable;
    }

    public int getXGridCoordinate() {
        return mXGridCoordinate;
    }

    public int getYGridCoordinate() {
        return mYGridCoordinate;
    }

    public int getAdjacentMineCount() {
        return mAdjacentMineCount;
    }

    public int getState() {
        return mDrawableContainer.getLevel();
    }

    public void setState(int state) {
        mDrawableContainer.setLevel(state);
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
