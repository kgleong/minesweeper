package com.orangemako.minesweeper.tile;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;

public class TextDrawable extends Drawable {
    static final float DEFAULT_FILL_PERCENT = 0.75f;

    private String mText;
    private int mTextColor;
    private Paint mPaint;
    private float mFillPercent = DEFAULT_FILL_PERCENT;

    public TextDrawable(String text, int textColor) {
        this(text, textColor, null);
    }

    public TextDrawable(String text, int textColor, Float fillPercent) {
        mText = text;
        mTextColor = textColor;

        if(fillPercent != null) {
            mFillPercent = fillPercent;
        }

        setupDrawObjects();
    }

    private void setupDrawObjects() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(mTextColor);
        mPaint.setFakeBoldText(true);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setTypeface(Typeface.DEFAULT_BOLD);
    }

    @Override
    public void draw(Canvas canvas) {
        if(mText.length() > 0) {
            Rect bounds = getBounds();

            float textSize = Math.max(bounds.height(), getBounds().width()) * mFillPercent;
            mPaint.setTextSize(textSize);

            // Ascent - The recommended distance above baseline for single spaced text.
            // Descent - The recommended distance below baseline for single spaced text.
            // Top - The maximum distance above the baseline for the tallest glyph in the font at a given text size.
            // Bottom - The maximum distance below the baseline for the lowest glyph in the font at a given text size.
            // Baseline - The horizontal line the text 'sits' on.

            // Calculate recommended height
            float textHeight = Math.abs(mPaint.ascent()) + Math.abs(mPaint.descent());

            // Calculate baseline to vertically center text.
            float centeredBaseline = bounds.centerY() + (textHeight / 2) - mPaint.descent();

            // 3rd argument is the baseline for text.
            canvas.drawText(mText, bounds.centerX(), centeredBaseline, mPaint);
        }
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
