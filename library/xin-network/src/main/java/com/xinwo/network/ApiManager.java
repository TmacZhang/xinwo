package com.xinwo.network;

public class ApiManager {
    private final static int TYPE_TEST = 1;
    private final static int TYPE_PRODUCTION = 2;


    // 普通接口
    private static String mHost;
    private final static String HOST_TEST = "";
    private final static String HOST_PRODUCTION = "";

    //文件上传接口
    private static String mFileUploadHost;
    private final static String FILE_UPLOAD_HOST_TEST = "";
    private final static String FILE_UPLOAD_HOST_PRODUCTION = "";

    //文件访问接口
    private static String mFileAccessHost;
    private final static String FILE_ACCESS_HOST_TEST = "";
    private final static String FILE_ACCESS_HOST_PRODUCTION = "";



    static {
        switch (TYPE_TEST){
            case TYPE_TEST:
                mHost = HOST_TEST;
                mFileUploadHost = FILE_UPLOAD_HOST_TEST;
                mFileAccessHost = FILE_ACCESS_HOST_TEST;
                break;
            case TYPE_PRODUCTION:
                mHost = HOST_PRODUCTION;
                mFileUploadHost = FILE_UPLOAD_HOST_PRODUCTION;
                mFileAccessHost = FILE_ACCESS_HOST_PRODUCTION;
                break;
        }
    }

    //分类数据
    public final static String LOGIN_WX = mHost + "/sns/login/app"; //微信登陆
    public final static String GET_CODE = mHost + "/sns/user/smsCode"; //获取验证码
    public final static String BIND_PHONE = mHost + "/sns/user/smsAccess"; //绑定手机

}
