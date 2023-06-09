package com.xinwo.base;

import android.content.Context;
import android.content.res.Resources;
import android.os.Environment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import com.cavalry.androidlib.LibApplication;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.SDKOptions;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.util.NIMUtil;
import com.xinwo.log.LibSPUtils;
import com.xinwo.application.XinApplicationUtil;

import java.io.IOException;

public class BaseApplication extends LibApplication {
    private static BaseApplication mInstance;
    public static Context mContext;
    /**
     * 屏幕宽度
     */
    public static int screenWidth;
    /**
     * 屏幕高度
     */
    public static int screenHeight;
    /**
     * 屏幕密度
     */
    public static float screenDensity;

    @Override
    public void onCreate() {
        XinApplicationUtil.Companion.getInstance().setMApplication(this);
        super.onCreate();
        mContext = getApplicationContext();
        mInstance = this;
        initScreenSize();

        // ... your codes

        // SDK初始化（启动后台服务，若已经存在用户登录信息， SDK 将完成自动登录）
        NIMClient.init(this, loginInfo(), options());

        // ... your codes
        if (NIMUtil.isMainProcess(this)) {
            // 注意：以下操作必须在主进程中进行
            // 1、UI相关初始化操作
            // 2、相关Service调用
        }
    }

    public static BaseApplication getInstance() {
        return mInstance;
    }

    /**
     * 初始化当前设备屏幕宽高
     */
    private void initScreenSize() {
        DisplayMetrics curMetrics = getApplicationContext().getResources().getDisplayMetrics();
        screenWidth = curMetrics.widthPixels;
        screenHeight = curMetrics.heightPixels;
        screenDensity = curMetrics.density;
    }

    public static int px2dip(float px) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }


    public static int dip2px(float dip) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip,
                Resources.getSystem().getDisplayMetrics());
    }


    public static int px2sp(float px) {
        final float fontScale = Resources.getSystem().getDisplayMetrics().scaledDensity;
        return (int) (px / fontScale + 0.5f);
    }

    public static int sp2px(float sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
                Resources.getSystem().getDisplayMetrics());
    }

    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }


    private WorkerThread mWorKerThread;
    public synchronized void initWorkerThread(){
        if (mWorKerThread == null){
            mWorKerThread = new WorkerThread(getApplicationContext());
            mWorKerThread.start();
            mWorKerThread.waitForReady();
        }
    }
    public synchronized WorkerThread getWorkerThread(){
        return mWorKerThread;
    }
    public synchronized void deInitWorkerThread(){
        mWorKerThread.exit();
        try{
            mWorKerThread.join();
        }catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        mWorKerThread = null;
    }


    // 如果返回值为 null，则全部使用默认参数。
    private SDKOptions options() {
        return null;

//        SDKOptions options = new SDKOptions();
//
//        // 如果将新消息通知提醒托管给 SDK 完成，需要添加以下配置。否则无需设置。
//        StatusBarNotificationConfig config = new StatusBarNotificationConfig();
//        config.notificationEntrance = WelcomeActivity.class; // 点击通知栏跳转到该Activity
//        config.notificationSmallIconId = R.drawable.ic_stat_notify_msg;
//        // 呼吸灯配置
//        config.ledARGB = Color.GREEN;
//        config.ledOnMs = 1000;
//        config.ledOffMs = 1500;
//        // 通知铃声的uri字符串
//        config.notificationSound = "android.resource://com.netease.nim.demo/raw/msg";
//        options.statusBarNotificationConfig = config;
//
//        // 配置保存图片，文件，log 等数据的目录
//        // 如果 options 中没有设置这个值，SDK 会使用采用默认路径作为 SDK 的数据目录。
//        // 该目录目前包含 log, file, image, audio, video, thumb 这6个目录。
//        String sdkPath = getAppCacheDir(context) + "/nim"; // 可以不设置，那么将采用默认路径
//        // 如果第三方 APP 需要缓存清理功能， 清理这个目录下面个子目录的内容即可。
//        options.sdkStorageRootPath = sdkPath;
//
//        // 配置是否需要预下载附件缩略图，默认为 true
//        options.preloadAttach = true;
//
//        // 配置附件缩略图的尺寸大小。表示向服务器请求缩略图文件的大小
//        // 该值一般应根据屏幕尺寸来确定， 默认值为 Screen.width / 2
//        options.thumbnailSize = ${Screen.width} / 2;
//
//        // 用户资料提供者, 目前主要用于提供用户资料，用于新消息通知栏中显示消息来源的头像和昵称
//        options.userInfoProvider = new UserInfoProvider() {
//            @Override
//            public UserInfo getUserInfo(String account) {
//                return null;
//            }
//
//            @Override
//            public int getDefaultIconResId() {
//                return R.drawable.avatar_def;
//            }
//
//            @Override
//            public Bitmap getTeamIcon(String tid) {
//                return null;
//            }
//
//            @Override
//            public Bitmap getAvatarForMessageNotifier(String account) {
//                return null;
//            }
//
//            @Override
//            public String getDisplayNameForMessageNotifier(String account, String sessionId,
//                                                           SessionTypeEnum sessionType) {
//                return null;
//            }
//        };
//        return options;
    }
    public static final String NIM_LOGIN_INFO_ACCOUNT = "nim_login_info_acount";
    public static final String NIM_LOGIN_INFO_TOKEN = "nim_login_info_token";
    public static final String NIM_LOGIN_INFO_APPKEY = "nim_login_info_appkey";
    // 如果已经存在用户登录信息，返回LoginInfo，否则返回null即可
    private LoginInfo loginInfo() {
        String account = LibSPUtils.getString(NIM_LOGIN_INFO_ACCOUNT, "");
        if("" == account || account != null){
            return null;
        }

        String token = LibSPUtils.getString(NIM_LOGIN_INFO_TOKEN, "");
        if("" == token || token != null){
            return null;
        }

        String appkey = LibSPUtils.getString(NIM_LOGIN_INFO_APPKEY, "");
        if("" == appkey || appkey != null){
            return null;
        }

        LoginInfo loginInfo = new LoginInfo(account, token, appkey);

        return loginInfo;
    }


}
