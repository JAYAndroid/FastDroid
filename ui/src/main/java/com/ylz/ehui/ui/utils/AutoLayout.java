package com.ylz.ehui.ui.utils;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
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
    private static final String KEY_DESIGN_WIDTH = "auto_design_width";
    private static final String KEY_DESIGN_HEIGHT = "auto_design_height";
    private static float sNoncompatDensity;
    private static float sNoncompatScaleDensity;
    private static AutoBase mAutoBase;

    private AutoLayout() {
    }

    /**
     * 设计稿初始化方式一，建议在application的oncreate初始化
     *
     * @param width
     * @param height
     */
    public static void initDesignWH(int width, int height) {
        designWidth = width;
        designHeight = height;
    }

    /**
     * 设计稿初始化方式二，manifest中初始化
     */
    private void getMetaDesignWH(Context context) {
        PackageManager packageManager = context.getPackageManager();
        ApplicationInfo applicationInfo;
        try {
            applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            if (applicationInfo != null && applicationInfo.metaData != null) {
                designWidth = (int) applicationInfo.metaData.get(KEY_DESIGN_WIDTH);
                designHeight = (int) applicationInfo.metaData.get(KEY_DESIGN_HEIGHT);

                if (designWidth == 0 || designHeight == 0) {
                    String tip = "需要在manifest配置设计稿宽度 " + KEY_DESIGN_WIDTH + " 和高度 " + KEY_DESIGN_HEIGHT
                            + " ,或者在application的onCreate中调用AutoLayout.initDesignWH()";
                    throw new RuntimeException(tip);
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static class Singleton {
        private static AutoLayout instance = new AutoLayout();
    }

    public static AutoLayout base(@NonNull AutoBase autoBase) {
        mAutoBase = autoBase;
        return Singleton.instance;
    }

    public void auto(@NonNull Activity activity) {
        if (designHeight == 0 || designWidth == 0) {
            getMetaDesignWH(activity);
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

        // 如果设备有虚拟导航键，则使用高度维度做适配；
        // 或者用户指定高度适配，也使用高度作为适配维度
        if (getVirtualBarHeight(activity) > 0 || mAutoBase.ordinal() == AutoBase.BASE_HEIGHT.ordinal()) {
            targetDensity = appDisplayMetrics.heightPixels / (float) designHeight;
        } else {
            targetDensity = appDisplayMetrics.widthPixels / (float) designWidth;
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
