package com.cavalry.androidlib.mvp.service;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;
import rx.Observable;

/**
 * @author Cavalry Lin
 * @since 1.0.0
 */

public interface GetService {
    /**
     *
     * @param url 完整的url地址
     * @param params　参数(如果没有参数,需传递一个空的Map)
     * @return
     */
    @GET
    Observable<ResponseBody> get(@Url String url, @QueryMap Map<String, String> params);
}
