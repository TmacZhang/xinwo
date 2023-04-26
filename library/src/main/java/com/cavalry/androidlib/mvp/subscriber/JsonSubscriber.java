package com.cavalry.androidlib.mvp.subscriber;

import com.alibaba.fastjson.JSON;
import com.cavalry.androidlib.toolbox.exception.LibException;
import com.cavalry.androidlib.toolbox.utils.LibLog;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;

/**
 * <p>
 *     使用FastJson将json数据解析为bean对象.如果bean对象是继承自统一的BaseBean,如:
 *     <code>
 *         public class BaseBean{
 *             public int code;
 *             public String msg;
 *         }
 *
 *         public MyBean extends BaseBean{
 *             ...
 *         }
 *     </code>
 *     那么你就可以覆写{@link #processBean(Object)},根据不同的code值返回不同的状态:
 *     <li>{@link #PROCESS_SUCCESS}: 将会调用{@link #onProcessSuccess(Object)}和{@link #processCache(String, String, boolean)}</li>
 *     <li>{@link #PROCESS_ERROR}: 将会调用{@link #onProcessError(Object)}</li>
 *     <li>{@link #PROCESS_OTHER}: 将会调用{@link #onProcessOther(Object)}</li>
 * </p>
 * @author Cavalry Lin
 * @since 1.0.0
 */
public abstract class JsonSubscriber extends BaseSubscriber<ResponseBody> implements ISubscriber<Object>{
    private final String TAG = "JsonSubscriber";

    protected final int PROCESS_SUCCESS = 1;
    protected final int PROCESS_ERROR = 2;
    protected final int PROCESS_OTHER = 3;

    protected Type typeOfT;
    protected Class clazz;
    protected String key;
    protected boolean cache;

    /**
     * @param clazz     没有泛型的bean的Class
     */
    public JsonSubscriber(Class clazz){
        this.clazz = clazz;
    }

    /**
     * @param typeOfT   有泛型的bean的Type,通过{@code  new com.alibaba.fastjson.TypeReference<Bean<BeanGeneric>>(){}.getType()}获得
     */
    public JsonSubscriber(Type typeOfT) {
        this.typeOfT = typeOfT;
    }

    /**
     * @param clazz     没有泛型的bean的Class
     * @param cache     是否缓存
     */
    public JsonSubscriber(Class clazz, String key, boolean cache){
        this.clazz = clazz;
        this.cache = cache;
        this.key = key;
    }

    /**
     * @param typeOfT   有泛型的bean的Type,通过{@code  new com.alibaba.fastjson.TypeReference<Bean<BeanGeneric>>(){}.getType()}获得
     * @param cache     是否缓存
     */
    public JsonSubscriber(Type typeOfT, String key, boolean cache) {
        this.typeOfT = typeOfT;
        this.cache = cache;
        this.key = key;
    }


    @Override
    public void onStart() {
        super.onStart();
        onLoadStart();
    }

    @Override
    public void onCompleted() {
        onLoadComplete();
    }

    @Override
    public void onError(Throwable e) {
        onLoadError(e);
    }

    @Override
    public void onNext(ResponseBody responseBody) {
        String json = null;
        try {
            json = responseBody.string();
        } catch (IOException e) {
            e.printStackTrace();
            onError(new LibException(LibException.CODE_RESPONSE_TO_STRING_FAILED));
        }

        Object obj = parseObject(json);

        int processResult = processBean(obj);
        if(processResult==PROCESS_SUCCESS){
            onProcessSuccess(obj);
            processCache(key,json,cache);
        }else if(processResult==PROCESS_ERROR){
            onProcessError(obj);
        }else if(processResult==PROCESS_OTHER){
            onProcessOther(obj);
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

    protected abstract int processBean(Object obj);

    protected abstract void processCache(String key, String json, boolean cache);

    protected abstract void onProcessSuccess(Object obj);

    protected abstract void onProcessError(Object obj);

    protected abstract void onProcessOther(Object obj);
}
