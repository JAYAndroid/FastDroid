package com.ylz.ehui.http.builder;

import com.ylz.ehui.http.handler.IRequestHandler;

import okhttp3.CookieJar;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

/**
 * Created by yms on 2018/3/16.
 */

public class DefaultNetParamsBuild implements INetParamsBuild {
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
        return 0;
    }

    @Override
    public long configReadTimeoutSecs() {
        return 0;
    }

    @Override
    public long configWriteTimeoutSecs() {
        return 0;
    }

    @Override
    public boolean configLogEnable() {
        return false;
    }
}
