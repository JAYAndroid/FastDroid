package com.ylz.ehui.ui.mvp.view;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.ViewGroup;
import android.view.Window;

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.ylz.ehui.ui.manager.AppManager;
import com.ylz.ehui.ui.manager.StatusBarManager;
import com.ylz.ehui.ui.mvp.presenter.BasePresenter;
import com.ylz.ehui.ui.proxy.LogicProxy;
import com.ylz.ehui.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.disposables.Disposable;

public abstract class BaseActivity<T extends BasePresenter> extends RxAppCompatActivity implements BaseView {
    private BasePresenter mPresenter;
    private Unbinder bind;
    private List<Disposable> mSubscribers;

    protected abstract int getLayoutResource();

    protected abstract void onInitialization(Bundle bundle);

    protected Class getLogicClazz() {
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        checkResource();
        StatusBarManager.setStatusBarColor(this, initStatusBarColor());
        super.onCreate(savedInstanceState);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(getLayoutResource());
        AppManager.getInstance().addActivity(this);
        bind = ButterKnife.bind(this);
        mSubscribers = new ArrayList<>();
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
        ToastUtils.showShort(msg);
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
    protected void onStop() {
        super.onStop();

        for (Disposable subscriber : mSubscribers) {
            if (!subscriber.isDisposed()) {
                subscriber.dispose();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bind != null) {
            bind.unbind();
        }

        LogicProxy.getInstance().unbind(getLogicClazz(), this);
        if (mPresenter != null) {
            mPresenter.detachView();
        }

        mSubscribers.clear();
        AppManager.getInstance().removeActivity(this);
    }

    protected int initStatusBarColor() {
        return Color.parseColor("#196FFA");
    }
}
