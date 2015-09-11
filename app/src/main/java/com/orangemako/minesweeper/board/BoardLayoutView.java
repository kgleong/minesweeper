package com.orangemako.minesweeper.board;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

public class BoardLayoutView extends ViewGroup {
    public BoardLayoutView(Context context) {
        super(context);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }
}
