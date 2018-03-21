package com.ylz.ehui.ui.mvp.view;

import io.reactivex.Observable;

public interface BaseView {
    <T> void bind2Lifecycle(Observable<T> observable);
    void showToast(String msg);
}
