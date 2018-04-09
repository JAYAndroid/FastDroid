package com.ylz.ehui.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.app.Application.ActivityLifecycleCallbacks;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;

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

    private Utils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 初始化工具类
     */
    public static void init(@NonNull final Context context) {
        mApplicationContext = context.getApplicationContext();
    }

    /**
     * 获取 Application
     *
     * @return Application
     */
    public static Context getApp() {
        if (mApplicationContext != null) return mApplicationContext;
        throw new NullPointerException("u should init first");
    }
}
