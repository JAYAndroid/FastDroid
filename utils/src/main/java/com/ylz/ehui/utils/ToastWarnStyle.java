package com.ylz.ehui.utils;


import com.ylz.ehui.module_utils.R;

/**
 * Author: yms
 * Time: 2018/11/12 10:49
 * Describe:
 */
public class ToastWarnStyle extends ToastBlackStyle {
    @Override
    public int getBackgroundDrawable() {
        return R.drawable.toast_bg_red;
    }

    @Override
    public int getTextColor() {
        return 0Xffef482c;
    }
}
