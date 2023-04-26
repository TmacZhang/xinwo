package com.cavalry.androidlib.net;



import com.cavalry.androidlib.toolbox.exception.LibException;
import com.cavalry.androidlib.toolbox.utils.LibLog;
import com.cavalry.androidlib.toolbox.utils.LibToastUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * @author Cavalry Lin
 * @since 1.0.0
 */
public class NetInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        if (NetworkUtils.isNetworkConnected()) {
            LibLog.d("NetInterceptor", "url=="+chain.request().method()+"=="+ chain.request().url().toString());

            Response response = null;
            try{
                response = chain.proceed(chain.request());
            }catch (Exception e){
                e.printStackTrace();
            }
            return response;
        } else {
            LibLog.d("NetInterceptor", "没有网络连接");
            LibToastUtils.toastOnUiThread("没有网络连接");
            throw new LibException(LibException.CODE_NO_NET);
        }
    }
}
