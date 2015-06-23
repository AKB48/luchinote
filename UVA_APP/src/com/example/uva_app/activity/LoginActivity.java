package com.example.uva_app.activity;

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
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.uva_app.R;
import com.example.uva_app.utils.DialogUtil;
import com.example.uva_app.utils.HttpRequestType;
import com.example.uva_app.utils.HttpSender;
import com.example.uva_app.utils.HttpStatusCode;
import com.example.uva_app.utils.HttpUrl;
import com.example.uva_app.utils.IResponse;
import com.example.uva_app.utils.JSONParser;
import com.example.uva_app.utils.NetUtil;
import com.example.uva_app.utils.StringUtil;
import com.freddyyao.uva_app.MainActivity;
/**
 * @author freddyyao(Ҧ־��)
 * �������
 */
public class LoginActivity extends Activity {

	private ProgressDialog pDialog;
	private JSONParser jsonParser = new JSONParser(); // ʵ����JSON����
	private Button btn_login; // ����
	private TextView txt_isNet;
	private EditText username;
	private EditText password;
	private CheckBox chk_rememberMe;
	private CheckBox chk_showpassword;

	// url to create new product
	private static String url_login = "http://202.38.214.160:8080/sql/login.php";
	private static final String TAG_MESSAGE = "message";
	/** ��������SharePreferences�ı�ʶ */
	private final String SHARE_LOGIN_TAG = "MAP_SHARE_LOGIN_TAG";
	/** �����¼�ɹ���,���ڱ����û�����SharedPreferences,�Ա��´β������� */
	private String SHARE_LOGIN_USERNAME = "MAP_LOGIN_USERNAME";
	/** �����¼�ɹ���,���ڱ���PASSWORD��SharedPreferences,�Ա��´β������� */
	private String SHARE_LOGIN_PASSWORD = "MAP_LOGIN_PASSWORD";

	/*
	 * william
	 */
	private String sHARE_LOGIN_AUTOLOGIN = "MAP_LOGIN_AUTOLOGIN";
	private boolean loginState = false;// ����״̬
	
	/*
	 * william
	 */
	private String name;
	private String pwd;
	private Boolean isAutoLogin;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		
		setTitle("����");
		initData();		
		if (isAutoLogin && !"".equals(pwd))
		{
		    login();
		    return;
		}
		setContentView(R.layout.login);
		
        
		Intent intent=getIntent();
		name=intent.getStringExtra("userName");
		pwd=intent.getStringExtra("password");
		
		txt_isNet = (TextView) findViewById(R.id.txt_isNet);
		username = (EditText) findViewById(R.id.login_username);
		password = (EditText) findViewById(R.id.login_password);
		username.setText(name);
		password.setText(pwd);
		
		btn_login = (Button) findViewById(R.id.btn_login);
		chk_rememberMe = (CheckBox) findViewById(R.id.loginRememberMeCheckBox);
		chk_showpassword = (CheckBox) findViewById(R.id.chk_showpassword);
		Click();
		initView(false);

	}

	public void Click() {
		// �ж��Ƿ���������
		boolean isNet = NetUtil.isNetworkAvaliable(LoginActivity.this);
		if (!isNet) {
			txt_isNet.setText("���粻���ã���WIFI �� �������ӣ�����");
			Toast.makeText(this, "���粻���ã���WIFI �� �������ӣ�����", Toast.LENGTH_LONG).show();
			txt_isNet.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent("android.settings.WIRELESS_SETTINGS");
					startActivity(intent);
				}
			});
		}
		//���밴ť
		btn_login.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (validate()) {
                    login();
				}
			}
		});
		// �Ƿ��ס�ʺź�����
		chk_rememberMe.setOnCheckedChangeListener(remeberListener);
		chk_showpassword.setOnCheckedChangeListener(chkListener);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.login, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {

		case R.id.action_register: {
			Intent intent = new Intent(LoginActivity.this, ReigsterActivity.class);
			startActivity(intent);
			finish();
		}
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private OnCheckedChangeListener chkListener = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton button, boolean isChecked) {
			if (isChecked) {
				// Toast.makeText(LoginActivity.this, "�����¼�ɹ�,�Ժ��˺ź�������Զ�����!",
				// Toast.LENGTH_SHORT).show();
				/* �趨EditText������Ϊ�ɼ��� */
				password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
			} else {
				/* �趨EditText������Ϊ���ص� */
				password.setTransformationMethod(PasswordTransformationMethod.getInstance());
			}
		}
	};
	private OnCheckedChangeListener remeberListener = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton button, boolean isChecked) {
			if (isChecked) {
				Toast.makeText(LoginActivity.this, "�����¼�ɹ�,�Ժ��˺ź�������Զ�����!", Toast.LENGTH_SHORT).show();
			}
		}
	};

	/*
	 * william
	 */
	private void initData()
	{
	    // int mode=0 ��ʾĬ�������ֻ�Ա�Ӧ����Ч
        SharedPreferences share = getSharedPreferences(SHARE_LOGIN_TAG, 0);
        // getString �ĵڶ������� Ĭ��ֵΪ��
        name = share.getString(SHARE_LOGIN_USERNAME, "");
        pwd = share.getString(SHARE_LOGIN_PASSWORD, "");
        isAutoLogin = share.getBoolean(sHARE_LOGIN_AUTOLOGIN, false);
        share = null;
	}

	/**
	 * ��ʼ������
	 * @param isRememberMe
	 * �����ʱ�����RememberMe,���ҵ�½�ɹ���һ��,��saveSharePreferences(true,ture)��,��ֱ�ӽ���
	 * */
	private void initView(boolean isRememberMe) {
		  /*
         * william modify
         */
		Log.d(this.toString(), "userName=" + name + " password=" + pwd);
		if (!"".equals(name)) {
			username.setText(name);
		}
		if (!"".equals(pwd)) {
			password.setText(pwd);
			chk_rememberMe.setChecked(true);
		}
		// �������Ҳ������,��ֱ���õ�½��ť��ȡ����
		if (password.getText().toString().length() > 0) {
			btn_login.requestFocus();
			password.requestFocus();
		}
	}

	/**
	 * �����¼�ɹ���,�򽫵�½�û����������¼��SharePreferences
	 * 
	 * @param saveUserName
	 *            �Ƿ��û������浽SharePreferences
	 * @param savePassword�Ƿ����뱣�浽SharePreferences
	 * */
	private void saveSharePreferences(boolean saveUserName, boolean savePassword) {
		SharedPreferences share = getSharedPreferences(SHARE_LOGIN_TAG, 0);
		if (saveUserName) {
			Log.d(this.toString(), "saveUserName=" + username.getText().toString());
			share.edit().putString(SHARE_LOGIN_USERNAME, username.getText().toString()).commit();
		}
		if (savePassword) {
			share.edit().putString(SHARE_LOGIN_PASSWORD, password.getText().toString()).commit();
		}
		/**
		 * WILLIAM ADD
		 */
		share.edit().putBoolean(sHARE_LOGIN_AUTOLOGIN, true).commit();
		share = null;
	}

	/** ������� */
	private void clearSharePassword() {
		SharedPreferences share = getSharedPreferences(SHARE_LOGIN_TAG, 0);
		share.edit().putString(SHARE_LOGIN_PASSWORD, "").commit();
		share = null;
	}

	/** ��ס�ҵ�ѡ���Ƿ�ѡ */
	private boolean isRememberMe() {
		if (chk_rememberMe.isChecked()) {
			return true;
		}
		return false;
	}

	/**
	 * �ж������Ƿ���Ч
	 */
	private boolean validate() {
	    name = username.getText().toString().trim();
		pwd = password.getText().toString().trim();
		if (name.equals("") || pwd.equals("")) {
			DialogUtil.showDialog(this, "�û���/����/���ܿգ�", false);
			return false;
		}
		/*
		 * william add
		 */
		else if (name.length() != 11 || !StringUtil.isMobile(name))
        {
            DialogUtil.showDialog(this, "�ʺŸ�ʽ����ȷ��", false);
            return false;
        }
        else if (pwd.length() < 5 || pwd.length() > 16)
        {
            DialogUtil.showDialog(this, "���볤�ȱ���Ϊ5-16��", false);
            return false;
        }
        return true;
	}


	/*
	 * william
	 */
	private void login()
	{
	    List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("username", name));
        params.add(new BasicNameValuePair("password", pwd));
        
        try {
            HttpSender.post(HttpUrl.LOGIN, params, new LoginResponse(), HttpRequestType.POST_DATA);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
	}
	
	
	/*
	 * william
	 */
	class LoginResponse implements IResponse
	{
        @Override
        public void onResponse(Object respContent) {
            try {
                JSONObject resp= new JSONObject((String) respContent);
                int status = resp.getInt("status");
                String respInfo = resp.getString("info");
                
                if (status == HttpStatusCode.HTTP_OK)
                {
                    String nickname = resp.getString("nickname");
                    // remember username and password.
                    if (!isAutoLogin && isRememberMe()) {
                        saveSharePreferences(true, true);
                    } else {
                        saveSharePreferences(true, false);
                    }
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("userName",name);
                    intent.putExtra("nickName", nickname);
                    intent.putExtras(intent);
                    startActivity(intent);
                    finish();
                    Toast.makeText(LoginActivity.this, respInfo, Toast.LENGTH_LONG).show();
                }
                else if (status == HttpStatusCode.HTTP_ERROR)
                {
                    Toast.makeText(LoginActivity.this, respInfo, Toast.LENGTH_LONG).show();
                }
                if (!chk_rememberMe.isChecked()) {
    				clearSharePassword();
    			}
                
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
	}
	
	/**
	 * �����η��ؼ��˳�
	 */
	private long mExitTime;
    @Override  
    public boolean onKeyDown(int keyCode, KeyEvent event) {  
        
        if (keyCode == KeyEvent.KEYCODE_BACK) {
			if ((System.currentTimeMillis() - mExitTime) > 2000) {
				Toast.makeText(this, "�ٰ�һ���˳�����", Toast.LENGTH_SHORT).show();
				mExitTime = System.currentTimeMillis();
			} else {
				 moveTaskToBack(false); 
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
    }  
	
	/**
	 * �첽����
	 */
	class Up extends AsyncTask<String, String, String> {
		@Override
		// onPreExecute(), �÷�������ִ��ʵ�ʵĺ�̨����ǰ��UI thread����
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(LoginActivity.this);
			pDialog.setMessage("���ڵ���..");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		// doInBackground(Params...), ����onPreExecute ����ִ�к�����ִ�У��÷��������ں�̨�߳��С�
		protected String doInBackground(String... args) {
			name = username.getText().toString();
			String pwd = password.getText().toString();

			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("userName", name));
			params.add(new BasicNameValuePair("password", pwd));

			try {
				JSONObject json = jsonParser.makeHttpRequest(url_login, "POST", params);
				String message = json.getString(TAG_MESSAGE);
				return message;
			} catch (Exception e) {
				e.printStackTrace();
				return "ffff";
			}
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		// onPostExecute(Result), ��doInBackground ִ����ɺ�onPostExecute ��������UI
		// thread���ã���̨�ļ�������ͨ���÷������ݵ�UI thread.
		protected void onPostExecute(String message) {
			pDialog.dismiss();
			if (message.equals("����ɹ�.")) {
				// Toast.makeText(LoginActivity.this, message,
				// Toast.LENGTH_SHORT).show();
				loginState = true;// ��½�ɹ�
				if (loginState) {
					if (isRememberMe()) {
						saveSharePreferences(true, true);
					} else {
						saveSharePreferences(true, false);
					}
				}
				Intent intent = new Intent(LoginActivity.this, MainActivity.class);
				intent.putExtra("userName", name);
				intent.putExtra("password",pwd);
				intent.putExtras(intent);
				startActivity(intent);
			} else {
				// ��������������
				Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
			}
			if (!chk_rememberMe.isChecked()) {
				clearSharePassword();
			}
		}
	}
}
