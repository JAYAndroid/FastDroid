package com.ylz.ehui.ui.manager;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.SparseArray;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nineoldandroids.view.ViewHelper;
import com.ylz.ehui.base_ui.R;
import com.ylz.ehui.utils.AppUtils;
import com.ylz.ehui.utils.SizeUtils;

/**
 * Created by yons on 2018/3/20.
 */

public class CommonFlexibleSpaceTitleManager implements View.OnAttachStateChangeListener, AppBarLayout.OnOffsetChangedListener {
    private View mRootView;
    private Context mContext;
    private CommonTitleBarManager mCommonTitleBarManager;
    private SubHeadLayoutOnOffsetChangedListener mSubHeadLayoutOnOffsetChangedListener;
    private View.OnClickListener mRightListener;

    private TextView mFlexibleSpaceTitleView;
    private TextView mFlexibleSpaceSubTitleView;
    private View mFlexibleSpaceTitleLayout;
    private View mFlexibleSpaceRightView;

    private NestedScrollView mCustomFlexibleLayout;
    private View mCustomFlexibleView;
    private View mCustomSubFlexibleView;
    private SparseArray<View> mViewCache;

    private RelativeLayout mSubHeadLayout;
    private int mTotalFlexibleSpaceHeight;

    private Toolbar toolbar;
    private RecyclerView mFlexibleContentRc;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private AppBarLayout mAppBarLayout;

    private String mTitle;
    private String mSubTitle;
    private int mSubTitleResId;
    private int mFlexibleRightViewResId;
    private boolean mHidenFlexibleRightView;
    private boolean mHidenLocationView;
    private int mSubFlexibleSpaceContentHeight;

    private int mPadding;
    private boolean mDoSubFlexibleViewAnim = true;

    private CommonFlexibleSpaceTitleManager(Builder builder) {
        this.mContext = builder.mContext;
        this.mRootView = builder.mRootView;
        this.mCommonTitleBarManager = builder.mCommonTitleBarManager;
        this.mSubHeadLayoutOnOffsetChangedListener = builder.mSubHeadLayoutOnOffsetChangedListener;
        this.mTitle = builder.mTitle;
        this.mSubTitle = builder.mSubTitle;
        this.mSubTitleResId = builder.mSubTitleResId;
        this.mHidenFlexibleRightView = builder.mHidenFlexibleRightView;
        this.mHidenLocationView = builder.mHidenLocationView;
        this.mCustomSubFlexibleView = builder.mCustomSubFlexibleView;
        this.mCustomFlexibleView = builder.mCustomFlexibleView;
        this.mFlexibleRightViewResId = builder.mFlexibleRightViewResId;
        this.mRightListener = builder.mRightListener;

        initView();
        initListener();
    }

    private void initListener() {
        if (mRootView != null) {
            mRootView.addOnAttachStateChangeListener(this);
        }

        mAppBarLayout.addOnOffsetChangedListener(this);
    }

    private void initView() {
        if (mRootView == null) {
            throw new RuntimeException("activity布局需要<include layout=\"@layout/xxx_common_flexible_space_title\" />");
        }

        mViewCache = new SparseArray<>();
        toolbar = mRootView.findViewById(R.id.toolbar);
        mFlexibleSpaceTitleLayout = mRootView.findViewById(R.id.rl_flexible_title_layout);
        mFlexibleSpaceTitleLayout.measure(0,0);
        mFlexibleSpaceTitleView = mRootView.findViewById(R.id.tv_flexible_title);
        mFlexibleSpaceTitleView.measure(0, 0);

        mFlexibleSpaceSubTitleView = mRootView.findViewById(R.id.tv_flexible_sub_title);
        mFlexibleSpaceSubTitleView.measure(0,0);
        if (!mHidenLocationView) {
            mFlexibleSpaceSubTitleView.setVisibility(View.VISIBLE);
            mFlexibleSpaceSubTitleView.setText(TextUtils.isEmpty(mSubTitle) ? "" : mSubTitle);
            mFlexibleSpaceSubTitleView.setCompoundDrawablesWithIntrinsicBounds(mSubTitleResId, 0, 0, 0);
        }

        mFlexibleSpaceRightView = mRootView.findViewById(R.id.iv_flexible_right);

        if (mRightListener != null) {
            mFlexibleSpaceRightView.setOnClickListener(mRightListener);
        }

        mFlexibleContentRc = mRootView.findViewById(R.id.rc_flexible);
        mAppBarLayout = mRootView.findViewById(R.id.app_bar_layout);
        collapsingToolbarLayout = mRootView.findViewById(R.id.collapsing_toolbar_layout);

        mFlexibleContentRc.setLayoutManager(new LinearLayoutManager(mContext));

        mFlexibleSpaceTitleView.setText(TextUtils.isEmpty(mTitle) ? "" : mTitle);


        mTotalFlexibleSpaceHeight = mContext.getResources().getDimensionPixelSize(R.dimen.flexible_space_show_height);
        mAppBarLayout.getLayoutParams().height = mTotalFlexibleSpaceHeight;
        mSubFlexibleSpaceContentHeight = mContext.getResources().getDimensionPixelSize(R.dimen.sub_flexible_space_content_height);
        mPadding = SizeUtils.dp2px(20);

        if (mFlexibleRightViewResId > 0) {
            mFlexibleSpaceRightView.setBackgroundResource(mFlexibleRightViewResId);
        }

        if (mCustomSubFlexibleView != null) {
            mTotalFlexibleSpaceHeight += mSubFlexibleSpaceContentHeight + SizeUtils.dp2px(10);
            mAppBarLayout.getLayoutParams().height = mTotalFlexibleSpaceHeight;
            mFlexibleSpaceTitleLayout.getLayoutParams().height = mTotalFlexibleSpaceHeight;

            mSubHeadLayout = mRootView.findViewById(R.id.rl_flexible_sub_head_layout);
            mSubHeadLayout.addView(mCustomSubFlexibleView);
            mSubHeadLayout.setVisibility(View.VISIBLE);
        }

        if (mHidenFlexibleRightView) {
            mFlexibleSpaceRightView.setVisibility(View.INVISIBLE);
        }

        if (mCustomFlexibleView != null) {
            mCustomFlexibleLayout = mRootView.findViewById(R.id.ns_custom_flexible_layout);
            mCustomFlexibleLayout.setVisibility(View.VISIBLE);
            mCustomFlexibleLayout.addView(mCustomFlexibleView);
            mFlexibleContentRc.setVisibility(View.GONE);
        }
    }

    public void setSubTitleText(String message) {
        if (mFlexibleSpaceSubTitleView != null) {
            mFlexibleSpaceSubTitleView.setText(TextUtils.isEmpty(message) ? "" : message);
        }
    }

    public void setCustomSubFlexibleViewVisibility(boolean isVisible) {
        mDoSubFlexibleViewAnim = isVisible;
        int base = mSubFlexibleSpaceContentHeight + SizeUtils.dp2px(10);

        if (isVisible) {
            mTotalFlexibleSpaceHeight += base;
        } else {
            mTotalFlexibleSpaceHeight -= base;
        }

        mAppBarLayout.getLayoutParams().height = mTotalFlexibleSpaceHeight;
    }

    public View getMiddlerRootView() {
        return mCommonTitleBarManager.getMiddlerRootView();
    }

    public View getSubHeadLayout() {
        return mSubHeadLayout;
    }

    public View getRightRootView() {
        return mCommonTitleBarManager.getRightRootView();
    }

    public Toolbar getToolBar() {
        return toolbar;
    }

    public RecyclerView getFlexibleContentRv() {
        return mFlexibleContentRc;
    }

    @Override
    public void onViewAttachedToWindow(View view) {

    }

    @Override
    public void onViewDetachedFromWindow(View view) {
        onRelease();
    }

    public void onRelease() {
        if (mRootView != null) {
            mRootView.removeOnAttachStateChangeListener(this);
        }

        if (mCommonTitleBarManager != null) {
            mCommonTitleBarManager.onRelease();
        }

        if (mAppBarLayout != null) {
            mAppBarLayout.removeOnOffsetChangedListener(this);
        }

        if (mSubHeadLayoutOnOffsetChangedListener != null) {
            mSubHeadLayoutOnOffsetChangedListener = null;
        }

        if (mViewCache.size() > 0) {
            mViewCache.clear();
            mViewCache = null;
        }

    }

    public void setFlexibleContentAdapter(RecyclerView.Adapter adapter) {
        mFlexibleContentRc.setAdapter(adapter);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        float baseMaxTitleTransY = mTotalFlexibleSpaceHeight
                - mFlexibleSpaceTitleView.getMeasuredHeight()
                - 2 * SizeUtils.dp2px(12)
                - mFlexibleSpaceSubTitleView.getMeasuredHeight()
                - mFlexibleSpaceTitleLayout.getPaddingTop();

        float maxLocationTransY = mTotalFlexibleSpaceHeight
                - mFlexibleSpaceTitleView.getMeasuredHeight()
                - SizeUtils.dp2px(12);

        float flexibleTitleTransY = baseMaxTitleTransY;

        if (mCustomSubFlexibleView != null && mDoSubFlexibleViewAnim) {
            // 留出空间，用来显示子标题内容
            baseMaxTitleTransY = baseMaxTitleTransY - mSubFlexibleSpaceContentHeight - mPadding;
            maxLocationTransY = maxLocationTransY - mSubFlexibleSpaceContentHeight - mPadding;
            flexibleTitleTransY = baseMaxTitleTransY + mPadding / 2;
        }

        float maxTransX = (mFlexibleSpaceTitleLayout.getMeasuredWidth()
                - mFlexibleSpaceTitleView.getMeasuredWidth()) / 2
                - mFlexibleSpaceSubTitleView.getPaddingLeft();

        // 缩放因子，取值范围 1～0
        float scale = ((float) (mTotalFlexibleSpaceHeight - AppUtils.getActionBarSize()
                - Math.abs(verticalOffset)) / (mTotalFlexibleSpaceHeight - AppUtils.getActionBarSize()));

        // 标题信息动画
        ViewHelper.setTranslationY(mFlexibleSpaceTitleView, flexibleTitleTransY * scale);
        ViewHelper.setTranslationX(mFlexibleSpaceTitleView, -maxTransX * scale);

        ViewHelper.setPivotY(mFlexibleSpaceTitleView, 0);// 设置动画锚点
        ViewHelper.setPivotX(mFlexibleSpaceTitleView, 0);

        ViewHelper.setScaleX(mFlexibleSpaceTitleView, Math.max(1, scale + 0.6f));
        ViewHelper.setScaleY(mFlexibleSpaceTitleView, Math.max(1, scale + 0.6f));

        // 头部子标题布局动画
        if (mCustomSubFlexibleView != null && mDoSubFlexibleViewAnim) {
            if (mSubHeadLayoutOnOffsetChangedListener != null) {
                mSubHeadLayoutOnOffsetChangedListener.onOffsetChanged(baseMaxTitleTransY, scale);
            }
        }

        // 定位信息动画
        if (!mHidenLocationView) {
            ViewHelper.setTranslationY(mFlexibleSpaceSubTitleView, maxLocationTransY * scale);
            ViewHelper.setAlpha(mFlexibleSpaceSubTitleView, scale);
        }

        // 搜索按钮动画
        if (!mHidenFlexibleRightView) {
            ViewHelper.setScaleX(mFlexibleSpaceRightView, Math.max(1, scale + 0.4f));
            ViewHelper.setScaleY(mFlexibleSpaceRightView, Math.max(1, scale + 0.4f));
            ViewHelper.setTranslationY(mFlexibleSpaceRightView, baseMaxTitleTransY * scale);
        }
    }

    public View getCustomFlexibleView() {
        return mCustomFlexibleView;
    }

    public <T extends View> T getCustomViewById(int viewId) {
        if (mCustomFlexibleView == null) {
            return null;
        }

        if (mViewCache.get(viewId) != null) {
            return (T) mViewCache.get(viewId);
        }
        T targView = mCustomFlexibleView.findViewById(viewId);
        mViewCache.put(viewId, targView);
        return targView;
    }

    public <T extends View> T getSuvFlexibleViewById(@IdRes int viewId) {
        if (mCustomSubFlexibleView == null) {
            return null;
        }

        if (mViewCache.get(viewId) != null) {
            return (T) mViewCache.get(viewId);
        }
        T targView = mCustomSubFlexibleView.findViewById(viewId);
        mViewCache.put(viewId, targView);
        return targView;
    }

    public final static class Builder {
        private View mRootView;
        private Context mContext;
        private SubHeadLayoutOnOffsetChangedListener mSubHeadLayoutOnOffsetChangedListener;

        private CommonTitleBarManager.Builder mCommonTitleBarManagerBuilder;
        private CommonTitleBarManager mCommonTitleBarManager;
        private String mTitle;
        private int mSubTitleResId;
        private String mSubTitle;
        private boolean mHidenFlexibleRightView = false;
        private boolean mHidenLocationView = false;
        private View mCustomFlexibleView;
        private View mCustomSubFlexibleView;
        private int mFlexibleRightViewResId;
        private View.OnClickListener mRightListener;

        public Builder(View rootView) {
            mRootView = rootView;
            mContext = rootView.getContext();
            mCommonTitleBarManagerBuilder = new CommonTitleBarManager.Builder(rootView);
        }

        public CommonFlexibleSpaceTitleManager build() {
            mCommonTitleBarManager = mCommonTitleBarManagerBuilder.build();
            return new CommonFlexibleSpaceTitleManager(this);
        }

        public Builder hidenSpliteLine() {
            mCommonTitleBarManagerBuilder.hidenSpliteLine();
            return this;
        }

        public Builder hidenTitleLine() {
            mCommonTitleBarManagerBuilder.hidenTitleLine();
            return this;
        }

        public Builder setRightDrawable(int resId) {
            mFlexibleRightViewResId = resId;
//            mCommonTitleBarManagerBuilder.setRightDrawable(resId);
            return this;
        }

        public Builder setMiddlerView(View view) {
            mCommonTitleBarManagerBuilder.setMiddlerView(view);
            return this;
        }

        public Builder setLeftClickListener(View.OnClickListener onClickListener) {
            mCommonTitleBarManagerBuilder.setLeftClickListener(onClickListener);
            return this;
        }

        public Builder setRightClickListener(View.OnClickListener onClickListener) {
            mRightListener = onClickListener;
            mCommonTitleBarManagerBuilder.setRightClickListener(onClickListener);
            return this;
        }

        public Builder setOnOffsetChangedListener(SubHeadLayoutOnOffsetChangedListener subHeadLayoutOnOffsetChangedListener) {
            this.mSubHeadLayoutOnOffsetChangedListener = subHeadLayoutOnOffsetChangedListener;
            return this;
        }

        public Builder setFlexibleTitle(String title) {
            this.mTitle = title;
            return this;
        }

        public Builder setFlexibleSubTitleDrawableLeft(int resId) {
            this.mSubTitleResId = resId;
            return this;
        }

        public Builder setFlexibleSubTitle(String subTitle) {
            this.mSubTitle = subTitle;
            return this;
        }

        public Builder hidenFlexibleRightView() {
            this.mHidenFlexibleRightView = true;
            return this;
        }

        public Builder hidenLocationView() {
            this.mHidenLocationView = true;
            return this;
        }

        public Builder setCustomFlexibleView(View customView) {
            this.mCustomFlexibleView = customView;
            return this;
        }

        public Builder setCustomSubFlexibleView(View customView) {
            this.mCustomSubFlexibleView = customView;
            return this;
        }
    }

    public interface SubHeadLayoutOnOffsetChangedListener {
        void onOffsetChanged(float topOffset, float botomOffset);
    }
}
