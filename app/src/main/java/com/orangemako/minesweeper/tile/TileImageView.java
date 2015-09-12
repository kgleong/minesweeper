package com.orangemako.minesweeper.tile;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.LevelListDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;

import com.orangemako.minesweeper.R;
import com.orangemako.minesweeper.exceptions.InvalidArgumentException;
import com.orangemako.minesweeper.utilities.GraphicsUtils;

public class TileImageView extends View {
    // States
    public static final int COVERED = 0;
    public static final int FLAGGED_AS_MINE = 1;
    public static final int UNCOVERED = 2;

    private LevelListDrawable mDrawableContainer;

    public TileImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setupBackgrounds();
        setupListeners();
    }

    private void setupListeners() {
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // Toggle mine flag.  The drawable container level is equivalent to view state.
                switch(mDrawableContainer.getLevel()) {
                    case COVERED:
                        mDrawableContainer.setLevel(FLAGGED_AS_MINE);
                        break;
                    case FLAGGED_AS_MINE:
                        mDrawableContainer.setLevel(COVERED);
                        break;

                }
            }
        });

        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                // Uncover tile.
                switch(mDrawableContainer.getLevel()) {
                    case COVERED:
                        mDrawableContainer.setLevel(UNCOVERED);
                        break;
                    case FLAGGED_AS_MINE:
                        String errorMessage = getContext().getResources().getString(R.string.uncover_tile_error);
                        Toast.makeText(view.getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                        break;
                }

                // Return true to consume event.
                return true;
            }
        });
    }

    private void setupBackgrounds() {
        mDrawableContainer = new LevelListDrawable();

        int colorInner = GraphicsUtils.getColor(getContext(), R.color.blue_grey_200);
        int colorTop = GraphicsUtils.getColor(getContext(), R.color.blue_grey_300);
        int colorLeft = GraphicsUtils.getColor(getContext(), R.color.blue_grey_400);
        int colorBottom = GraphicsUtils.getColor(getContext(), R.color.blue_grey_500);
        int colorRight = GraphicsUtils.getColor(getContext(), R.color.blue_grey_600);

        int[] tileColors = new int[]{colorInner, colorLeft, colorTop, colorRight, colorBottom};

        try {
            mDrawableContainer.addLevel(0, COVERED, new BeveledTileDrawable(tileColors));
        } catch (InvalidArgumentException e) {
            String errorMessage = getContext().getResources().getString(R.string.board_initialization_error);
            Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
        }

        mDrawableContainer.addLevel(0, FLAGGED_AS_MINE, new ConcentricCirclesDrawable(getContext()));
        mDrawableContainer.addLevel(0, UNCOVERED, new TextDrawable("5", Color.RED));
        setBackground(mDrawableContainer);
    }
}
