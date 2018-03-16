package com.ylz.ehui.http.interceptor;

import android.support.annotation.NonNull;

import com.ylz.ehui.http.handler.IRequestHandler;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Author: yms
 * Date: 2017/10/11 13:48
 * Desc:
 */
public class NetInterceptor implements Interceptor {
    private IRequestHandler handler;

    public NetInterceptor(IRequestHandler handler) {
        this.handler = handler;
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        if (handler != null) {
            request = handler.onBeforeRequest(request, chain);
        }
        Response response = chain.proceed(request);
        if (handler != null) {
            Response tmp = handler.onAfterRequest(response, chain);
            if (tmp != null) {
                return tmp;
            }
        }
        return response;
    }
}
