package com.example.uva_app.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
/*
 * 非单例模式下的POST 方法
 */
public class JSONParser {

	static InputStream is = null;
	static JSONObject jObj = null;
	static String json = "";

	// constructor
	public JSONParser() {
	}

	// function get json from url
	// by making HTTP POST
	public JSONObject makeHttpRequest(String url, String method, List<NameValuePair> params) {

		// Making HTTP request
		try {
			// request method is POST
			HttpClient httpClient = new DefaultHttpClient(); //创建一个默认的HttpClient
			HttpPost httpRequest = new HttpPost(url); //创建一个POST请求
			HttpEntity httpEntity = new UrlEncodedFormEntity(params, "utf-8");
			httpRequest.setEntity(httpEntity); //HttpRequest实例代表了真实的HTTP请求
			HttpResponse httpResponse = httpClient.execute(httpRequest);//HttpResponse实例代表了真实的HTTP响应
			// 是否连接上服务器
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				is = httpResponse.getEntity().getContent();
			} else {

			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			if (is != null) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"));
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
				is.close();
				json = sb.toString();
				// Android 4.0及以上不需要处理
				if (json != null && json.startsWith("\ufeff")) {
					json = json.substring(1);
				}
			} else {
				StringBuilder builder = new StringBuilder();
				builder.append("{");
				builder.append('"');
				builder.append("message");
				builder.append('"');
				builder.append(':');
				builder.append('"');
				builder.append("服务器未开启或服务器故障！");
				builder.append('"');
				builder.append("}");
				json = builder.toString();
			}
		} catch (Exception e) {
			Log.e("Buffer Error", "Error converting result " + e.toString());
			Log.d("json", json.toString());
		}
		// try parse the string to a JSON object
		try {
			jObj = new JSONObject(json);
		} catch (JSONException e) {
			Log.e("JSON Parser", "Error parsing data " + e.toString());
		}
		// return JSON String
		return jObj;

	}
}