package com.xinwo.network.interceptor;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class BasicParamsInterceptor implements Interceptor {
    Map<String, String> mHeads = new HashMap<>();
    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
       Request.Builder builder =  chain.request().newBuilder();
       for (String key : mHeads.keySet()) {
           builder.addHeader(key, Objects.requireNonNull(mHeads.get(key)));
       }
       Request newRequest = builder.build();
        return chain.proceed(newRequest);
    }

    public BasicParamsInterceptor addHeaderParam(String name, String value) {
        mHeads.put(name,value) ;
        return this;
    }
}
