package com.cavalry.androidlib.mvp.subscriber;

import android.content.Context;

import com.cavalry.androidlib.toolbox.exception.LibException;
import com.cavalry.androidlib.toolbox.managers.GsonManager;
import com.google.gson.Gson;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;

/**
 * <p>将ResponseBody转换为具体的实体类型T</p>
 * <p>转换成功会调用会调用{@link #onLoadSuccess(Object)},否则会调用{@link #onError(Throwable)}</p>
 * <p>使用{@link #GsonSubscriber(Class)} 或 {@link #GsonSubscriber(Type)}创建的对象不会显示loading, 其他造方法创建的对象会显示loading</p>
 * @author Cavalry Lin
 * @since 1.0.0
 */

public abstract class GsonSubscriber extends LoadingSubscriber<ResponseBody> implements ISubscriber<Object> {
    private final static String TAG = "GsonSubscriber";



    private Type typeOfT;
    private Class clazz;

    /**
     * 不会显示loading
     * @param clazz 没有泛型的bean的Class
     */
    public GsonSubscriber(Class clazz){
        this.clazz = clazz;
    }

    /**
     * 不会显示loading
     * @param typeOfT 有泛型的bean的Type,通过{@code new TypeToken<Bean<BeanGeneric>>(){}.getType()}获得
     */
    public GsonSubscriber(Type typeOfT) {
        this.typeOfT = typeOfT;
    }


    /**
     * 显示loading
     * @param clazz 没有泛型的bean的Class
     * @param context
     */
    public GsonSubscriber(Class clazz, Context context) {
        super(context);
    }

    /**
     * 显示loading
     * @param clazz 没有泛型的bean的Class
     * @param context
     * @param dialogCancelable 点击是否取消loading窗
     */

    public GsonSubscriber(Class clazz, Context context, boolean dialogCancelable) {
        super(context, dialogCancelable);
    }

    /**
     * 显示loading,点击不可取消loading窗
     * @param clazz 没有泛型的bean的Class
     * @param context
     * @param msg loading的显示信息
     */
    public GsonSubscriber(Class clazz, Context context, String msg) {
        super(context, msg);
    }

    /**
     * 显示loading
     * @param clazz 没有泛型的bean的Class
     * @param context
     * @param dialogCancelable 点击是否取消loading窗
     * @param msg loading的显示信息
     */
    public GsonSubscriber(Class clazz, Context context, boolean dialogCancelable, String msg) {
        super(context, dialogCancelable, msg);
        this.clazz = clazz;
    }

    /**
     * 显示loading
     * @param typeOfT 有泛型的bean的Type,通过{@code new TypeToken<Bean<BeanGeneric>>(){}.getType()}获得
     * @param context
     */
    public GsonSubscriber(Type typeOfT, Context context) {
        super(context);
    }

    /**
     * 显示loading
     * @param typeOfT 有泛型的bean的Type,通过{@code new TypeToken<Bean<BeanGeneric>>(){}.getType()}获得
     * @param context
     * @param dialogCancelable 点击是否取消loading窗
     */
    public GsonSubscriber(Type typeOfT, Context context, boolean dialogCancelable) {
        super(context, dialogCancelable);
    }

    /**
     * 显示loading,点击不可取消loading窗
     * @param typeOfT 有泛型的bean的Type,通过{@code new TypeToken<Bean<BeanGeneric>>(){}.getType()}获得
     * @param context
     * @param msg loading的显示信息
     */
    public GsonSubscriber(Type typeOfT, Context context, String msg) {
        super(context, msg);
    }

    /**
     * 显示loading
     * @param typeOfT 有泛型的bean的Type,通过{@code new TypeToken<Bean<BeanGeneric>>(){}.getType()}获得
     * @param context
     * @param dialogCancelable 点击是否取消loading窗
     * @param msg loading的显示信息
     */
    public GsonSubscriber(Type typeOfT, Context context, boolean dialogCancelable, String msg) {
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
        Object obj = fromResponseBody(responseBody);
        onLoadSuccess(obj);
    }

    protected Object fromResponseBody(ResponseBody responseBody){
        Gson gson = GsonManager.getGson();
        Object obj = null;
        try {
            //gson解析
            String json = responseBody.string();

            if(clazz!=null){
                obj = gson.fromJson(json,clazz);
            }else if(typeOfT!=null){
                obj = gson.fromJson(json, typeOfT);
            }

        } catch (IOException e) {
            onError(new LibException(LibException.CODE_RESPONSE_TO_STRING_FAILED));
        } catch (Exception e) {
            onError(new LibException(LibException.CODE_JSON_PARSE_ERROR));
        }finally {
            GsonManager.recycleGson(gson);
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
