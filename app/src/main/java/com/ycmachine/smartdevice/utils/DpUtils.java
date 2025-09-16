package com.ycmachine.smartdevice.utils;

import android.content.Context;
import android.util.DisplayMetrics;

// dp转px工具类
public class DpUtils {
    public static int dpToPx(Context context, int dp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (int) (dp * metrics.density + 0.5f);
    }
}