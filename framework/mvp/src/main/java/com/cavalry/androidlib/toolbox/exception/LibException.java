package com.cavalry.androidlib.toolbox.exception;

import android.util.Log;

/**
 * @author Cavalry Lin
 * @since 1.0.0
 */
public class LibException extends RuntimeException {
    private final String TAG = "LibException";
    private int code;
    private String msg;

    /**<--------------------- 自定义code使用负值 --------------------->**/
    public static final int CODE_SELF_DEFINE = -1;//自定义输入内容


    public static final int CODE_NO_NET = -2;//没有网络连接
    public static final String MSG_NO_NET = "没有网络连接";

    public static final int CODE_RESPONSE_TO_STRING_FAILED = -3;
    public static final String MSG_RESPONSE_TO_STRING_FAILED = "读取数据失败";

    public static final int CODE_JSON_PARSE_ERROR = -4;
    public static final String MSG_JSON_PARSE_ERROR = "数据解析异常";

    public LibException(int code){
        this(code,getMsg(code));
    }

    public LibException(int code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    @Override
    public String getMessage() {
        return "code:"+code+"   msg:"+msg;
    }

    @Override
    public String toString() {
        return getMessage();
    }

    @Override
    public void printStackTrace() {
        Log.e(TAG,"code:"+code);
        Log.e(TAG,"msg:"+msg);
    }

    /**
     * 根据code获取相对应的msg
     * @param code
     * @return
     */
    private static String getMsg(int code){
        String msg = null;
        switch (code){
            case CODE_NO_NET:
                msg = MSG_NO_NET;
                break;
            case CODE_RESPONSE_TO_STRING_FAILED:
                msg = MSG_RESPONSE_TO_STRING_FAILED;
                break;
            case CODE_JSON_PARSE_ERROR:
                msg = MSG_JSON_PARSE_ERROR;
                break;
        }
        return msg;
    }
}
