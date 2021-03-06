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
import android.util.SparseArray;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nineoldandroids.view.ViewHelper;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.ylz.ehui.base_ui.R;
import com.ylz.ehui.ui.widget.MySmartRefreshLayout;
import com.ylz.ehui.utils.AppUtils;
import com.ylz.ehui.utils.SizeUtils;

/**
 * Created by yons on 2018/3/20.
 */

public class CommonFlexibleSpaceTitleManager implements View.OnAttachStateChangeListener, AppBarLayout.OnOffsetChangedListener {
    private View mRootView;
    private Context mContext;
    private FrameLayout mMainContentLayout;
    private final int mMainContentLayoutBg;
    private CommonTitleBarManager mCommonTitleBarManager;
    private SubHeadLayoutOnOffsetChangedListener mSubHeadLayoutOnOffsetChangedListener;
    private View.OnClickListener mRightListener;

    private TextView mFlexibleSpaceTitleView;
    private TextView mFlexibleSpaceSubTitleView;
    private View mFlexibleSpaceTitleLayout;
    private int mCollapsingToolbarLayoutColor;
    private View mFlexibleSpaceRightView;

    private MySmartRefreshLayout smartRefreshLayout;
    private OnRefreshListener onRefreshListener;
    private OnLoadMoreListener onLoadMoreListener;
    private View.OnClickListener mSubTitleListener;

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
    private int mFlexibleTitleColor;
    private String mSubTitle;
    private int mSubTitleResId;
    private int mFlexibleRightViewResId;
    private boolean mHidenFlexibleRightView;
    private boolean mHidenLocationView;
    private int mSubFlexibleSpaceContentHeight;

    private int mPadding;
    private boolean mDoSubFlexibleViewAnim = true;
    private boolean isFixed; // true-标题固定不可上下滑动

    private CommonFlexibleSpaceTitleManager(Builder builder) {
        this.mContext = builder.mContext;
        this.mRootView = builder.mRootView;
        this.mCommonTitleBarManager = builder.mCommonTitleBarManager;
        this.mSubHeadLayoutOnOffsetChangedListener = builder.mSubHeadLayoutOnOffsetChangedListener;
        this.mTitle = builder.mTitle;
        this.mFlexibleTitleColor = builder.mFlexibleTitleColor;
        this.mSubTitle = builder.mSubTitle;
        this.mSubTitleResId = builder.mSubTitleResId;
        this.mHidenFlexibleRightView = builder.mHidenFlexibleRightView;
        this.mHidenLocationView = builder.mHidenLocationView;
        this.mCustomSubFlexibleView = builder.mCustomSubFlexibleView;
        this.mCustomFlexibleView = builder.mCustomFlexibleView;
        this.mFlexibleRightViewResId = builder.mFlexibleRightViewResId;
        this.mRightListener = builder.mRightListener;
        this.mCollapsingToolbarLayoutColor = builder.mCollapsingToolbarLayoutColor;
        this.mMainContentLayoutBg = builder.mMainContentLayoutBg;
        this.onLoadMoreListener = builder.onLoadMoreListener;
        this.onRefreshListener = builder.onRefreshListener;
        this.isFixed = builder.isFixed;
        this.mSubTitleListener = builder.mSubTitleListener;

        initView();
        initListener();
    }

    private void initListener() {
        if (mRootView != null) {
            mRootView.addOnAttachStateChangeListener(this);
        }

        if (!isFixed) {
            mAppBarLayout.addOnOffsetChangedListener(this);
        }
    }

    private void initView() {
        if (mRootView == null) {
            throw new RuntimeException("activity布局需要<include layout=\"@layout/xxx_common_flexible_space_title\" />");
        }

        mViewCache = new SparseArray<>();
        toolbar = mRootView.findViewById(R.id.toolbar);
        mFlexibleSpaceTitleLayout = mRootView.findViewById(R.id.rl_flexible_title_layout);
        mFlexibleSpaceTitleLayout.measure(0, 0);
        smartRefreshLayout = mRootView.findViewById(R.id.refreshLayout);
        smartRefreshLayout.setEnableRefresh(onRefreshListener != null);
        smartRefreshLayout.setEnableLoadMore(onLoadMoreListener != null);

        if (onLoadMoreListener != null) {
            smartRefreshLayout.setOnLoadMoreListener(onLoadMoreListener);
        }

        if (onRefreshListener != null) {
            smartRefreshLayout.setOnRefreshListener(onRefreshListener);
        }

        if (mMainContentLayoutBg > 0) {
            mMainContentLayout = mRootView.findViewById(R.id.fl_main_content_layout);
            try {
                mMainContentLayout.setBackgroundResource(mMainContentLayoutBg);
            } catch (Exception e) {
                mMainContentLayout.setBackgroundColor(mMainContentLayoutBg);
            }
        }

        mFlexibleSpaceTitleView = mRootView.findViewById(R.id.tv_flexible_title);
        mFlexibleSpaceTitleView.measure(0, 0);

        mFlexibleSpaceSubTitleView = mRootView.findViewById(R.id.tv_flexible_sub_title);
        mFlexibleSpaceSubTitleView.measure(0, 0);
        if (!mHidenLocationView && !isFixed) {
            mFlexibleSpaceSubTitleView.setVisibility(View.VISIBLE);
            mFlexibleSpaceSubTitleView.setText(TextUtils.isEmpty(mSubTitle) ? "" : mSubTitle);
            mFlexibleSpaceSubTitleView.setCompoundDrawablesWithIntrinsicBounds(mSubTitleResId, 0, 0, 0);
        }

        if (mSubTitleListener != null) {
            mFlexibleSpaceSubTitleView.setOnClickListener(mSubTitleListener);
        }

        mFlexibleSpaceRightView = mRootView.findViewById(R.id.iv_flexible_right);

        if (mRightListener != null) {
            mFlexibleSpaceRightView.setOnClickListener(mRightListener);
        }

        mFlexibleContentRc = mRootView.findViewById(R.id.rc_flexible);
        mAppBarLayout = mRootView.findViewById(R.id.app_bar_layout);
        collapsingToolbarLayout = mRootView.findViewById(R.id.collapsing_toolbar_layout);
        if (mCollapsingToolbarLayoutColor > 0) {
            try {
                collapsingToolbarLayout.setBackgroundColor(mContext.getResources().getColor(mCollapsingToolbarLayoutColor));
            } catch (Exception e) {
                collapsingToolbarLayout.setBackgroundColor(mCollapsingToolbarLayoutColor);
            }
        }

        mFlexibleContentRc.setLayoutManager(new LinearLayoutManager(mContext));

        mFlexibleSpaceTitleView.setText(TextUtils.isEmpty(mTitle) ? "" : mTitle);
        if (mFlexibleTitleColor > 0) {
            try {
                mFlexibleSpaceTitleView.setTextColor(mContext.getResources().getColor(mFlexibleTitleColor));
            } catch (Exception e) {
                mFlexibleSpaceTitleView.setTextColor(mFlexibleTitleColor);
            }
        }

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

    public void setFlexibleTitle(String title) {
        if (mFlexibleSpaceTitleView != null) {
            mTitle = title;
            mFlexibleSpaceTitleView.setText(TextUtils.isEmpty(title) ? "" : title);
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

    public NestedScrollView getCustomFlexibleLayout() {
        return mCustomFlexibleLayout;
    }


    public void setFlexibleContentRvMargins(int left, int top, int right, int bottom) {
        if (mFlexibleContentRc != null) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mFlexibleContentRc.getLayoutParams();
            layoutParams.setMargins(left, top, right, bottom);
        }
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
        private View.OnClickListener mSubTitleListener;
        private int mCollapsingToolbarLayoutColor;
        private int mFlexibleTitleColor;
        private int mMainContentLayoutBg;
        private OnRefreshListener onRefreshListener;
        private OnLoadMoreListener onLoadMoreListener;
        private boolean isFixed = false;

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
            return this;
        }

        public Builder setLeftDrawable(int resId) {
            mCommonTitleBarManagerBuilder.setLeftDrawable(resId);
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

        public Builder setSubTitleClickListener(View.OnClickListener onClickListener) {
            mSubTitleListener = onClickListener;
            return this;
        }

        public Builder setOnOffsetChangedListener(SubHeadLayoutOnOffsetChangedListener subHeadLayoutOnOffsetChangedListener) {
            this.mSubHeadLayoutOnOffsetChangedListener = subHeadLayoutOnOffsetChangedListener;
            return this;
        }

        public Builder setFlexibleTitle(String title) {
            setFlexibleTitle(title, 0);
            return this;
        }

        public Builder setFlexibleTitle(String title, int colorRes) {
            this.mTitle = title;
            this.mFlexibleTitleColor = colorRes;
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

        public Builder setCollapsingToolbarLayoutColor(int colorRes) {
            this.mCollapsingToolbarLayoutColor = colorRes;
            return this;
        }

        public Builder setMainContentLayoutBg(int drawableRes) {
            this.mMainContentLayoutBg = drawableRes;
            return this;
        }

        public Builder setFixedTitleBgColor(int colorRes) {
            mCommonTitleBarManagerBuilder.setBackgroundColor(colorRes);
            return this;
        }

        public Builder setOnRefreshListener(OnRefreshListener onRefreshListener) {
            this.onRefreshListener = onRefreshListener;
            return this;
        }

        public Builder setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
            this.onLoadMoreListener = onLoadMoreListener;
            return this;
        }

        public Builder isFixed(boolean isFixed) {
            this.isFixed = isFixed;
            return this;
        }

        public Builder hidenLeftView() {
            mCommonTitleBarManagerBuilder.hidenLeftView();
            return this;
        }
    }

    public interface SubHeadLayoutOnOffsetChangedListener {
        void onOffsetChanged(float topOffset, float botomOffset);
    }

    public void doFinish() {
        if (smartRefreshLayout.getState() == RefreshState.Loading) {
            smartRefreshLayout.finishLoadMore();
        }

        if (smartRefreshLayout.getState() == RefreshState.Refreshing) {
            smartRefreshLayout.finishRefresh();
        }
    }
}
