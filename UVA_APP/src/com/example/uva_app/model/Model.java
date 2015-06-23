package com.example.uva_app.model;

import com.example.uva_app.R;

/**
 * @author yzf
 * Model : 全局常量类
 */
public class Model {
	
	public static String[] function = {"我的足迹", "我的收藏", "我的分享", "设置" };
	public static int[] icon = { R.drawable.icon2, R.drawable.icon3,R.drawable.icon4, R.drawable.icon5 };
	
	// 网络交互地址前段
	public static String HTTPURL = "http://202.38.214.160:8080/sql/";
	// 登入请求
	public static String LOGINNURL = "login.php";
	//注册请求
	public static String REIGSTERNURL = "reigster.php";

}
