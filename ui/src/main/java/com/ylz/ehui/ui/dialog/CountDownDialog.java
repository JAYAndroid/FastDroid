package com.ylz.ehui.ui.dialog;

import android.os.CountDownTimer;
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

public class CountDownDialog extends BaseDialogFragment implements View.OnClickListener {
    private View.OnClickListener mPositiveListener;

    private TextView mTitleView;
    private TextView mMsgView;
    private Button mPositiveVew;

    private Creater mCreater;
    private MyCountDown mMyCountDown;


    @Override
    protected Builder build(Builder builder) {
        return builder.setView(R.layout.fast_droid_dialog_count_down)
                .heightScale(0.4f)
                .widthScale(0.8f);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (mPositiveListener != null) {
            mPositiveListener = null;
        }

        if (mMyCountDown != null) {
            mMyCountDown.cancel();
            mMyCountDown = null;
        }
    }

    @Override
    protected void doInit(View parent) {
        String mTitle = mCreater.mTitle;
        String mMsg = mCreater.mMsg;
        String mPositiveMsg = mCreater.mPositiveMsg;
        this.mPositiveListener = mCreater.mPositiveListener;
        int mResColor = mCreater.mResColor;

        mTitleView = parent.findViewById(R.id.tv_confirm_dialog_title);
        mMsgView = parent.findViewById(R.id.tv_confirm_dialog_msg);

        mPositiveVew = parent.findViewById(R.id.btn_confirm_dialog_positive);
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

        mPositiveVew.setEnabled(false);
        // 此处增加一秒是为了视觉效果过渡
        mCreater.millisInFuture += 1000;
        String down = mCreater.millisInFuture / 1000 + "s";
        mPositiveVew.setText(down);

        mMyCountDown = new MyCountDown(mCreater.millisInFuture, mCreater.countDownInterval);
        mMyCountDown.start();
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        dismiss();

        if (viewId == R.id.btn_confirm_dialog_positive && mPositiveListener != null) {
            mPositiveListener.onClick(view);
        }
    }

    private class MyCountDown extends CountDownTimer {
        public MyCountDown(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millis) {
            String down = millis / 1000 + "s";
            mPositiveVew.setText(down);
        }

        @Override
        public void onFinish() {
            mPositiveVew.setEnabled(true);
            mPositiveVew.setText(StringUtils.isEmpty(mCreater.tickFinishText) ? "我知道了" : mCreater.tickFinishText);
        }
    }

    public static class Creater implements Serializable {
        private String mTitle;
        private String mMsg;
        private String mPositiveMsg;
        private View.OnClickListener mPositiveListener;
        private int mResColor;
        private long millisInFuture = 5000;
        private long countDownInterval = 1000;
        private String tickFinishText;

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

        public Creater setTickFinishText(String tickFinishText) {
            this.tickFinishText = tickFinishText;
            return this;
        }

        public Creater setMillisInFuture(long millisInFuture) {
            this.millisInFuture = millisInFuture;
            return this;
        }

        public Creater setCountDownInterval(long countDownInterval) {
            this.countDownInterval = countDownInterval;
            return this;
        }

        public Creater setPositiveButton(String positiveMsg, View.OnClickListener listener) {
            mPositiveMsg = positiveMsg;
            mPositiveListener = listener;
            return this;
        }

        public CountDownDialog create() {
            CountDownDialog confirmDialog = new CountDownDialog();
            confirmDialog.bindData(this);
            return confirmDialog;
        }
    }

    private void bindData(Creater creater) {
        mCreater = creater;
    }

}
