package com.ylz.ehui.ui.utils;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;

/**
 * Author: yms
 * Time: 2018/9/4 09:38
 * Describe:
 */
public class AutoLayout {
    private static int designWidth;
    private static int designHeight;

    private static float sNoncompatDensity;
    private static float sNoncompatScaleDensity;

    private AutoLayout() {

    }

    private static class Singleton {
        private static AutoLayout instance = new AutoLayout();
    }

    /**
     * 适配设计稿宽度，单位dp
     *
     * @param width
     * @return
     */
    public static AutoLayout designWidth(int width) {
        designWidth = width;
        designHeight = 0;
        return Singleton.instance;
    }

    /**
     * 适配设计稿高度，单位dp
     *
     * @param height
     * @return
     */
    public static AutoLayout designHeight(int height) {
        designWidth = 0;
        designHeight = height;
        return Singleton.instance;
    }

    public void auto(@NonNull Activity activity) {
        if (designHeight == 0 && designWidth == 0) {
            throw new RuntimeException("必须初始化设计稿的宽或高");
        }

        final Application application = activity.getApplication();
        DisplayMetrics appDisplayMetrics = application.getResources().getDisplayMetrics();

        if (sNoncompatDensity == 0) {
            sNoncompatDensity = appDisplayMetrics.density;
            sNoncompatScaleDensity = appDisplayMetrics.scaledDensity;
            application.registerComponentCallbacks(new ComponentCallbacks() {
                @Override
                public void onConfigurationChanged(Configuration newConfig) {
                    if (newConfig != null && newConfig.fontScale > 0) {
                        sNoncompatScaleDensity = application.getResources().getDisplayMetrics().scaledDensity;
                    }
                }

                @Override
                public void onLowMemory() {

                }
            });
        }

        final float targetDensity;
        if (designWidth > 0) {
            targetDensity = appDisplayMetrics.widthPixels / (float) designWidth;
        } else {
            targetDensity = appDisplayMetrics.heightPixels / (float) designHeight;
        }


        final float targetScaledDensity = targetDensity * (sNoncompatScaleDensity / sNoncompatDensity);
        final int targetDensityDpi = (int) (160 * targetDensity);
        appDisplayMetrics.density = targetDensity;
        appDisplayMetrics.scaledDensity = targetScaledDensity;
        appDisplayMetrics.densityDpi = targetDensityDpi;

        DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
        displayMetrics.density = targetDensity;
        displayMetrics.scaledDensity = targetScaledDensity;
        displayMetrics.densityDpi = targetDensityDpi;
    }


}
