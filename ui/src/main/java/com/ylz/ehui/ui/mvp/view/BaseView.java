package com.ylz.ehui.ui.mvp.view;

import io.reactivex.disposables.Disposable;

public interface BaseView {
    void bind2Lifecycle(Disposable subscribe);

    void showToast(String msg);

    void onError(String msg);

    void showDialog();

    void dismissDialog();
}
