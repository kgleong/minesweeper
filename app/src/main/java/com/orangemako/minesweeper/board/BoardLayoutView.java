package com.orangemako.minesweeper.board;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.orangemako.minesweeper.R;
import com.orangemako.minesweeper.utilities.GraphicsUtils;

public class BoardLayoutView extends ViewGroup {
    static final int DEFAULT_LINE_COLOR = Color.BLACK;

    // In density independent pixels (dp)
    static final int DEFAULT_BORDER_WIDTH = 2;
    static final int DEFAULT_GRIDLINE_WIDTH = 1;

    private Board mBoard;

    private Paint mGridLinesPaint;
    private int mGridLineColor;
    private float mGridLineStrokeWidth;
    private float mDefaultGridLineStrokeWidth;

    private Paint mBorderPaint;
    private int mBorderColor;
    private float mBorderStrokeWidth;
    private float mDefaultBorderStrokeWidth;

    public BoardLayoutView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setupDefaultValues();
        extractAttributes(attrs);

        // Enable drawing for ViewGroup object
        setWillNotDraw(false);

        setupDrawObjects();
    }

    private void setupDefaultValues() {
        mDefaultBorderStrokeWidth = GraphicsUtils.dpToPx(DEFAULT_BORDER_WIDTH, getContext());
        mDefaultGridLineStrokeWidth = GraphicsUtils.dpToPx(DEFAULT_GRIDLINE_WIDTH, getContext());
    }

    private void extractAttributes(AttributeSet attrs) {
        TypedArray attributesArray = getContext().obtainStyledAttributes(attrs, R.styleable.BoardLayoutView);

        try {
            mGridLineColor =
                    attributesArray.getColor(R.styleable.BoardLayoutView_gridLineColor, DEFAULT_LINE_COLOR);

            mGridLineStrokeWidth =
                    attributesArray.getDimension(R.styleable.BoardLayoutView_gridLineWidth, mDefaultGridLineStrokeWidth);

            mBorderColor =
                    attributesArray.getColor(R.styleable.BoardLayoutView_borderColor, DEFAULT_LINE_COLOR);

            mBorderStrokeWidth =
                    attributesArray.getDimension(R.styleable.BoardLayoutView_borderWidth, mDefaultBorderStrokeWidth);
        }
        finally {
           attributesArray.recycle();
        }
    }

    private void setupDrawObjects() {
        mGridLinesPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mGridLinesPaint.setColor(mGridLineColor);
        mGridLinesPaint.setStyle(Paint.Style.STROKE);
        mGridLinesPaint.setStrokeWidth(mGridLineStrokeWidth);

        mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBorderPaint.setColor(mBorderColor);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setStrokeWidth(mBorderStrokeWidth);
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

        drawGridLines(width, height, canvas);
        drawBorder(width, height, canvas);
    }

    private void drawBorder(int width, int height, Canvas canvas) {
        canvas.drawRect(0, 0, width, height, mBorderPaint);
    }

    private void drawGridLines(int width, int height, Canvas canvas) {
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
                canvas.drawLine(startX, startY, endX, endY, mGridLinesPaint);
            }

            startY = 0;
            endY = height;

            // Vertical lines
            for(int i = 1; i < dimension; i++) {
                startX = endX = interval * i;
                canvas.drawLine(startX, startY, endX, endY, mGridLinesPaint);
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
