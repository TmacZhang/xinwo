package com.cavalry.androidlib.mvp.presenter;

import android.content.Context;

import com.cavalry.androidlib.mvp.service.GetService;
import com.cavalry.androidlib.mvp.service.PostBodyService;
import com.cavalry.androidlib.mvp.service.PostFormService;

import java.lang.reflect.Type;

/**
 *
 * @author Cavalry Lin
 * @since 1.0.0
 */

public abstract class LibTagPresenter<V> extends BasePresenter<V> {

    public static final int GET = 1;
    public static final int POST = 2;

    public LibTagPresenter(Context context, V view) {
        super(context, view);
    }


    /**
     * GET 方式获取数据, 不使用缓存
     *
     * @param url       除参数外的完整url
     * @param tag       自己定义的一个整形,用于区分一个页面多个请求的情况
     * @param beanClazz bean对象的Class
     */
    public void getData(String url, int tag, Class beanClazz) {
        loadData(url, tag, beanClazz, GET,false);
    }

    /**
     * GET 方式获取数据, 不使用缓存
     *
     * @param url       除参数外的完整url
     * @param tag       自己定义的一个整形,用于区分一个页面多个请求的情况
     * @param typeOfT   有泛型的bean的Type,通过{@code  new com.alibaba.fastjson.TypeReference<Bean<BeanGeneric>>(){}.getType()}获得
     */
    public void getData(String url, int tag, Type typeOfT) {
        loadData(url, tag, typeOfT, GET,false);
    }

    /**
     * GET 方式获取数据, 使用缓存
     *
     * @param url           除参数外的完整url
     * @param tag           自己定义的一个整形,用于区分一个页面多个请求的情况
     * @param beanClazz     bean对象的Class
     */
    public void getDataCache(String url, int tag, Class beanClazz) {
        loadData(url, tag, beanClazz, GET,true);
    }

    /**
     * GET 方式获取数据, 使用缓存
     *
     * @param url       除参数外的完整url
     * @param tag       自己定义的一个整形,用于区分一个页面多个请求的情况
     * @param typeOfT   有泛型的bean的Type,通过{@code  new com.alibaba.fastjson.TypeReference<Bean<BeanGeneric>>(){}.getType()}获得
     */
    public void getDataCache(String url, int tag, Type typeOfT) {
        loadData(url, tag, typeOfT, GET,true);
    }


    /**
     * POST 方式获取数据
     *
     * @param url           除参数外的完整url
     * @param tag           自己定义的一个整形,用于区分一个页面多个请求的情况
     * @param beanClazz     bean对象的Class
     */
    public void postData(String url, int tag, Class beanClazz) {
        loadData(url, tag, beanClazz, POST,false);
    }

    /**
     * POST 方式获取数据
     *
     * @param url       除参数外的完整url
     * @param tag       自己定义的一个整形,用于区分一个页面多个请求的情况
     * @param typeOfT   有泛型的bean的Type,通过{@code  new com.alibaba.fastjson.TypeReference<Bean<BeanGeneric>>(){}.getType()}获得
     */
    public void postData(String url, int tag, Type typeOfT) {
        loadData(url, tag, typeOfT, POST,false);
    }

    /**
     * 获取数据, 仅支持get 和post的form提交，如果使用post的body提交，请查看子类相关方法
     *
     * @param url       除参数外的完整url
     * @param tag       自己定义的一个整形,用于区分一个页面多个请求的情况
     * @param beanClazz bean对象的Class
     * @param method    {@link #GET} 或者 {@link #POST}
     * @param cache     是否缓存
     */
    public abstract void loadData(String url, final int tag, Class beanClazz, int method, boolean cache);


    /**
     * 获取数据，仅支持get 和post的form提交，如果使用post的body提交，请查看子类相关方法
     *
     * @param url       除参数外的完整url
     * @param tag       自己定义的一个整形,用于区分一个页面多个请求的情况
     * @param typeOfT   有泛型的bean的Type,通过{@code  new com.alibaba.fastjson.TypeReference<Bean<BeanGeneric>>(){}.getType()}获得
     * @param method    {@link #GET} 或者 {@link #POST}
     * @param cache     是否缓存
     */
    public abstract void loadData(String url, final int tag, Type typeOfT, int method, boolean cache);

    protected abstract GetService createGetService();

    /**
     * form提交的post请求
     * @return
     */
    protected abstract PostFormService createPostFormService();

    /**
     * body提交的post请求
     * @return
     */
    protected abstract PostBodyService createPostBodyService();
}
