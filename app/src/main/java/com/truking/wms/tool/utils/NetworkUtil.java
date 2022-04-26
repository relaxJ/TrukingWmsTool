package com.truking.wms.tool.utils;

import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NetworkUtil {

    public okhttp3.Callback callback;

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public void post(String url, JSONObject parameters){
        //网络请求封装
        OkHttpClient mOkHttpClent = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request();
                        Response response = chain.proceed(request);
                        return response;
                    }
                })
                .connectTimeout(4000, TimeUnit.MILLISECONDS)
                .readTimeout(4000, TimeUnit.MILLISECONDS)
                .writeTimeout(4000, TimeUnit.MILLISECONDS)
                .build();
        //修改样式
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        //修改样式和上传json参数
        RequestBody requestBody = RequestBody.create(JSON, parameters.toString());
        //请求封装
        Request request = new okhttp3.Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        //发送网络请求
        mOkHttpClent.newCall(request).enqueue(
                this.callback
        );
    }
}
