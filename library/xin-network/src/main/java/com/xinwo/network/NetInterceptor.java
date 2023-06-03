package com.xinwo.network;

import com.xinwo.log.LibLog;
import com.xinwo.log.LibToastUtils;
import com.xinwo.log.XinApplicationUtil;

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
        if (NetworkUtils.isNetworkConnected(XinApplicationUtil.Companion.getInstance().getMApplication())) {
            LibLog.d("NetInterceptor", "url==" + chain.request().method() + "==" + chain.request().url().toString());

            Response response = null;
            try {
                response = chain.proceed(chain.request());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        } else {
            LibLog.d("NetInterceptor", "没有网络连接");
            LibToastUtils.toastOnUiThread(XinApplicationUtil.Companion.getInstance().getMApplication(), "没有网络连接");
            //throw new LibException(LibException.CODE_NO_NET);
            return null;
        }
    }
}
