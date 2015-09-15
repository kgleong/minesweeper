package com.orangemako.minesweeper.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import com.orangemako.minesweeper.drawable.BeveledTileDrawable;
import com.orangemako.minesweeper.exceptions.InvalidArgumentException;

public class BeveledTileTextView extends TextView {
    public static String TAG = BeveledTileTextView.class.getName();

    public BeveledTileTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        BeveledTileDrawable.BeveledTileAttributeSet beveledAttributes =
                BeveledTileDrawable.extractAttributes(context, attrs);

        try {
            BeveledTileDrawable drawable =
                    new BeveledTileDrawable(beveledAttributes.getColorArray(), beveledAttributes.getFillPercent());

            setBackground(drawable);
        } catch (InvalidArgumentException e) {
            Log.e(TAG, e.getClass().getName());
        }
    }
}
