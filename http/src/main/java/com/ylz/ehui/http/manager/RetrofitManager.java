package com.ylz.ehui.http.manager;


import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ylz.ehui.http.builder.DefaultInterceptBuild;
import com.ylz.ehui.http.builder.DefaultNetParamsBuild;
import com.ylz.ehui.http.builder.INetParamsBuild;
import com.ylz.ehui.http.handler.IRequestHandler;
import com.ylz.ehui.http.interceptor.HttpLoggingInterceptor;
import com.ylz.ehui.http.interceptor.NetInterceptor;
import com.ylz.ehui.utils.NetworkUtils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.CookieJar;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * Author: yms
 * Date: 2017/10/11 13:36
 * Desc: Retrofit实现类
 */
final public class RetrofitManager {
    private static final long DEFAULT_TIME_OUT = 10 * 1000L;
    private INetParamsBuild sProvider = null;
    private Retrofit mRetrofit;
    private Gson mGson;
    private Converter.Factory customConverterFactory;
    private OkHttpClient mOkHttpClient;
    private String mBaseUrl;

    private RetrofitManager() {
        sProvider = new DefaultNetParamsBuild();
        customConverterFactory = new DefaultInterceptBuild();
        mGson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();
    }

    private static class Singleton {
        private static RetrofitManager instance = new RetrofitManager();
    }


    public static RetrofitManager getInstance() {
        return Singleton.instance;
    }


    public void setcustomConverterFactory(Converter.Factory factory) {
        customConverterFactory = factory;
    }

    public <S> S get(Class<S> service) {
        return getInstance().getRetrofit().create(service);
    }

    public void registerProvider(INetParamsBuild provider) {
        this.sProvider = provider;
    }

    public INetParamsBuild getCommonProvider() {
        return sProvider;
    }

    public Retrofit getRetrofit() {
        return getRetrofit(sProvider);
    }

    public void setBaseUrl(String baseUrl) {
        if (!TextUtils.isEmpty(baseUrl) && !baseUrl.endsWith("/")) {
            baseUrl = baseUrl + "/";
        }
        mBaseUrl = baseUrl;
        RetrofitBaseUrlManager.getInstance().setGlobalBaseUrl(mBaseUrl);
    }

    public Retrofit getRetrofit(INetParamsBuild provider) {
        if (empty(mBaseUrl)) {
            throw new RuntimeException("mBaseUrl为空，请先调用setBaseUrl");
        }
        if (mRetrofit != null) {
            return mRetrofit;
        }

        if (provider == null) {
            provider = sProvider;
        }

        checkProvider(provider);

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(mBaseUrl)
                .client(getClient(provider))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(customConverterFactory);

        mRetrofit = builder.build();
        return mRetrofit;
    }

    public Gson getGson() {
        return mGson;
    }

    private boolean empty(String baseUrl) {
        return baseUrl == null || baseUrl.isEmpty();
    }

    private OkHttpClient getClient(INetParamsBuild provider) {
        if (mOkHttpClient != null) {
            return mOkHttpClient;
        }

        checkProvider(provider);
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(provider.configConnectTimeoutSecs() != 0
                ? provider.configConnectTimeoutSecs()
                : DEFAULT_TIME_OUT, TimeUnit.SECONDS);
        builder.readTimeout(provider.configReadTimeoutSecs() != 0
                ? provider.configReadTimeoutSecs() : DEFAULT_TIME_OUT, TimeUnit.SECONDS);

        builder.writeTimeout(provider.configWriteTimeoutSecs() != 0
                ? provider.configReadTimeoutSecs() : DEFAULT_TIME_OUT, TimeUnit.SECONDS);
        CookieJar cookieJar = provider.configCookie();
        if (cookieJar != null) {
            builder.cookieJar(cookieJar);
        }
        provider.configHttps(builder);

        builder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                if (!NetworkUtils.isConnected()) {
                    throw new RuntimeException("网络连接不可用");
                }
                return chain.proceed(chain.request());
            }
        });

        IRequestHandler handler = provider.configHandler();
        if (handler != null) {
            builder.addInterceptor(new NetInterceptor(handler));
        }

        Interceptor[] interceptors = provider.configInterceptors();
        if (!empty(interceptors)) {
            for (Interceptor interceptor : interceptors) {
                builder.addInterceptor(interceptor);
            }
        }

//        if (provider.configLogEnable()) {
//            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
//                @Override
//                public void log(String message) {
//                    //打印retrofit日志
//                    Log.d("RetrofitLog = %s", message);
//                }
//            });
//
//            HttpLoggingInterceptor.Level level = provider.configLogLevel();
//            if (level == null) {
//                level = HttpLoggingInterceptor.Level.BODY;
//            }
//
//            loggingInterceptor.setLevel(level);
//            builder.addInterceptor(loggingInterceptor);
//        }

        mOkHttpClient = RetrofitBaseUrlManager.getInstance().with(builder).build();
        return mOkHttpClient;
    }

    private boolean empty(Interceptor[] interceptors) {
        return interceptors == null || interceptors.length == 0;
    }

    private void checkProvider(INetParamsBuild provider) {
        if (provider == null) {
            throw new IllegalStateException("must register provider first");
        }
    }
}
