package com.ylz.ehui.ui.dialog;

import android.support.annotation.ColorRes;
import android.view.View;
import android.widget.Button;
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
    private int mResColor;

    private TextView mTitleView;
    private TextView mMsgView;
    private Button mNegativeView;
    private Button mPositiveVew;

    private Creater mCreater;

    @Override
    protected Builder build(Builder builder) {
        return builder.setView(R.layout.fast_droid_dialog_confirm);
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
    protected void doInit(View parent) {
        this.mTitle = mCreater.mTitle;
        this.mMsg = mCreater.mMsg;
        this.mPositiveMsg = mCreater.mPositiveMsg;
        this.mNegativeMsg = mCreater.mNegativeMsg;
        this.mPositiveListener = mCreater.mPositiveListener;
        this.mNegativeListener = mCreater.mNegativeListener;
        this.mResColor = mCreater.mResColor;

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


    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        dismiss();

        if (viewId == R.id.btn_confirm_dialog_negative) {
            if (mNegativeListener != null) {
                mNegativeView.setOnClickListener(mNegativeListener);
            }

        } else if (viewId == R.id.btn_confirm_dialog_positive) {
            if (mPositiveListener != null) {
                mPositiveVew.setOnClickListener(mPositiveListener);
            }
        }

    }

    public static class Creater implements Serializable {
        private String mTitle;
        private String mMsg;
        private String mPositiveMsg;
        private String mNegativeMsg;
        private View.OnClickListener mPositiveListener;
        private View.OnClickListener mNegativeListener;
        private int mResColor;

        public Creater setTitle(String title) {
            mTitle = title;
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
