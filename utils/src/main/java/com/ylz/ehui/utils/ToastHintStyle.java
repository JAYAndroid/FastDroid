package com.ylz.ehui.utils;

import com.ylz.ehui.module_utils.R;

/**
 * Author: yms
 * Time: 2018/11/12 10:49
 * Describe:
 */
public class ToastHintStyle extends ToastBlackStyle {
    @Override
    public int getBackgroundDrawable() {
        return R.drawable.toast_bg_blue;
    }

    @Override
    public int getTextColor() {
        return 0XFF196FFA;
    }
}
