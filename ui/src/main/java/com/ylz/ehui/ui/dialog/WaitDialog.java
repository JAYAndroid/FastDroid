package com.ylz.ehui.ui.dialog;

import android.view.View;

import com.ylz.ehui.base_ui.R;
import com.ylz.ehui.utils.SizeUtils;

public class WaitDialog extends BaseDialogFragment {
    @Override
    protected Builder build(Builder builder) {
        return builder.setView(R.layout.fast_droid_dialog_wait)
                .height(SizeUtils.dp2px(120))
                .setCanceledOnTouchOutside(true)
                .setCancelable(true)
                .width(SizeUtils.dp2px(120));
    }


    @Override
    protected void doInit(View parent) {

    }
}
