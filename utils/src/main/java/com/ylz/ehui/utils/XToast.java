package com.ylz.ehui.utils;

import android.content.Context;

/**
 *    author : HJQ
 *    github : https://github.com/getActivity/ToastUtils
 *    time   : 2018/09/17
 *    desc   : 防止频繁弹出的Toast
 */
final class XToast extends BaseToast {

    XToast(Context context) {
        super(context);
    }

    @Override
    public void show() {
        // 移除之前显示吐司的任务
        getHandler().removeCallbacks(this);
        // 添加一个显示吐司的任务
        getHandler().postDelayed(this, SHOW_DELAY_MILLIS);
    }

    /**
     * {@link Runnable}
     */
    @Override
    public void run() {
        // 设置吐司文本
        getMessageView().setText(getText());
        // 显示吐司
        super.show();
    }

    @Override
    public void cancel() {
        // 移除之前显示吐司的任务
        getHandler().removeCallbacks(this);
        // 取消显示
        super.cancel();
    }
}