package com.ylz.ehui.ui.dialog;

import android.view.View;

import com.ylz.ehui.base_ui.R;

public class WaitDialog extends BaseDialogFragment {
    @Override
    protected Builder build(Builder builder) {
        return builder.setView(R.layout.fast_droid_dialog_wait);
    }

    @Override
    protected void doInit(View parent) {

    }
}
