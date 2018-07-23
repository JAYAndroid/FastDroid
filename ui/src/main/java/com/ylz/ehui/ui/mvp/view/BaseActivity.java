package com.ylz.ehui.ui.mvp.view;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.ylz.ehui.base_ui.R;
import com.ylz.ehui.ui.dialog.BaseDialogFragment;
import com.ylz.ehui.ui.dialog.WaitDialog;
import com.ylz.ehui.ui.loadSir.LoadService;
import com.ylz.ehui.ui.loadSir.LoadSir;
import com.ylz.ehui.ui.loadSir.callback.Callback;
import com.ylz.ehui.ui.manager.AppManager;
import com.ylz.ehui.ui.manager.StatusBarManager;
import com.ylz.ehui.ui.mvp.presenter.BasePresenter;
import com.ylz.ehui.ui.proxy.LogicProxy;
import com.ylz.ehui.utils.ToastUtils;
import com.zhy.autolayout.AutoLayoutActivity;

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

public abstract class BaseActivity<T extends BasePresenter> extends AutoLayoutActivity implements BaseView {
    private BasePresenter mPresenter;
    private Unbinder bind;
    private List<Disposable> mSubscribers;
    private boolean isDestroyed = false;

    private BaseDialogFragment mDialog;
    protected LoadService mLoadService;

    @LayoutRes
    protected int getLayoutResource() {
        return R.layout.fastdroid_common_activity_layout;
    }

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
        if (isShowStatusBar()) {
            StatusBarManager.setStatusBarColor(this, initStatusBarColor());
        } else {
            StatusBarManager.translucentStatusBar(this, true);
        }
        super.onCreate(savedInstanceState);

        //缺省页
        mLoadService = LoadSir.getDefault().register(registerTarget(), new Callback.OnReloadListener() {
            @Override
            public void onReload(View v) {
                // 重新加载逻辑
                onLoadRefresh();
            }
        });
        mLoadService.showSuccess();

        EventBus.getDefault().register(this);
        setRequestedOrientation(initOrientation());
        setContentView(getLayoutResource());
        AppManager.getInstance().addActivity(this);
        bind = ButterKnife.bind(this);
        mSubscribers = new ArrayList<>();
        mDialog = initDialog();
        this.onInitData2Remote();
        this.onInitialization(savedInstanceState);
    }

    protected Object registerTarget() {
        return this;
    }

    /**
     * 网络数据刷新
     */
    protected void onLoadRefresh() {
    }

    protected boolean isShowStatusBar() {
        return true;
    }

    protected int initOrientation() {
        return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
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

    @Override
    public void showDialog() {
        if (mDialog != null) {
            mDialog.show(this);
        }
    }

    @Override
    public void dismissDialog() {
        if (mDialog != null) {
            mDialog.dismiss(this);
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

    @Override
    public void onBackPressed() {
        doBack();
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleEvent(Object event) {

    }

    @Override
    protected void onDestroy() {
        doDestroy();
        super.onDestroy();
        EventBus.getDefault().unregister(this);
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
        AppManager.getInstance().removeActivity(this);

        isDestroyed = true;
    }

    protected int initStatusBarColor() {
        return Color.parseColor("#196FFA");
    }
}
