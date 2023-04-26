/*
 * Copyright (c) 2015 [1076559197@qq.com | tchen0707@gmail.com]
 *
 * Licensed under the Apache License, Version 2.0 (the "License”);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cavalry.androidlib.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.cavalry.androidlib.LibApplication;

import java.util.Locale;

/**
 * @author Cavalry Lin
 * @since 1.0.0
 */
public class NetworkUtils {

	public enum NetworkType{
		WIFI,MOBILE,NONE
	}

	public static boolean isNetworkConnected(){
		NetworkInfo info = getNetworkInfo();

		return info != null && info.isConnected();
	}

	public static boolean isWifiConnected(){
		NetworkInfo info = getNetworkInfo();
		return (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_WIFI);
	}

	public static boolean isMobileConnected(){
		NetworkInfo info = getNetworkInfo();
		return (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_MOBILE);
	}

	private static NetworkInfo getNetworkInfo() {
		ConnectivityManager connectivityManager
				= (ConnectivityManager) LibApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		return connectivityManager.getActiveNetworkInfo();
	}

	public static NetworkType getNetworkType(){
		if(!isNetworkConnected())
			return NetworkType.NONE;

		if(isWifiConnected())
			return NetworkType.WIFI;

		if(isMobileConnected())
			return NetworkType.MOBILE;

		return NetworkType.NONE;
	}
}
