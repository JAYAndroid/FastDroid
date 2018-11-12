package com.ylz.ehui.utils;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

/**
 * <pre>
 *     author: Blankj
 *     blog  : http://blankj.com
 *     time  : 16/12/08
 *     desc  : Utils 初始化相关
 * </pre>
 */
public final class Utils {
    @SuppressLint("StaticFieldLeak")
    private static Context mApplicationContext;
    private static boolean isDebug = false;

    private Utils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 初始化工具类
     */
    public static void init(@NonNull final Application application) {
        init(application, false);
    }

    public static void init(@NonNull final Application application, boolean debug) {
        mApplicationContext = application.getApplicationContext();
        isDebug = debug;
        ToastUtils.init(application);
    }

    /**
     * 获取 Application
     *
     * @return Application
     */
    public static Context getApp() {
        if (mApplicationContext != null) return mApplicationContext;
        throw new NullPointerException("请先调用Utils的init进行初始化");
    }

    public static boolean isDebug() {
        return isDebug;
    }
}
