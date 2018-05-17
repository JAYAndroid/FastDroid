package com.ylz.ehui.http.builder;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonWriter;
import com.ylz.ehui.http.base.BaseEntity;
import com.ylz.ehui.utils.SecurityUtils;
import com.ylz.ehui.utils.SignUtils;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.Buffer;
import retrofit2.Converter;
import retrofit2.Retrofit;

/********************
 * 日期：18/4/2  时间：上午10:23
 * 公司：易联众易惠科技有限公司
 * 注释：拦截器，网络请求出入参处理
 ********************/
public class DefaultInterceptBuild extends Converter.Factory {
    private final Gson gson;


    public DefaultInterceptBuild() {
        gson = new Gson();
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations,
                                                            Retrofit retrofit) {
        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        return new GsonResponseBodyConverter<>(gson, adapter);  //响应
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        //进行条件判断，如果传进来的Type不是class，则匹配失败
        if (!(type instanceof Class<?>)) {
            return null;
        }
        //进行条件判断，如果传进来的Type不是Map的实现类，则也匹配失败
        if (!Map.class.isAssignableFrom((Class<?>) type)) {
            return null;
        }

        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        return new GsonRequestBodyConverter<>(gson, adapter); //请求
    }


    /**
     * 对响应数据进行拦截，实现解密等业务逻辑
     *
     * @param <T>
     */
    class GsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
        private final Gson gson;
        private final TypeAdapter<T> adapter;

        GsonResponseBodyConverter(Gson gson, TypeAdapter<T> adapter) {
            this.gson = gson;
            this.adapter = adapter;
        }

        @Override
        public T convert(ResponseBody value) throws IOException {
            String response = value.string();
            BaseEntity baseEntity = gson.fromJson(response, BaseEntity.class);
            try {
                if (SignUtils.ENTRY) {
                    if (baseEntity.getEncryptData() == null) {
                        return adapter.read(gson.newJsonReader(new StringReader(JSON.toJSONString(baseEntity))));
                    }
                    String data = SecurityUtils.decryptByAES(baseEntity.getEncryptData(), SignUtils.APP_SECRET, SignUtils.APP_ID);
                    baseEntity.setParam(JSONObject.parse(data));
                }
                return adapter.read(gson.newJsonReader(new StringReader(JSON.toJSONString(baseEntity))));
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                value.close();
            }

            return adapter.read(gson.newJsonReader(new StringReader(JSON.toJSONString(baseEntity))));
        }
    }

    /**
     * 对请求进行拦截，实现加密等业务逻辑
     *
     * @param <T>
     */
    class GsonRequestBodyConverter<T> implements Converter<T, RequestBody> {
        private final MediaType MEDIA_TYPE = MediaType.parse("application/json; charset=UTF-8");
        private final Charset UTF_8 = Charset.forName("UTF-8");

        private final Gson gson;
        private final TypeAdapter<T> adapter;

        GsonRequestBodyConverter(Gson gson, TypeAdapter<T> adapter) {
            this.gson = gson;
            this.adapter = adapter;
        }

        @Override
        public RequestBody convert(T value) throws IOException {
            Map rawRequestParams = (Map) value;
            Map resultRequestParams = SignUtils.getRequest(rawRequestParams, String.valueOf(rawRequestParams.get("serviceId")));

            Buffer buffer = new Buffer();
            Writer writer = new OutputStreamWriter(buffer.outputStream(), UTF_8);
            JsonWriter jsonWriter = gson.newJsonWriter(writer);
            adapter.write(jsonWriter, (T) resultRequestParams);
            jsonWriter.close();
            return RequestBody.create(MEDIA_TYPE, buffer.readByteString());
        }
    }
}