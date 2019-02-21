package com.ylz.ehui.ui.manager;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ylz.ehui.base_ui.R;
import com.ylz.ehui.utils.SizeUtils;


/**
 * Author: yemusen
 * Date: 2017/5/16
 * Description:统一标题栏管理类（布局风格：左侧、中间、右侧）
 * Builder模式
 */

public class CommonTitleBarManager implements View.OnAttachStateChangeListener, View.OnClickListener {

    /**
     * 控制标题栏左侧是否隐藏
     */
    private boolean hidenLeftView;

    /**
     * 控制标题栏中间侧是否隐藏
     */
    private boolean hidenMiddleView;

    /**
     * 控制标题栏右侧是否隐藏
     */
    private boolean isShowRightView;

    /**
     * 控制标题栏"关闭"按钮是否隐藏
     */
    private boolean hidenCloseView;

    /**
     * 控制分割线是否隐藏
     */
    private boolean hidenSpliteLine;
    private boolean hidenTitleLine;
    /**
     * 标题栏中间视图（动态替换）
     */
    private View middlerView;

    /**
     * 左侧标题栏点击事件
     */
    private View.OnClickListener mLeftListener;

    /**
     * 右侧标题栏点击事件
     */
    private View.OnClickListener mRightListener;

    /**
     * 标题栏"关闭"按钮点击事件
     */
    private View.OnClickListener mCloseListener;

    /**
     * 标题栏所在根视图
     */
    private View mRootView;

    /**
     * 标题栏背景色
     */
    private int bgColor;

    /**
     * 标题栏左侧图标id
     */
    private int leftResId;

    /**
     * 标题栏右侧图标id
     */
    private int rightResId;

    /**
     * 右边文本视图
     */
    public String rightText;
    public int rightTextColor;

    private ImageView ivLeftView;
    private ImageView ivCloseView;
    private ImageView ivRightView;
    private FrameLayout flRightView;
    private TextView tvRightTextView;

    private RelativeLayout rlMiddleView;
    private RelativeLayout contentView;

    private View spliteLineView;
    private View spliteTitleLineView;


    private Context mContext;

    private CommonTitleBarManager(Builder builder) {
        this.hidenLeftView = builder.hideLeftView;
        this.hidenMiddleView = builder.hideMiddleView;
        this.isShowRightView = builder.isShowRightView;
        this.hidenCloseView = builder.hideCloseView;
        this.hidenSpliteLine = builder.hidenSpliteLine;
        this.hidenTitleLine = builder.hidenTitleLine;

        this.mContext = builder.mContext;

        this.middlerView = builder.mMiddlerView;

        this.mLeftListener = builder.mLeftListener;
        this.mRightListener = builder.mRightListener;
        this.mCloseListener = builder.mCloseListener;

        this.mRootView = builder.mRootView;
        this.bgColor = builder.bgColor;
        this.leftResId = builder.leftResId;
        this.rightResId = builder.rightResId;

        this.rightText = builder.rightText;
        this.rightTextColor = builder.rightTextColor;

        initView();
        initListener();

    }

    private void initListener() {
        ivLeftView.setOnClickListener(this);
        if (mRightListener != null) {
            flRightView.setOnClickListener(mRightListener);
        }

        if (mCloseListener != null) {
            ivCloseView.setOnClickListener(mCloseListener);
        }

        if (mRootView != null) {
            mRootView.addOnAttachStateChangeListener(this);
        }
    }

    private void initView() {
        contentView = (RelativeLayout) mRootView.findViewById(R.id.rl_common_title_bar);
        ivLeftView = (ImageView) contentView.findViewById(R.id.iv_title_left);
        ivCloseView = (ImageView) contentView.findViewById(R.id.iv_title_close);
        ivRightView = (ImageView) contentView.findViewById(R.id.iv_title_right);
        flRightView = (FrameLayout) contentView.findViewById(R.id.fl_title_right);

        tvRightTextView = (TextView) contentView.findViewById(R.id.tv_title_right);
        rlMiddleView = (RelativeLayout) contentView.findViewById(R.id.rl_title_middle);

        spliteLineView = contentView.findViewById(R.id.splite);
        spliteTitleLineView = contentView.findViewById(R.id.title_line);

        ivLeftView.setVisibility(hidenLeftView ? View.GONE : View.VISIBLE);


        if (isShowRightView || rightResId > 0 || !TextUtils.isEmpty(rightText)) {
            flRightView.setVisibility(View.VISIBLE);
        } else {
            flRightView.setVisibility(View.INVISIBLE);
        }


        ivCloseView.setVisibility(hidenCloseView ? View.GONE : View.VISIBLE);
        rlMiddleView.setVisibility(hidenMiddleView ? View.GONE : View.VISIBLE);

        spliteLineView.setVisibility(hidenSpliteLine ? View.GONE : View.VISIBLE);
        spliteTitleLineView.setVisibility(hidenTitleLine ? View.GONE : View.VISIBLE);

        if (middlerView != null) {
            rlMiddleView.addView(middlerView);
        }

//        int padding = SizeUtils.dp2px(4);
//        ivLeftView.setPadding(padding, padding, padding, padding);
//        ivCloseView.setPadding(padding, padding, padding, padding);

        ivLeftView.setImageDrawable(mContext.getResources().getDrawable(leftResId > 0 ? leftResId : R.drawable.fast_droid_arrow_white_left));
        ivRightView.setImageDrawable(mContext.getResources().getDrawable(rightResId > 0 ? rightResId : R.drawable.fast_droid_search));
        contentView.setBackgroundColor(mContext.getResources().getColor(bgColor > 0 ? bgColor : R.color.theme));

        if (!TextUtils.isEmpty(rightText)) {
            ivRightView.setVisibility(View.GONE);
            tvRightTextView.setVisibility(View.VISIBLE);
            tvRightTextView.setText(rightText);

            if (rightTextColor > 0) {
                tvRightTextView.setTextColor(mContext.getResources().getColor(rightTextColor));
            }
        } else if (rightResId > 0) {
            ivRightView.setVisibility(View.VISIBLE);
        }
    }

    public void setRightTextView(String text) {
        tvRightTextView.setText(text);
    }

    public void setRightTextColor(int color) {
        tvRightTextView.setTextColor(mContext.getResources().getColor(color));
    }

    public <T extends View> T getMiddlerView(Class<T> clazz) {
        if (middlerView == null) {
            return null;
        }
        return (T) middlerView;
    }

    public RelativeLayout getMiddlerRootView() {
        return rlMiddleView;
    }

    public ImageView getCloseView() {
        return ivCloseView;
    }

    public ImageView getRightImageView() {
        return ivRightView;
    }

    public FrameLayout getRightRootView() {
        return flRightView;
    }

    public ImageView getLeftView() {
        return ivLeftView;
    }

    @Override
    public void onViewAttachedToWindow(View view) {

    }

    @Override
    public void onViewDetachedFromWindow(View view) {
        onRelease();
    }

    public void onRelease() {
        mCloseListener = null;
        mLeftListener = null;
        mRightListener = null;

        if (mRootView != null) {
            mRootView.removeOnAttachStateChangeListener(this);
        }
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.iv_title_left) {
            if (mLeftListener != null) {
                mLeftListener.onClick(view);
            } else if (mContext instanceof Activity) {
                ((Activity) mContext).finish();
            }
        }
    }

    public final static class Builder {
        private boolean hideLeftView = false;
        private boolean hideMiddleView = false;
        private boolean isShowRightView = false;
        private boolean hideCloseView = true;
        private boolean hidenSpliteLine = false;
        private boolean hidenTitleLine = false;

        private int leftResId;
        private int rightResId;

        private int bgColor;
        private View mMiddlerView;
        private View mRootView;

        private Context mContext;
        private View.OnClickListener mLeftListener;
        private View.OnClickListener mRightListener;
        public View.OnClickListener mCloseListener;

        public String rightText;
        public int rightTextColor;

        public Builder(View rootView) {
            mRootView = rootView;
            mContext = rootView.getContext();
        }

        public Builder setLeftClickListener(View.OnClickListener listener) {
            mLeftListener = listener;
            return this;
        }

        public Builder setRightClickListener(View.OnClickListener listener) {
            mRightListener = listener;
            return this;
        }

        public Builder setCloseClickListener(View.OnClickListener listener) {
            mCloseListener = listener;
            return this;
        }

        public Builder hidenLeftView() {
            hideLeftView = true;
            return this;
        }

        public Builder hidenMiddleView() {
            hideMiddleView = true;
            return this;
        }

        public Builder showRightView() {
            isShowRightView = true;
            return this;
        }

        public Builder showRightTextView(String text, int color) {
            rightText = text;
            rightTextColor = color;
            return this;
        }

        public Builder showRightTextView(String text) {
            return showRightTextView(text, 0);
        }

        public Builder hidenCloseView(boolean isHiden) {
            hideCloseView = isHiden;
            return this;
        }

        public Builder hidenSpliteLine() {
            hidenSpliteLine = true;
            return this;
        }

        public Builder hidenTitleLine() {
            hidenTitleLine = true;
            return this;
        }

        public <T extends View> Builder setMiddlerView(T view) {
            mMiddlerView = view;
            return this;
        }

        public Builder setBackgroundColor(int color) {
            bgColor = color;
            return this;
        }

        public Builder setLeftDrawable(int resId) {
            leftResId = resId;
            return this;
        }

        public Builder setRightDrawable(int resId) {
            rightResId = resId;
            return this;
        }

        public CommonTitleBarManager build() {
            return new CommonTitleBarManager(this);
        }
    }

}
