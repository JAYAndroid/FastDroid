package com.ylz.ehui.http.base;


import android.text.TextUtils;

import com.google.gson.Gson;
import com.ylz.ehui.http.manager.RetrofitManager;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Author: yms
 * Date: 2017/10/11 13:46
 * Desc: 业务基础类
 */
public class BaseBusiness<T> {

    protected boolean openEncrypt;
    protected Gson mGson;

    public BaseBusiness() {
        openEncrypt = true;
        mGson = RetrofitManager.getInstance().getGson();
    }

    protected T businessClient() {
        return RetrofitManager.getInstance().getRetrofit().create(getType());
    }

    //指定观察者与被观察者线程
    protected <T> ObservableTransformer<T, T> normalSchedulers() {
        return new ObservableTransformer<T, T>() {

            @Override
            public ObservableSource<T> apply(Observable<T> upstream) {
                return upstream.onTerminateDetach().subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    protected <T> Observable<T> startScheduler(Observable<T> observable) {
        return observable.compose(this.<T>normalSchedulers());
    }

    protected RequestBody json2Body(Map<String, String> params) {
        return RequestBody.create(MediaType.parse("application/json"), mGson.toJson(params));
    }

    private Class<T> getType() {
        Class<T> entityClass = null;
        Type t = getClass().getGenericSuperclass();
        Type[] p = ((ParameterizedType) t).getActualTypeArguments();
        entityClass = (Class<T>) p[0];
        return entityClass;
    }

    protected Map joinRequest(Map params, String service) {
        if (params == null) {
            params = new HashMap();
        }

        if (TextUtils.isEmpty(service)) {
            service = "";
        }

        params.put("serviceId", service);
        return params;
    }

}
