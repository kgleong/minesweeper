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
    private int[] mRingColorList;
    private Paint mPaint;

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
            mRingColorList = new int[]{DEFAULT_OUTER_RING_COLOR, DEFAULT_INNER_RING_COLOR};
        }
        else {
            mRingColorList = ringColorList;
        }

        if(fillPercent != null) {
            mfillPercent = fillPercent;
        }

        setupDrawObjects();
        saveConstantState();
    }

    private void saveConstantState() {
        if(mDrawableState == null) {
            mDrawableState = new ConcentricCirclesDrawableState();
            mDrawableState.mfillPercent = mfillPercent;
            mDrawableState.mRingColorList = mRingColorList;
        }
    }

    private void setupDrawObjects() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    public void draw(Canvas canvas) {
        Rect bounds = getBounds();

        float interval = mfillPercent / mRingColorList.length;

        for(int i = 0; i < mRingColorList.length; i++) {
            mPaint.setColor(mRingColorList[i]);
            drawCenteredCircle(bounds, mfillPercent - (i * interval), canvas, mPaint);
        }
    }

    private void drawCenteredCircle(Rect bounds, float radiusPercentage, Canvas canvas, Paint paint) {
        float radius = Math.min(bounds.height(), bounds.width()) * radiusPercentage / 2;
        canvas.drawCircle(bounds.centerX(), bounds.centerY(), radius, paint);
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
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
