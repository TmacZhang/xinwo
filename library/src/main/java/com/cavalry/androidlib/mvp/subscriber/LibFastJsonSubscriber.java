package com.cavalry.androidlib.mvp.subscriber;

import android.content.Context;

import com.cavalry.androidlib.toolbox.exception.LibException;

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
 *     那么你就可以覆写{@link #processBean(Object)},根据不同的code值返回不同的状态.
 *     然后分别覆写{@link #onProcessSuccess(Object)},{@link #onProcessError(Object)},{@link #onProcessOther(Object)}来处理不同的状态
 * </p>
 * @author Cavalry Lin
 * @since 1.0.0
 */

public abstract class LibFastJsonSubscriber extends FastJsonSubscriber{

    protected final int PROCESS_SUCCESS = 1;
    protected final int PROCESS_ERROR = 2;
    protected final int PROCESS_OTHER = 3;

    public LibFastJsonSubscriber(Class clazz) {
        super(clazz);
    }

    public LibFastJsonSubscriber(Type typeOfT) {
        super(typeOfT);
    }

    public LibFastJsonSubscriber(Class clazz, Context context) {
        super(clazz, context);
    }

    public LibFastJsonSubscriber(Class clazz, Context context, boolean dialogCancelable) {
        super(clazz, context, dialogCancelable);
    }

    public LibFastJsonSubscriber(Class clazz, Context context, String msg) {
        super(clazz, context, msg);
    }

    public LibFastJsonSubscriber(Class clazz, Context context, boolean dialogCancelable, String msg) {
        super(clazz, context, dialogCancelable, msg);
    }

    public LibFastJsonSubscriber(Type typeOfT, Context context) {
        super(typeOfT, context);
    }

    public LibFastJsonSubscriber(Type typeOfT, Context context, boolean dialogCancelable) {
        super(typeOfT, context, dialogCancelable);
    }

    public LibFastJsonSubscriber(Type typeOfT, Context context, String msg) {
        super(typeOfT, context, msg);
    }

    public LibFastJsonSubscriber(Type typeOfT, Context context, boolean dialogCancelable, String msg) {
        super(typeOfT, context, dialogCancelable, msg);
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
        }else if(processResult==PROCESS_ERROR){
            onProcessError(obj);
        }else if(processResult==PROCESS_OTHER){
            onProcessOther(obj);
        }
    }

    protected abstract int processBean(Object obj);

    protected abstract void onProcessSuccess(Object obj);

    protected abstract void onProcessError(Object obj);

    protected abstract void onProcessOther(Object obj);
}
