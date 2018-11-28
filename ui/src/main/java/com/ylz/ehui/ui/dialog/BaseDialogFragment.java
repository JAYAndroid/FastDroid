package com.ylz.ehui.ui.dialog;


import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.MyDialogFragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.trello.rxlifecycle2.components.support.RxDialogFragment;
import com.ylz.ehui.ui.mvp.presenter.BasePresenter;
import com.ylz.ehui.ui.mvp.view.BaseView;
import com.ylz.ehui.ui.proxy.LogicProxy;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;

/**
 * Base dialog fragment for all your dialogs, styleable and same design on Android 2.2+.
 *
 * @author David Vávra (david@inmite.eu)
 */
public abstract class BaseDialogFragment<T extends BasePresenter> extends MyDialogFragment implements BaseView {
    protected Context mContext;
    private Builder builder;
    protected BasePresenter mPresenter;
    private List<Disposable> mSubscribers;
    private volatile boolean isDestroyed = false;
    private volatile boolean isShowing = false;
    private BaseDialogFragment mDialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        builder = new Builder(mContext, inflater, container);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mSubscribers = new ArrayList<>();
        mDialog = initDialog();
        onInitData2Remote();
        return build(builder).create();
    }

    protected BaseDialogFragment initDialog() {
        return new WaitDialog();
    }

    @Override
    public void showToast(String msg) {

    }

    @Override
    public void showDialog() {
        if (mDialog != null && getActivity() != null) {
            mDialog.show(getActivity());
        }
    }

    @Override
    public void dismissDialog() {
        if (mDialog != null && getActivity() != null) {
            mDialog.dismiss(getActivity());
        }
    }

    private Class<T> getLogicClazz() {
        Class<T> entityClass = null;
        Type t = this.getClass().getGenericSuperclass();
        if (t instanceof ParameterizedType) {
            Type[] p = ((ParameterizedType) t).getActualTypeArguments();
            entityClass = (Class) p[0];
            return entityClass;
        }

        return null;
    }

    protected void onInitData2Remote() {
        if (getLogicClazz() != null)
            mPresenter = getLogicImpl();
    }

    protected T getPresenter() {
        if (mPresenter != null) {
            return (T) mPresenter;
        }

        return null;
    }

    //获得该页面的实例
    public <T> T getLogicImpl() {
        return LogicProxy.getInstance().bind(getLogicClazz(), this);
    }

    @Override
    public void bind2Lifecycle(Disposable subscribe) {
        // 管理生命周期, 防止内存泄露
        if (!mSubscribers.contains(subscribe)) {
            mSubscribers.add(subscribe);
        }
    }

    @Override
    public void onError(String msg) {

    }

    @Override
    public void onPause() {
        super.onPause();
        if (isDetached()) {
            doDestroy();
        }
    }

    @Override
    public void onDestroy() {
        doDestroy();
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void doDestroy() {
        if (isDestroyed) {
            return;
        }

        for (Disposable subscriber : mSubscribers) {
            if (!subscriber.isDisposed()) {
                subscriber.dispose();
            }
        }

        LogicProxy.getInstance().unbind(getLogicClazz(), this);
        mSubscribers.clear();
        isDestroyed = true;
    }

    protected abstract void onInitialization(View parentVie, Bundle bundle);

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onInitialization(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        Window window = getDialog().getWindow();
        window.requestFeature(Window.FEATURE_NO_TITLE);
        super.onActivityCreated(savedInstanceState);
        getDialog().setCancelable(builder.mCancelable);
        getDialog().setCanceledOnTouchOutside(builder.CanceledOnTouchOutside);

        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        int height = WindowManager.LayoutParams.WRAP_CONTENT;
        int width = WindowManager.LayoutParams.MATCH_PARENT;

        if (builder.mWidthScale > 0) {
            width = (int) (builder.mWidthScale * displayMetrics.widthPixels);
        } else if (builder.mWidth > 0) {
            width = builder.mWidth;
        }

        if (builder.mHeightScale > 0) {
            height = (int) (builder.mHeightScale * displayMetrics.heightPixels);
        } else if (builder.mHeight > 0) {
            height = builder.mHeight;
        }

        if (builder.mGravity > 0) {
            window.setGravity(builder.mGravity);
        }

        window.setLayout(width, height);
    }

    protected abstract Builder build(Builder builder);

    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance()) {
            getDialog().setDismissMessage(null);
        }

        super.onDestroyView();
    }

    public void showAllowingStateLoss(FragmentManager manager, String tag) {
        FragmentTransaction ft = manager.beginTransaction();
        ft.remove(this);
        ft.add(this, tag);
        ft.commitAllowingStateLoss();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        isShowing = false;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        isShowing = false;
    }

    public void show(FragmentActivity activity) {
        try {
            FragmentManager supportFragmentManager = activity.getSupportFragmentManager();
            if (!isShowing && !isAdded() && null == supportFragmentManager.findFragmentByTag(getClass().getName())) {
                show(supportFragmentManager, getClass().getName());
                isShowing = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dismiss(FragmentActivity activity) {
        try {
            FragmentManager supportFragmentManager = activity.getSupportFragmentManager();
            if (isShowing && isAdded() && null != supportFragmentManager.findFragmentByTag(getClass().getName())) {
                dismissAllowingStateLoss();
                isShowing = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Custom dialog builder
     */
    protected static class Builder {
        private final Context mContext;
        private final ViewGroup mContainer;
        private View mCustomView;
        private LayoutInflater mLayoutInflater;
        private float mHeightScale;
        private float mWidthScale;
        private int mGravity;
        private boolean mCancelable;
        private boolean CanceledOnTouchOutside;
        private int mWidth;
        private int mHeight;


        public Builder(Context context, LayoutInflater inflater, ViewGroup container) {
            this.mContext = context;
            this.mLayoutInflater = inflater;
            this.mContainer = container;
            mCancelable = false;
            CanceledOnTouchOutside = false;
        }

        public Builder setView(@LayoutRes int layoutId) {
            mCustomView = mLayoutInflater.inflate(layoutId, mContainer, false);
            return this;
        }

        public View create() {
            return mCustomView;
        }

        public Builder heightScale(float scale) {
            this.mHeightScale = scale;
            return this;
        }

        public Builder widthScale(float scale) {
            this.mWidthScale = scale;
            return this;
        }

        public Builder width(int width) {
            this.mWidth = width;
            return this;
        }

        public Builder height(int height) {
            this.mHeight = height;
            return this;
        }

        public Builder setGravity(int gravity) {
            this.mGravity = gravity;
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            this.mCancelable = cancelable;
            return this;
        }

        public Builder setCanceledOnTouchOutside(boolean cancelable) {
            this.CanceledOnTouchOutside = cancelable;
            return this;
        }
    }
}