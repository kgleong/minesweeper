package com.orangemako.minesweeper.board;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.ViewGroup;

public class BoardLayoutView extends ViewGroup {
    private Board mBoard;
    private Paint mPaint;

    public BoardLayoutView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Enable drawing for ViewGroup object
        setWillNotDraw(false);

        setupDrawObjects();
    }

    private void setupDrawObjects() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(20.0f);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        // Ensure the board is a square
        int dimension = Math.min(width, height);

        setMeasuredDimension(dimension, dimension);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();

        drawLines(width, height, canvas);
    }

    private void drawLines(int width, int height, Canvas canvas) {
        if(mBoard != null) {
            int dimension = mBoard.getDimension();
            int interval = height / dimension;

            float startX = 0;
            float startY = 0;
            float endX = width;
            float endY = 0;

            // Horizontal lines
            for(int i = 1; i < dimension; i++) {
                startY = endY = interval * i;
                canvas.drawLine(startX, startY, endX, endY, mPaint);
            }

            startX = endX = 0;
            startY = 0;
            endY = height;

            // Vertical lines
            for(int i = 1; i < dimension; i++) {
                startX = endX = interval * i;
                canvas.drawLine(startX, startY, endX, endY, mPaint);
            }
        }
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    public void setBoard(Board mBoard) {
        this.mBoard = mBoard;
        invalidate();
    }
}
