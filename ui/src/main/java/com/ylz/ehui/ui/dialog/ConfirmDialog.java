package com.ylz.ehui.ui.dialog;

import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ylz.ehui.base_ui.R;
import com.ylz.ehui.utils.StringUtils;

import java.io.Serializable;

/**
 * Created by yons on 2018/4/3.
 */

public class ConfirmDialog extends BaseDialogFragment implements View.OnClickListener {
    private String mTitle;
    private String mMsg;
    private String mPositiveMsg;
    private String mNegativeMsg;
    private View.OnClickListener mPositiveListener;
    private View.OnClickListener mNegativeListener;

    private boolean mHidenPositiveButton;
    private boolean mHidenNegativeButton;

    private View mCustomView;
    private int mResColor;

    private TextView mTitleView;
    private TextView mMsgView;
    private Button mNegativeView;
    private Button mPositiveVew;

    private Creater mCreater;
    private boolean isCustomClosed;

    @Override
    protected Builder build(Builder builder) {
        return builder.setView(R.layout.fast_droid_dialog_confirm)
                .widthScale(0.8f);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (mPositiveListener != null) {
            mPositiveListener = null;
        }

        if (mNegativeListener != null) {
            mNegativeListener = null;
        }
    }

    @Override
    protected void onInitialization(View parent, Bundle bundle) {
        this.mTitle = mCreater.mTitle;
        this.mMsg = mCreater.mMsg;
        this.mPositiveMsg = mCreater.mPositiveMsg;
        this.mNegativeMsg = mCreater.mNegativeMsg;
        this.mPositiveListener = mCreater.mPositiveListener;
        this.mNegativeListener = mCreater.mNegativeListener;
        this.mResColor = mCreater.mResColor;
        this.mCustomView = mCreater.mCustomView;
        this.mHidenPositiveButton = mCreater.mHidenPositiveButton;
        this.mHidenNegativeButton = mCreater.mHidenNegativeButton;
        this.isCustomClosed = mCreater.isCustomClosed;

        mTitleView = parent.findViewById(R.id.tv_confirm_dialog_title);
        mMsgView = parent.findViewById(R.id.tv_confirm_dialog_msg);

        mNegativeView = parent.findViewById(R.id.btn_confirm_dialog_negative);
        mPositiveVew = parent.findViewById(R.id.btn_confirm_dialog_positive);

        mNegativeView.setOnClickListener(this);
        mPositiveVew.setOnClickListener(this);

        if (!StringUtils.isEmpty(mTitle)) {
            mTitleView.setText(mTitle);
        }

        if (mResColor > 0) {
            mTitleView.setTextColor(getContext().getResources().getColor(mResColor));
        }

        if (!StringUtils.isEmpty(mMsg)) {
            mMsgView.setText(mMsg);
        }

        if (!StringUtils.isEmpty(mPositiveMsg)) {
            mPositiveVew.setText(mPositiveMsg);
        }

        if (!StringUtils.isEmpty(mNegativeMsg)) {
            mNegativeView.setText(mNegativeMsg);
        }

        if (mCustomView != null) {
            FrameLayout customView = parent.findViewById(R.id.fl_custom_view);
            customView.setVisibility(View.VISIBLE);
            mMsgView.setVisibility(View.GONE);
            customView.addView(mCustomView);
        }

        if (mHidenPositiveButton) {
            mPositiveVew.setVisibility(View.GONE);

            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mNegativeView.getLayoutParams();
            layoutParams.setMargins(0, 0, 0, 0);
        } else if (mHidenNegativeButton) {
            mNegativeView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();

        if (viewId == R.id.btn_confirm_dialog_negative) {
            dismiss();
            if (mNegativeListener != null) {
                mNegativeListener.onClick(view);
            }

        } else if (viewId == R.id.btn_confirm_dialog_positive) {
            if (!isCustomClosed) {
                dismiss();
            }

            if (mPositiveListener != null) {
                mPositiveListener.onClick(view);
            }
        }

    }

    public static class Creater implements Serializable {
        public boolean isCustomClosed;
        private String mTitle;
        private String mMsg;
        private String mPositiveMsg;
        private String mNegativeMsg;
        private View.OnClickListener mPositiveListener;
        private View.OnClickListener mNegativeListener;
        private View mCustomView;
        private int mResColor;
        private boolean mHidenPositiveButton;
        private boolean mHidenNegativeButton;

        public Creater setTitle(String title) {
            mTitle = title;
            return this;
        }

        public Creater setCustomView(View view) {
            this.mCustomView = view;
            return this;
        }

        public Creater setTitleColor(@ColorRes int resColor) {
            this.mResColor = resColor;
            return this;
        }

        public Creater setMessage(String msg) {
            mMsg = msg;
            return this;
        }

        public Creater setPositiveButton(String positiveMsg, View.OnClickListener listener) {
            mPositiveMsg = positiveMsg;
            mPositiveListener = listener;
            return this;
        }

        public Creater hidenPositiveButton(boolean hiden) {
            this.mHidenPositiveButton = hiden;
            return this;
        }

        public Creater hidenNegativeButton(boolean hiden) {
            this.mHidenNegativeButton = hiden;
            return this;
        }

        public Creater customClose(){
            isCustomClosed = true;
            return this;
        }

        public Creater setNegativeButton(String negativeMsg, View.OnClickListener listener) {
            mNegativeListener = listener;
            mNegativeMsg = negativeMsg;
            return this;
        }

        public ConfirmDialog create() {
            ConfirmDialog confirmDialog = new ConfirmDialog();
            confirmDialog.bindData(this);
            return confirmDialog;
        }
    }

    private void bindData(Creater creater) {
        mCreater = creater;
    }
}
