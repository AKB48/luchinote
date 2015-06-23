package com.example.uva_app.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetUtil {

	/**
	 * 构造函数，用来初始化
	 */
	public NetUtil() {
	}
		/**
		 * 判断网络是否可用
		 * @param context
		 * @return
		 */
		public static boolean isNetworkAvaliable(Context context){
			ConnectivityManager manager = (ConnectivityManager) (context.getSystemService(Context.CONNECTIVITY_SERVICE));
			NetworkInfo networkinfo = manager.getActiveNetworkInfo();
			return !(networkinfo == null || !networkinfo.isAvailable());
		}
		
		/**
		 * 判断网络类型 wifi  3G
		 * @param context
		 * @return
		 */
		public static  String isWifiNetwrokType(Context context) {
			ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			//获取代表联网状态的NetworkInfo对象信息
			NetworkInfo info = connectivityManager.getActiveNetworkInfo();
			if (info != null && info.isAvailable()) {
				if (info.getType()==ConnectivityManager.TYPE_WIFI) 
				{
					return "wifi";
				} 
				else if(info.getType()==ConnectivityManager.TYPE_MOBILE)
					return "3G/2G";
			}
			return "...";
		}
	}

