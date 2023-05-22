package com.cavalry.androidlib.mvp.subscriber;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.cavalry.androidlib.toolbox.exception.LibException;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;

/**
 * <p>将ResponseBody转换为具体的实体类型T</p>
 * <p>转换成功会调用会调用{@link #onLoadSuccess(Object)},否则会调用{@link #onError(Throwable)}</p>
 * <p>使用{@link #FastJsonSubscriber(Class)}  或 {@link #FastJsonSubscriber(Type)}创建的对象不会显示loading, 其他造方法创建的对象会显示loading</p>
 * @author Cavalry Lin
 * @since 1.0.0
 */

public abstract class FastJsonSubscriber extends LoadingSubscriber<ResponseBody> implements ISubscriber<Object> {
    private final static String TAG = "FastJsonSubscriber";



    private Type typeOfT;
    private Class clazz;

    /**
     * 不会显示loading
     * @param clazz 没有泛型的bean的Class
     */
    public FastJsonSubscriber(Class clazz){
        this.clazz = clazz;
    }

    /**
     * 不会显示loading
     * @param typeOfT 有泛型的bean的Type,通过{@code  new com.alibaba.fastjson.TypeReference<Bean<BeanGeneric>>(){}.getType()}获得
     */
    public FastJsonSubscriber(Type typeOfT) {
        this.typeOfT = typeOfT;
    }


    /**
     * 显示loading
     * @param clazz 没有泛型的bean的Class
     * @param context
     */
    public FastJsonSubscriber(Class clazz, Context context) {
        super(context);
    }

    /**
     * 显示loading
     * @param clazz 没有泛型的bean的Class
     * @param context
     * @param dialogCancelable 点击是否取消loading窗
     */

    public FastJsonSubscriber(Class clazz, Context context, boolean dialogCancelable) {
        super(context, dialogCancelable);
    }

    /**
     * 显示loading,点击不可取消loading窗
     * @param clazz 没有泛型的bean的Class
     * @param context
     * @param msg loading的显示信息
     */
    public FastJsonSubscriber(Class clazz, Context context, String msg) {
        super(context, msg);
    }

    /**
     * 显示loading
     * @param clazz 没有泛型的bean的Class
     * @param context
     * @param dialogCancelable 点击是否取消loading窗
     * @param msg loading的显示信息
     */
    public FastJsonSubscriber(Class clazz, Context context, boolean dialogCancelable, String msg) {
        super(context, dialogCancelable, msg);
        this.clazz = clazz;
    }

    /**
     * 显示loading
     * @param typeOfT 有泛型的bean的Type,通过{@code new com.alibaba.fastjson.TypeReference<Bean<BeanGeneric>>(){}.getType()}获得
     * @param context
     */
    public FastJsonSubscriber(Type typeOfT, Context context) {
        super(context);
    }

    /**
     * 显示loading
     * @param typeOfT 有泛型的bean的Type,通过{@code new com.alibaba.fastjson.TypeReference<Bean<BeanGeneric>>(){}.getType()}获得
     * @param context
     * @param dialogCancelable 点击是否取消loading窗
     */
    public FastJsonSubscriber(Type typeOfT, Context context, boolean dialogCancelable) {
        super(context, dialogCancelable);
    }

    /**
     * 显示loading,点击不可取消loading窗
     * @param typeOfT 有泛型的bean的Type,通过{@code new com.alibaba.fastjson.TypeReference<Bean<BeanGeneric>>(){}.getType()}获得
     * @param context
     * @param msg loading的显示信息
     */
    public FastJsonSubscriber(Type typeOfT, Context context, String msg) {
        super(context, msg);
    }

    /**
     * 显示loading
     * @param typeOfT 有泛型的bean的Type,通过{@code new com.alibaba.fastjson.TypeReference<Bean<BeanGeneric>>(){}.getType()}获得
     * @param context
     * @param dialogCancelable 点击是否取消loading窗
     * @param msg loading的显示信息
     */
    public FastJsonSubscriber(Type typeOfT, Context context, boolean dialogCancelable, String msg) {
        super(context, dialogCancelable, msg);
        this.typeOfT = typeOfT;
    }

    @Override
    public void onStart() {
        super.onStart();
        onLoadStart();
    }

    @Override
    public void onCompleted() {
        super.onCompleted();
        onLoadComplete();
    }

    @Override
    public void onError(Throwable e) {
        super.onError(e);
        e.printStackTrace();
        onLoadError(e);
    }

    @Override
    public void onNext(ResponseBody responseBody) {
        try {
            String json = responseBody.string();
            Object obj = parseObject(json);
            onLoadSuccess(obj);
        } catch (IOException e) {
            e.printStackTrace();
            onError(new LibException(LibException.CODE_RESPONSE_TO_STRING_FAILED));
        }

    }

    protected Object parseObject(String json){
        Object obj = null;
        try {

            if(clazz!=null){
                obj = JSON.parseObject(json,clazz);
            }else if(typeOfT!=null){
                obj = JSON.parseObject(json,typeOfT);
            }
        } catch (Exception e) {
            onError(new LibException(LibException.CODE_JSON_PARSE_ERROR));
        }

        return obj;
    }



    @Override
    public void onLoadStart() {
    }

    @Override
    public void onLoadComplete() {
    }
}
