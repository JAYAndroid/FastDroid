package com.ylz.ehui.ui.mvp.view;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.trello.rxlifecycle2.components.support.RxFragment;
import com.ylz.ehui.ui.dialog.BaseDialogFragment;
import com.ylz.ehui.ui.dialog.WaitDialog;
import com.ylz.ehui.ui.loadSir.LoadService;
import com.ylz.ehui.ui.loadSir.LoadSir;
import com.ylz.ehui.ui.loadSir.callback.Callback;
import com.ylz.ehui.ui.mvp.presenter.BasePresenter;
import com.ylz.ehui.ui.proxy.LogicProxy;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.disposables.Disposable;

public abstract class BaseFragment<T extends BasePresenter> extends RxFragment implements BaseView {

    protected BasePresenter mPresenter;
    protected View rootView;
    protected Context mContext = null;//context
    private Unbinder bind;
    private List<Disposable> mSubscribers;
    private boolean isDestroyed = false;

    private BaseDialogFragment mDialog;
    protected LoadService mLoadService;

    protected abstract int getLayoutResource();

    protected abstract void onInitView(Bundle savedInstanceState);

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getLayoutResource() != 0) {
            rootView = inflater.inflate(getLayoutResource(), null);
        } else {
            rootView = super.onCreateView(inflater, container, savedInstanceState);
        }

        bind = ButterKnife.bind(this, rootView);
        mSubscribers = new ArrayList<>();
        mDialog = initDialog();
        onInitData2Remote();

        //缺省页
        mLoadService = LoadSir.getDefault().register(registerTarget(), new Callback.OnReloadListener() {
            @Override
            public void onReload(View v) {
                // 重新加载逻辑
                onLoadRefresh();
            }
        });

        onInitView(savedInstanceState);

        mLoadService.showSuccess();
        return mLoadService.getLoadLayout();
    }

    protected Object registerTarget() {
        return rootView;
    }

    protected void onLoadRefresh() {
    }

    protected BaseDialogFragment initDialog() {
        return new WaitDialog();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        mContext = getActivity();
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
        super.onDestroy();
        doDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void showDialog() {
        if (mDialog != null) {
            mDialog.show(getActivity());
        }
    }

    @Override
    public void dismissDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }

    private void doDestroy() {
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
        isDestroyed = true;
    }


    @Override
    public void bind2Lifecycle(Disposable subscribe) {
        // 管理生命周期, 防止内存泄露
        if (!mSubscribers.contains(subscribe)) {
            mSubscribers.add(subscribe);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleEvent(Object event) {

    }

    @Override
    public void onError(String msg) {

    }

    //获得该页面的实例
    public <T> T getLogicImpl() {
        return LogicProxy.getInstance().bind(getLogicClazz(), this);
    }

    @Override
    public void onStart() {
        if (mPresenter != null && !mPresenter.isViewBind()) {
            LogicProxy.getInstance().bind(getLogicClazz(), this);
        }
        super.onStart();
    }
}
