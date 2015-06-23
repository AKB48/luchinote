package com.example.uva_app.activity;

import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Toast;

import com.example.uva_app.R;
import com.example.uva_app.model.Model;
import com.example.uva_app.utils.DialogUtil;
import com.example.uva_app.utils.HttpRequestType;
import com.example.uva_app.utils.HttpSender;
import com.example.uva_app.utils.HttpStatusCode;
import com.example.uva_app.utils.HttpUrl;
import com.example.uva_app.utils.IResponse;
import com.example.uva_app.utils.JSONParser;
import com.example.uva_app.utils.StringUtil;
import com.freddyyao.uva_app.MainActivity;

public class ReigsterActivity extends Activity {
	private Button btn_register;
	private Button btn_set;
	private EditText userName;
	private EditText password;
	private EditText passwordAgain;
	private CheckBox chk_showpassword;

	private String name;
	private String pwd;
	private String pwdAgain;

	private ProgressDialog pDialog;
	private JSONParser jsonParser = new JSONParser(); // ʵ����JSON����
	private static final String TAG_MESSAGE = "message";

	/**
	 * william add
	 */
	private final String SHARE_LOGIN_TAG = "MAP_SHARE_LOGIN_TAG";
	private String SHARE_LOGIN_USERNAME = "MAP_LOGIN_USERNAME";
	private String SHARE_LOGIN_PASSWORD = "MAP_LOGIN_PASSWORD";
	private String sHARE_LOGIN_AUTOLOGIN = "MAP_LOGIN_AUTOLOGIN";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		setTitle("ע��");
		getActionBar().setDisplayHomeAsUpEnabled(true);// ����Actionbar
		setOverflowShowingAlways();
		btn_register = (Button) findViewById(R.id.btn_register);
		chk_showpassword = (CheckBox) findViewById(R.id.chk_reg_showpassword);
		userName = (EditText) findViewById(R.id.reg_userName);
		passwordAgain = (EditText) findViewById(R.id.reg_passwordAgain);
		password = (EditText) findViewById(R.id.reg_password);
		btn_set = (Button) findViewById(R.id.btn_set);
		//����
		btn_set.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				passwordAgain.setText("");
				password.setText("");
			}
		});
		btn_register.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				if (validate()) {
					/*
					Intent intent = new Intent(ReigsterActivity.this, LoginActivity.class);
					intent.putExtra("userName", name);
					intent.putExtra("password", pwd);
					intent.putExtras(intent);
					startActivity(intent);
					finish();
					Toast.makeText(ReigsterActivity.this, "ע��ɹ���", Toast.LENGTH_LONG).show();
					*/
					name = userName.getText().toString();
					pwd = password.getText().toString();

					List<NameValuePair> params = new ArrayList<NameValuePair>();
					params.add(new BasicNameValuePair("username", name));
					params.add(new BasicNameValuePair("password", pwd));

					try {
						HttpSender.post(HttpUrl.REGIST, params, new RegistResponse(), HttpRequestType.POST_DATA);
					} catch (MalformedURLException e) {
						e.printStackTrace();
					}
				}
			}
		});
		chk_showpassword.setOnCheckedChangeListener(chkListener);
	}

	private void setOverflowShowingAlways() {
		try {
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
			menuKeyField.setAccessible(true);
			menuKeyField.setBoolean(config, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Home ���ؼ�
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home: {
			Intent intent = new Intent(ReigsterActivity.this, LoginActivity.class);
			startActivity(intent);
			finish();// ֱ�ӹر�
		}
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * �����Ƿ�ɼ�
	 */
	private OnCheckedChangeListener chkListener = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton button, boolean isChecked) {
			if (isChecked) {

				/* �趨EditText������Ϊ�ɼ��� */
				password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
				passwordAgain.setTransformationMethod(HideReturnsTransformationMethod.getInstance());

			} else {
				/* �趨EditText������Ϊ���ص� */
				password.setTransformationMethod(PasswordTransformationMethod.getInstance());
				passwordAgain.setTransformationMethod(PasswordTransformationMethod.getInstance());
			}
		}
	};

	/**
	 * �ж������Ƿ���Ч
	 */
	private boolean validate() {
		name = userName.getText().toString().trim();
		pwd = password.getText().toString().trim();
		pwdAgain = passwordAgain.getText().toString().trim();

		if (name.equals("") || pwd.equals("") || pwdAgain.equals("")) {
			DialogUtil.showDialog(this, "�û���/����/���ܿգ�", false);
			return false;
		} else if (name.length() != 11 || !StringUtil.isMobile(name)) {
			DialogUtil.showDialog(this, "�ʺŸ�ʽ����ȷ��", false);
			return false;
		} else if (!pwd.equals(pwdAgain)) {
			DialogUtil.showDialog(this, "�����������벻һ�£�", false);
			return false;
		} else if (pwd.length() < 5 || pwd.length() > 16) {
			DialogUtil.showDialog(this, "���볤�ȱ���Ϊ5-16��", false);
			return false;
		}
		return true;

	}

	/**
	 * @author WilliamDeng
	 */
	private void saveSharePreferences() {
		SharedPreferences share = getSharedPreferences(SHARE_LOGIN_TAG, 0);
		share.edit().putString(SHARE_LOGIN_USERNAME, name).commit();
		share.edit().putString(SHARE_LOGIN_PASSWORD, pwd).commit();
		share.edit().putBoolean(sHARE_LOGIN_AUTOLOGIN, true).commit();
		share = null;
	}

	class RegistResponse implements IResponse {
		@Override
		public void onResponse(Object respContent) {
			try {
				JSONObject resp = new JSONObject((String) respContent);
				int status = resp.getInt("status");
				String respInfo = resp.getString("info");
				if (status == HttpStatusCode.HTTP_OK) {
					saveSharePreferences();
					Intent intent = new Intent(ReigsterActivity.this, MainActivity.class);
					intent.putExtra("userName", name);
					intent.putExtra("nickName", name);
					intent.putExtras(intent);
					startActivity(intent);
					finish();
					Toast.makeText(ReigsterActivity.this, respInfo, Toast.LENGTH_LONG).show();
				} else if (status == HttpStatusCode.HTTP_ERROR) {
					Toast.makeText(ReigsterActivity.this, respInfo, Toast.LENGTH_LONG).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

}
