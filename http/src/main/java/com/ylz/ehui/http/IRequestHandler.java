package com.ylz.ehui.http;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Author: yms
 * Date: 2017/10/11 13:37
 * Desc:
 */
public interface IRequestHandler {

    Request onBeforeRequest(Request request, Interceptor.Chain chain);

    Response onAfterRequest(Response response, Interceptor.Chain chain) throws IOException;

}
