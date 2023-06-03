package com.xinwo.network;

import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Url;
import rx.Observable;

/**
 * body提交的post请求
 * @author Cavalry Lin
 * @since 1.0.0
 */

public interface PostBodyService {
    /**
     *
     * @param url 完整的url地址
     * @param requestBody　参数
     * @return
     */
    @POST
    Observable<ResponseBody> post(@HeaderMap Map<String,String> headers, @Url String url, @Body RequestBody requestBody);
}
