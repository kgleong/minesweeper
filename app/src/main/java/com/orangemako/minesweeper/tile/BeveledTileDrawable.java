package com.orangemako.minesweeper.tile;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import com.orangemako.minesweeper.exceptions.InvalidArgumentException;

import java.util.Arrays;
import java.util.List;

public class BeveledTileDrawable extends Drawable {
    static final float DEFAULT_FILL_PERCENT = 0.75f;
    static final int INNER_RECT_COLOR_INDICE = 0;
    static final int LEFT_BEVEL_COLOR_INDICE = 1;
    static final int TOP_BEVEL_COLOR_INDICE = 2;
    static final int RIGHT_BEVEL_COLOR_INDICE = 3;
    static final int BOTTOM_BEVEL_COLOR_INDICE = 4;
    static final int REQUIRED_COLOR_COUNT = 5;

    private float mFillPercent = DEFAULT_FILL_PERCENT;

    private int[] mColorList;
    private Paint mInnerRectPaint;
    private Paint mTopPaint;
    private Paint mLeftPaint;
    private Paint mBottomPaint;
    private Paint mRightPaint;

    private Path mTopBevelPath;
    private Path mLeftBevelPath;
    private Path mBottomBevelPath;
    private Path mRighBevelPath;

    public BeveledTileDrawable(int[] colorList) throws InvalidArgumentException {
        if(colorList.length != REQUIRED_COLOR_COUNT) {
            throw new InvalidArgumentException("Must provide 5 colors.");
        }

        mColorList = colorList;
        setupDrawObjects();
    }

    private void setupDrawObjects() {
        mInnerRectPaint = new Paint();
        mInnerRectPaint.setColor(mColorList[INNER_RECT_COLOR_INDICE]);

        mLeftPaint = new Paint();
        mLeftPaint.setColor(mColorList[LEFT_BEVEL_COLOR_INDICE]);

        mTopPaint = new Paint();
        mTopPaint.setColor(mColorList[TOP_BEVEL_COLOR_INDICE]);

        mRightPaint = new Paint();
        mRightPaint.setColor(mColorList[RIGHT_BEVEL_COLOR_INDICE]);

        mBottomPaint = new Paint();
        mBottomPaint.setColor(mColorList[BOTTOM_BEVEL_COLOR_INDICE]);

        List<Paint> paintList = Arrays.asList(
                mInnerRectPaint,
                mTopPaint,
                mLeftPaint,
                mBottomPaint,
                mRightPaint);

        for(Paint paint : paintList) {
            paint.setStyle(Paint.Style.FILL);
            paint.setAntiAlias(true);
        }

        mTopBevelPath = new Path();
        mLeftBevelPath = new Path();
        mBottomBevelPath = new Path();
        mRighBevelPath = new Path();
    }


    @Override
    public void draw(Canvas canvas) {
        Rect bounds = getBounds();

        Rect innerRect = createInnerRect(bounds);

        // Draw inner rectangle
        canvas.drawRect(innerRect, mInnerRectPaint);

        // Bounds rect
        float[] topLeft = new float[]{bounds.left, bounds.top};
        float[] topRight = new float[]{bounds.right, bounds.top};
        float[] bottomRight = new float[]{bounds.right, bounds.bottom};
        float[] bottomLeft = new float[]{bounds.left, bounds.bottom};

        // Inner rect
        float[] innerTopLeft = new float[]{innerRect.left, innerRect.top};
        float[] innerTopRight = new float[]{innerRect.right, innerRect.top};
        float[] innerBottomRight = new float[]{innerRect.right, innerRect.bottom};
        float[] innerBottomLeft = new float[]{innerRect.left, innerRect.bottom};

        // Draw left bevel
        setBevelPath(mLeftBevelPath, topLeft, innerTopLeft, innerBottomLeft, bottomLeft);
        canvas.drawPath(mLeftBevelPath, mLeftPaint);

        // Draw top bevel
        setBevelPath(mTopBevelPath, topLeft, topRight, innerTopRight, innerTopLeft);
        canvas.drawPath(mTopBevelPath, mTopPaint);

        // Draw right bevel
        setBevelPath(mRighBevelPath, innerTopRight, topRight, bottomRight, innerBottomRight);
        canvas.drawPath(mRighBevelPath, mRightPaint);

        // Draw bottom bevel
        setBevelPath(mBottomBevelPath, innerBottomLeft, innerBottomRight, bottomRight, bottomLeft);
        canvas.drawPath(mBottomBevelPath, mBottomPaint);
    }

    private Rect createInnerRect(Rect bounds) {
        float height = bounds.height();
        float width = bounds.width();

        float innerHeight = height * mFillPercent;
        float innerWidth = width * mFillPercent;

        int left = (int) ((width - innerWidth) / 2);
        int top = (int) ((height - innerHeight) / 2);
        int bottom = (int) (top + innerHeight);
        int right = (int) (left + innerWidth);

        return new Rect(left, top, right, bottom);
    }

    private void setBevelPath(Path path,
                              float[] topLeft,
                              float[] topRight,
                              float[] bottomRight,
                              float[] bottomLeft) {

        path.moveTo(topLeft[0], topLeft[1]);
        List<float[]> vertexList = Arrays.asList(topRight, bottomRight, bottomLeft, topLeft);

        for (float[] vertex : vertexList) {
            path.lineTo(vertex[0], vertex[1]);
        }
        path.close();
    }


    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(ColorFilter cf) {

    }

    @Override
    public int getOpacity() {
        return 0;
    }
}
