package com.ylz.ehui.http.builder;

import com.ylz.ehui.http.handler.IRequestHandler;
import com.ylz.ehui.http.interceptor.HttpLoggingInterceptor;

import io.reactivex.annotations.CheckReturnValue;
import io.reactivex.annotations.NonNull;
import okhttp3.CookieJar;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

/**
 * Author: yms
 * Date: 2017/10/11 13:36
 * Desc: 网络参数设置
 */
public interface INetParamsBuild {

    Interceptor[] configInterceptors();

    void configHttps(OkHttpClient.Builder builder);

    CookieJar configCookie();

    IRequestHandler configHandler();

    long configConnectTimeoutSecs();

    long configReadTimeoutSecs();

    long configWriteTimeoutSecs();

    boolean configLogEnable();

    HttpLoggingInterceptor.Level configLogLevel();

}
