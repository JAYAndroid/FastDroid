package com.ylz.ehui.http.manager;


import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ylz.ehui.http.INetParamsBuild;
import com.ylz.ehui.http.IRequestHandler;
import com.ylz.ehui.http.interceptor.HttpLoggingInterceptor;
import com.ylz.ehui.http.interceptor.NetInterceptor;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.CookieJar;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Author: yms
 * Date: 2017/10/11 13:36
 * Desc: Retrofit实现类
 */
final public class RetrofitManager {
    private static final long DEFAULT_TIME_OUT = 10 * 1000L;

    private INetParamsBuild sProvider = null;
    private Map<String, INetParamsBuild> providerMap;
    private Map<String, Retrofit> retrofitMap;
    private Map<String, OkHttpClient> clientMap;
    private Gson mGson;
    private String mBaseUrl;

    private RetrofitManager() {
        providerMap = new HashMap<>();
        retrofitMap = new HashMap<>();
        clientMap = new HashMap<>();

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


    public <S> S get(Class<S> service) {
        return getInstance().getRetrofit().create(service);
    }

    public void registerProvider(INetParamsBuild provider) {
        this.sProvider = provider;
    }

    public void registerProvider(String baseUrl, INetParamsBuild provider) {
        getInstance().providerMap.put(baseUrl, provider);
    }

    public INetParamsBuild getCommonProvider() {
        return sProvider;
    }

    public void clearCache() {
        retrofitMap.clear();
        clientMap.clear();
    }

    public Retrofit getRetrofit() {
        return getRetrofit(null);
    }

    public void setBaseUrl(String baseUrl) {
        mBaseUrl = baseUrl;
    }

    private Retrofit getRetrofit(INetParamsBuild provider) {
        if (empty(mBaseUrl)) {
            throw new IllegalStateException("baseUrl can not be null");
        }
        if (retrofitMap.get(mBaseUrl) != null) {
            return retrofitMap.get(mBaseUrl);
        }

        if (provider == null) {
            provider = providerMap.get(mBaseUrl);
            if (provider == null) {
                provider = sProvider;
            }
        }
        checkProvider(provider);

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(mBaseUrl)
                .client(getClient(mBaseUrl, provider))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(getGson()));

        Retrofit retrofit = builder.build();
        retrofitMap.put(mBaseUrl, retrofit);
        providerMap.put(mBaseUrl, provider);

        return retrofit;
    }

    public Gson getGson() {
        return mGson;
    }

    private boolean empty(String baseUrl) {
        return baseUrl == null || baseUrl.isEmpty();
    }

    private OkHttpClient getClient(String baseUrl, INetParamsBuild provider) {
        if (empty(baseUrl)) {
            throw new IllegalStateException("baseUrl can not be null");
        }
        if (clientMap.get(baseUrl) != null) {
            return clientMap.get(baseUrl);
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

        if (provider.configLogEnable()) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                @Override
                public void log(String message) {
                    //打印retrofit日志
                    Log.d("RetrofitLog = %s", message);
                }
            });

            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(loggingInterceptor);
        }

        OkHttpClient client = builder.build();
        clientMap.put(baseUrl, client);
        providerMap.put(baseUrl, provider);

        return client;
    }

    private boolean empty(Interceptor[] interceptors) {
        return interceptors == null || interceptors.length == 0;
    }

    private void checkProvider(INetParamsBuild provider) {
        if (provider == null) {
            throw new IllegalStateException("must register provider first");
        }
    }

    public Map<String, Retrofit> getRetrofitMap() {
        return retrofitMap;
    }

    public Map<String, OkHttpClient> getClientMap() {
        return clientMap;
    }

}
