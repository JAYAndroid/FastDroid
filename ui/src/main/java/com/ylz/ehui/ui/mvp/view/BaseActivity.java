package com.ylz.ehui.ui.mvp.view;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.ViewGroup;
import android.view.Window;

import com.ylz.ehui.ui.dialog.BaseDialogFragment;
import com.ylz.ehui.ui.dialog.WaitDialog;
import com.ylz.ehui.ui.manager.AppManager;
import com.ylz.ehui.ui.manager.StatusBarManager;
import com.ylz.ehui.ui.mvp.presenter.BasePresenter;
import com.ylz.ehui.ui.mvp.view.BaseView;
import com.ylz.ehui.ui.proxy.LogicProxy;
import com.ylz.ehui.utils.ToastUtils;
import com.zhy.autolayout.AutoLayoutActivity;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.disposables.Disposable;

public abstract class BaseActivity<T extends BasePresenter> extends AutoLayoutActivity implements BaseView {
    private BasePresenter mPresenter;
    private Unbinder bind;
    private List<Disposable> mSubscribers;
    private boolean isDestroyed = false;

    private BaseDialogFragment mDialog;

    protected abstract int getLayoutResource();

    protected abstract void onInitialization(Bundle bundle);

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

    protected BaseDialogFragment initDialog() {
        return new WaitDialog();
    }

    protected T getPresenter() {
        if (mPresenter != null) {
            return (T) mPresenter;
        }

        return null;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        checkResource();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        StatusBarManager.setStatusBarColor(this, initStatusBarColor());
        super.onCreate(savedInstanceState);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        setContentView(getLayoutResource());
        AppManager.getInstance().addActivity(this);
        bind = ButterKnife.bind(this);
        mSubscribers = new ArrayList<>();
        mDialog = initDialog();
        this.onInitData2Remote();
        this.onInitialization(savedInstanceState);
    }

    //获得该页面的实例
    public <T> T getLogicImpl() {
        return LogicProxy.getInstance().bind(getLogicClazz(), this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mPresenter != null && !mPresenter.isViewBind()) {
            LogicProxy.getInstance().bind(getLogicClazz(), this);
        }
    }

    @Override
    public void bind2Lifecycle(Disposable subscribe) {
        // 管理生命周期, 防止内存泄露
        if (!mSubscribers.contains(subscribe)) {
            mSubscribers.add(subscribe);
        }
    }

    @Override
    public void showToast(String msg) {
        ToastUtils.showHint(msg);
    }

    protected void showDialog() {
        if (mDialog != null) {
            mDialog.show(this);
        }
    }

    protected void dismissDialog() {
        if (mDialog != null && mDialog.getDialog() != null && mDialog.getDialog().isShowing()) {
            mDialog.dismiss();
        }
    }

    @Override
    public void onError(String msg) {

    }

    protected ViewGroup getRootView() {
        checkResource();
        return (ViewGroup) ((ViewGroup) findViewById(android.R.id.content))
                .getChildAt(0);
    }

    protected void doBack() {
        AppManager.getInstance().finishActivity(this);
    }

    private void checkResource() {
        if (getLayoutResource() == 0) {
            throw new RuntimeException("getLayoutResource()需要返回有效的layout id");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            doDestroy();
        }
    }

    @Override
    protected void onDestroy() {
        doDestroy();
        super.onDestroy();
    }

    protected void doDestroy() {
        if (isDestroyed) {
            return;
        }

        // 资源回收
        if (bind != null) {
            bind.unbind();
        }

        for (Disposable subscriber : mSubscribers) {
            if (!subscriber.isDisposed()) {
                subscriber.dispose();
            }
        }

        LogicProxy.getInstance().unbind(getLogicClazz(), this);
        mSubscribers.clear();
        AppManager.getInstance().removeActivity(this);

        isDestroyed = true;
    }

    protected int initStatusBarColor() {
        return Color.parseColor("#196FFA");
    }
}
