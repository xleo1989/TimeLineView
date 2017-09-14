package com.x.leo.timelineview;

import android.content.Context;
import android.graphics.Color;

import java.util.Random;

/**
 * @作者:My
 * @创建日期: 2017/5/24 18:17
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public class Utils {
    public static int dp2Px(Context context, int dpSize) {
        return (int) (context.getResources().getDisplayMetrics().density * dpSize);
    }

    public static int px2Dp(Context context, float px) {
        return (int) (px / context.getResources().getDisplayMetrics().density);
    }

    public static int sp2Px(Context context, int sp) {
        return (int) (context.getResources().getDisplayMetrics().scaledDensity * sp);
    }

    public static int px2Sp(Context context, int px) {
        return (int) (px / context.getResources().getDisplayMetrics().scaledDensity);
    }

    public static int randomColor(int lastColor, int alpha) {
        int argb = 0;
        do {
            int red = 0, blue = 0, green = 0;
            red = getRandomInt(35, 186);
            blue = getRandomInt(35, 186);
            green = getRandomInt(35, 186);
            argb = Color.argb(alpha, red, green, blue);
        } while (argb == lastColor);
        return argb;
    }

    private static int getRandomInt(int min, int max) {
        return new Random().nextInt(max - min) + min;
    }
}
