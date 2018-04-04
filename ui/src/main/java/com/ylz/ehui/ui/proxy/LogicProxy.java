package com.ylz.ehui.ui.proxy;

import com.ylz.ehui.ui.mvp.presenter.BasePresenter;
import com.ylz.ehui.ui.mvp.view.BaseView;
import com.ylz.ehui.ui.annotation.Implement;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

public class LogicProxy {
    private Map<Class, Object> mPresenters;
    private static final LogicProxy mInstance = new LogicProxy();

    public static LogicProxy getInstance() {
        return mInstance;
    }

    private LogicProxy() {
        mPresenters = new HashMap<>();
    }

    public void init(Class clzz) {
//        if (clzz.isAnnotationPresent(Implement.class)) {
//            for (Annotation ann : clzz.getDeclaredAnnotations()) {
//                if (ann instanceof Implement) {
//                    try {
//                        mPresenters.put(clzz, ((Implement) ann).value().newInstance());
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }
        try {
            mPresenters.put(clzz, clzz.newInstance());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 初始化presenter add map
    public <T> T bind(Class clzz, BaseView var1) {
        if (!mPresenters.containsKey(clzz)) {
            init(clzz);
        }
        BasePresenter presenter = ((BasePresenter) mPresenters.get(clzz));
        if (presenter.getView() != null) {
            presenter.detachView();
        }

        presenter.attachView(var1);
        return (T) presenter;
    }

    // 解除绑定 移除map
    public void unbind(Class clzz, BaseView var1) {
        if (mPresenters.containsKey(clzz)) {
            BasePresenter presenter = ((BasePresenter) mPresenters.get(clzz));
            if (var1 != presenter.getView()) {
                if (presenter.getView() != null)
                    presenter.detachView();
                mPresenters.remove(clzz);
            }
        }
    }

}