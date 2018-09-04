package com.ylz.ehui.ui.utils;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import java.lang.reflect.Method;

/**
 * Author: yms
 * Time: 2018/9/4 09:38
 * Describe:
 */
public class AutoLayout {
    private static int designWidth;
    private static int designHeight;
    private static final int DEFAULT_DESIGN_HEIGHT = 667;

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

    /**
     * 是否有虚拟导航栏，没有设宽度适配
     * 有，高度适配，是否显示，显示减去虚拟导航栏高度，隐藏加上虚拟导航栏高度
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

        // 如果设备有虚拟导航键，则使用高度维度做适配
        if (getVirtualBarHeight(activity) > 0) {
            targetDensity = appDisplayMetrics.heightPixels / (float) DEFAULT_DESIGN_HEIGHT;
        } else {
            if (designWidth > 0) {
                targetDensity = appDisplayMetrics.widthPixels / (float) designWidth;
            } else {
                targetDensity = appDisplayMetrics.heightPixels / (float) designHeight;
            }
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

    private int getVirtualBarHeight(Context context) {
        int vh = 0;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (windowManager == null) {
            return vh;
        }

        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        try {
            @SuppressWarnings("rawtypes")
            Class c = Class.forName("android.view.Display");
            @SuppressWarnings("unchecked")
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, dm);
            vh = dm.heightPixels - display.getHeight();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vh;
    }


}
