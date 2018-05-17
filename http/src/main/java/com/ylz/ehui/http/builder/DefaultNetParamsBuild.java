package com.ylz.ehui.http.builder;

import com.ylz.ehui.http.handler.IRequestHandler;
import com.ylz.ehui.http.interceptor.HttpLoggingInterceptor;
import com.ylz.ehui.utils.Utils;

import okhttp3.CookieJar;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.internal.Util;

/**
 * Created by yms on 2018/3/16.
 */

public class DefaultNetParamsBuild implements INetParamsBuild {
    private final int TIME_OUT_SEC = 60;

    @Override
    public Interceptor[] configInterceptors() {
        return new Interceptor[0];
    }

    @Override
    public void configHttps(OkHttpClient.Builder builder) {

    }

    @Override
    public CookieJar configCookie() {
        return null;
    }

    @Override
    public IRequestHandler configHandler() {
        return null;
    }

    @Override
    public long configConnectTimeoutSecs() {
        return TIME_OUT_SEC;
    }

    @Override
    public long configReadTimeoutSecs() {
        return TIME_OUT_SEC;
    }

    @Override
    public long configWriteTimeoutSecs() {
        return TIME_OUT_SEC;
    }

    @Override
    public boolean configLogEnable() {
        return false;
    }

    @Override
    public HttpLoggingInterceptor.Level configLogLevel() {
        return Utils.isDebug() ? HttpLoggingInterceptor.Level.ALL : HttpLoggingInterceptor.Level.NONE;
    }
}
