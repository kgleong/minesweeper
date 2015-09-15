package com.orangemako.minesweeper.drawable;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.List;

public class ConcentricCirclesDrawable extends Drawable {
    static final float DEFAULT_FILL_PERCENT = 0.55f;
    static final int DEFAULT_OUTER_RING_COLOR = Color.GREEN;
    static final int DEFAULT_INNER_RING_COLOR = Color.YELLOW;

    private float mfillPercent = DEFAULT_FILL_PERCENT;
    private List<Paint> mRingPaintList = new ArrayList<>();

    private ConcentricCirclesDrawableState mDrawableState;

    public ConcentricCirclesDrawable() {
        this(null, null);
    }

    /**
     * Creates concentric circles using the supplied color list.
     *
     * @param ringColorList list of colors assigned from the outside
     *                      ring (index 0) to the center ring (index n)
     * @param fillPercent percent of space this drawable should take up within its bounds.
     */
    public ConcentricCirclesDrawable(int[] ringColorList, Float fillPercent) {
        if(ringColorList == null) {
            ringColorList = new int[]{DEFAULT_OUTER_RING_COLOR, DEFAULT_INNER_RING_COLOR};
        }

        if(fillPercent != null) {
            mfillPercent = fillPercent;
        }

        setupDrawObjects(ringColorList);
        saveConstantState(ringColorList);
    }

    private void saveConstantState(int[] ringColorList) {
        if(mDrawableState == null) {
            mDrawableState = new ConcentricCirclesDrawableState();
            mDrawableState.mfillPercent = mfillPercent;
            mDrawableState.mRingColorList = ringColorList;
        }
    }

    private void setupDrawObjects(int[] ringColorList) {
        for (int ringColor : ringColorList) {
            Paint ringPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            ringPaint.setStyle(Paint.Style.FILL);
            ringPaint.setColor(ringColor);

            mRingPaintList.add(ringPaint);
        }
    }

    @Override
    public void draw(Canvas canvas) {
        Rect bounds = getBounds();

        float interval = mfillPercent/ mRingPaintList.size();

        for(int i = 0; i < mRingPaintList.size(); i++) {
            Paint ringPaint = mRingPaintList.get(i);
            drawCenteredCircle(bounds, mfillPercent - (i * interval), canvas, ringPaint);
        }
    }

    private void drawCenteredCircle(Rect bounds, float radiusPercentage, Canvas canvas, Paint paint) {
        float radius = Math.min(bounds.height(), bounds.width()) * radiusPercentage / 2;
        canvas.drawCircle(bounds.centerX(), bounds.centerY(), radius, paint);
    }

    @Override
    public void setAlpha(int alpha) {
        for(Paint paint : mRingPaintList) {
            paint.setAlpha(alpha);
        }
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        for(Paint paint : mRingPaintList) {
            paint.setColorFilter(colorFilter);
        }
    }

    @Override
    public int getOpacity() {
        return 0;
    }

    @Override
    public ConstantState getConstantState() {
        return mDrawableState;
    }

    private class ConcentricCirclesDrawableState extends ConstantState {
        private float mfillPercent;
        private int[] mRingColorList;

        @Override
        public Drawable newDrawable() {
            return new ConcentricCirclesDrawable(mRingColorList, mfillPercent);
        }

        @Override
        public int getChangingConfigurations() {
            return 0;
        }
    }
}
