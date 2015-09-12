package com.orangemako.minesweeper.utilities;

import android.content.Context;

public class GraphicsUtils {
    public static float pxToDp(float px, Context context) {
        return px / getDensity(context);
    }

    public static float dpToPx(float dp, Context context) {
        return dp * getDensity(context);
    }

    private static float getDensity(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }
}
