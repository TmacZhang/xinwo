package com.xinwo.network;

import android.os.Build;

import com.xinwo.log.LibLog;
import com.xinwo.network.interceptor.BasicParamsInterceptor;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.EventListener;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetManager {
    public static final String TAG = "NetManager";
    private static OkHttpClient mClient;
    private static volatile Retrofit sRetrofit;
    private final static long DEFAULT_TIME_OUT = 10;

    public static OkHttpClient getOkHttpClient() {
        if (mClient == null) {
            synchronized (DefaultNetManager.class) {
                if (mClient == null) {
                    mClient = getUnsafeOkHttpClient();
                }
            }
        }
        return mClient;
    }

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");


    public String post(String url, String json) throws IOException {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(20000L, TimeUnit.MILLISECONDS)
                .readTimeout(20000L, TimeUnit.MILLISECONDS)
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                })
                .build();

        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    public String postForm(String url) throws IOException {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(20000L, TimeUnit.MILLISECONDS)
                .readTimeout(20000L, TimeUnit.MILLISECONDS)
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                })
                .build();

        RequestBody body = new MultipartBody.Builder()
                .addFormDataPart("username", "admin")
                .addFormDataPart("password", "123456")
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }


    public String get(String url) throws IOException {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(20000L, TimeUnit.MILLISECONDS)
                .readTimeout(20000L, TimeUnit.MILLISECONDS)
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                })
                .build();


        Request request = new Request.Builder()
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (response.body() != null) {
                return response.body().string();
            }
        } catch (Exception e) {
            LibLog.e(TAG, e.getMessage());
        }
        return "";
    }

    public static OkHttpClient getUnsafeOkHttpClient() {

        try {
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            final javax.net.ssl.SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory);

            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            return builder.build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Retrofit getRetrofit(String url) {
        if (sRetrofit == null) {
            synchronized (NetManager.class) {
                if (sRetrofit == null) {
                    OkHttpClient.Builder builder = new OkHttpClient.Builder();
                    builder.connectTimeout(DEFAULT_TIME_OUT, TimeUnit.SECONDS);//连接超时时间
                    builder.writeTimeout(DEFAULT_TIME_OUT, TimeUnit.SECONDS);//写操作 超时时间
                    builder.readTimeout(DEFAULT_TIME_OUT, TimeUnit.SECONDS);//读操作超时时间

                    // 添加公共参数拦截器
                    BasicParamsInterceptor basicParamsInterceptor = new BasicParamsInterceptor()
                            .addHeaderParam("userName", "xxx")//添加公共参数
                            .addHeaderParam("device", Build.MODEL);

                    builder.addInterceptor(basicParamsInterceptor);
                    sRetrofit = new Retrofit.Builder()
                            .client(builder.build())
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                            .addConverterFactory(GsonConverterFactory.create())
                            .baseUrl(url)
                            .build();
                }
            }
        }

        return sRetrofit;
    }


}
