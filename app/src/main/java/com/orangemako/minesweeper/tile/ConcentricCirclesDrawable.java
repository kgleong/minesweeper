package com.orangemako.minesweeper.tile;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import com.orangemako.minesweeper.R;

import java.util.ArrayList;
import java.util.List;

public class ConcentricCirclesDrawable extends Drawable {
    static final float DEFAULT_FILL_PERCENT = 0.65f;

    private Context mContext;

    private float mfillPercent = DEFAULT_FILL_PERCENT;
    private List<Paint> mRingPaintList = new ArrayList<>();

    public ConcentricCirclesDrawable(Context context) {
        this(context, null, null);
    }

    /**
     * Creates concentric circles using the supplied color list.
     *
     * @param context
     * @param ringColorList list of colors assigned from the outside
     *                      ring (index 0) to the center ring (index n)
     * @param fillPercent percent of space this drawable should take up within its bounds.
     */
    public ConcentricCirclesDrawable(Context context, int[] ringColorList, Float fillPercent) {
        this.mContext = context;

        if(ringColorList == null) {
            // Assign default colors
            int defaultOuterRingColor = context.getResources().getColor(R.color.light_blue_500);
            int defaultInnerRingColor = context.getResources().getColor(R.color.light_blue_300);

            ringColorList = new int[]{defaultOuterRingColor, defaultInnerRingColor};
        }

        if(fillPercent != null) {
            mfillPercent = fillPercent;
        }

        setupDrawObjects(ringColorList);
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
}
