package com.ylz.ehui.ui.manager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;

import java.util.Stack;

/**
 * Author: yms
 * Date: 2018/1/24
 * Description:
 */
public class AppManager {
    private Stack<FragmentActivity> container;

    private AppManager() {
        if (container == null) {
            container = new Stack<>();
        }
    }

    public static AppManager getInstance() {
        return Singleton.instance;
    }

    /**
     * 退添加单个Activity
     */
    public void addActivity(FragmentActivity activity) {

        if (activity == null) {
            return;
        }

        if (!container.contains(activity)) {
            container.add(activity);
        }
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    public FragmentActivity currentActivity() {
        return container.lastElement();
    }

    /**
     * 退出单个Activity
     */
    public void removeActivity(FragmentActivity activity) {
        if (activity == null) {
            return;
        }

        if (!container.contains(activity)) {
            return;
        }

        container.remove(activity);
    }

    public void finishActivity(@NonNull FragmentActivity activity) {
        activity.finish();
    }

    /**
     * 一键退出APP
     */
    public void existApp() {
        if (container != null && container.size() > 0) {
            for (Activity item : container) {
                item.finish();
            }

            container.clear();
        }
    }

    public void startActivity(FragmentActivity from, Intent intent) {
        from.startActivity(intent);
    }

    public <T extends FragmentActivity> void startActivityWithFinish(FragmentActivity from, Class<T> cls) {
        startActivity(from, cls, -1);
        from.finish();
    }

    public <T extends FragmentActivity> void startActivityWithFinish(FragmentActivity from, Intent intent) {
        startActivity(from, intent);
        from.finish();
    }

    public <T extends FragmentActivity> void startActivity(Context context, Class<T> cls) {
        startActivity(context, cls, -1);
    }

    public <T extends FragmentActivity> void startActivity(Context context, Class<T> cls, int flags) {
        Intent intent = new Intent(context, cls);
        if (flags > -1) {
            intent.setFlags(flags);
        }
        context.startActivity(intent);
    }

    private static class Singleton {
        private static AppManager instance = new AppManager();
    }
}
