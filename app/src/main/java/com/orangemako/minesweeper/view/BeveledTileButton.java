package com.orangemako.minesweeper.view;

import android.content.Context;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.StateSet;
import android.widget.Button;

import com.orangemako.minesweeper.drawable.BeveledTileDrawable;
import com.orangemako.minesweeper.exceptions.InvalidArgumentException;

public class BeveledTileButton extends Button {
    public static String TAG = BeveledTileButton.class.getName();
    static final int COLOR_OFFSET = 40;

    public BeveledTileButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        BeveledTileDrawable.BeveledTileAttributeSet beveledAttributes =
                BeveledTileDrawable.extractAttributes(context, attrs);

        try {
            setBackground(createBackground(beveledAttributes));

        } catch (InvalidArgumentException e) {
            Log.e(TAG, e.getClass().getName());
        }
    }

    private StateListDrawable createBackground(BeveledTileDrawable.BeveledTileAttributeSet attributeSet) throws InvalidArgumentException {
        StateListDrawable drawable = new StateListDrawable();

        BeveledTileDrawable normalDrawable = new BeveledTileDrawable(attributeSet.getColorArray(), attributeSet.getFillPercent());

        BeveledTileDrawable.BeveledTileAttributeSet pressedAttributeSet = createPressedAttributes(attributeSet);
        BeveledTileDrawable pressedDrawable = new BeveledTileDrawable(pressedAttributeSet.getColorArray(), pressedAttributeSet.getFillPercent());

        drawable.addState(new int[] {android.R.attr.state_pressed}, pressedDrawable);
        drawable.addState(new int[] {android.R.attr.state_hovered}, pressedDrawable);
        drawable.addState(StateSet.WILD_CARD, normalDrawable);
        return drawable;
    }

    private BeveledTileDrawable.BeveledTileAttributeSet createPressedAttributes(BeveledTileDrawable.BeveledTileAttributeSet attributeSet) {

        int[] colorList = attributeSet.getColorArray();
        int[] pressedColorList = new int[BeveledTileDrawable.REQUIRED_COLOR_COUNT];

        for(int i = 0; i < colorList.length; i++) {
            pressedColorList[i] = colorList[i] + COLOR_OFFSET;
        }

        return new BeveledTileDrawable.BeveledTileAttributeSet(pressedColorList, attributeSet.getFillPercent());
    }
}
