package com.ylz.ehui.ui.mvp.view;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.ViewGroup;
import android.view.Window;

import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.ylz.ehui.ui.manager.AppManager;
import com.ylz.ehui.ui.manager.StatusBarManager;
import com.ylz.ehui.ui.mvp.presenter.BasePresenter;
import com.ylz.ehui.ui.proxy.LogicProxy;

import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public abstract class BaseActivity<T extends BasePresenter> extends RxAppCompatActivity implements BaseView {

    private BasePresenter mPresenter;
    private Unbinder bind;

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
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(getLayoutResource());
        AppManager.getInstance().addActivity(this);
        bind = ButterKnife.bind(this);
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
    public <T> void bind2Lifecycle(Observable<T> observable) {
        // 管理生命周期, 防止内存泄露
        observable.compose(this.<T>bindToLifecycle()).subscribe();
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
    protected void onDestroy() {
        super.onDestroy();
        if (bind != null) {
            bind.unbind();
        }

        LogicProxy.getInstance().unbind(getLogicClazz(), this);
        if (mPresenter != null) {
            mPresenter.detachView();
        }

        AppManager.getInstance().removeActivity(this);
    }

    protected int initStatusBarColor() {
        return Color.parseColor("#196FFA");
    }
}
