package com.xinwo.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtils {

    public enum NetworkType {
        WIFI, MOBILE, NONE
    }

    public static boolean isNetworkConnected(Context context) {
        NetworkInfo info = getNetworkInfo(context);

        return info != null && info.isConnected();
    }

    public static boolean isWifiConnected(Context context) {
        NetworkInfo info = getNetworkInfo(context);
        return (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_WIFI);
    }

    public static boolean isMobileConnected(Context context) {
        NetworkInfo info = getNetworkInfo(context);
        return (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_MOBILE);
    }

    private static NetworkInfo getNetworkInfo(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo();
    }

    public static NetworkType getNetworkType(Context context) {
        if (!isNetworkConnected(context))
            return NetworkType.NONE;

        if (isWifiConnected(context))
            return NetworkType.WIFI;

        if (isMobileConnected(context))
            return NetworkType.MOBILE;

        return NetworkType.NONE;
    }
}
