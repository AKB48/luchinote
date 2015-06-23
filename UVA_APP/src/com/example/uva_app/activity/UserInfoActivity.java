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
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.uva_app.R;
import com.example.uva_app.model.Data;
import com.example.uva_app.utils.DialogUtil;
import com.example.uva_app.utils.HttpRequestType;
import com.example.uva_app.utils.HttpSender;
import com.example.uva_app.utils.HttpStatusCode;
import com.example.uva_app.utils.HttpUrl;
import com.example.uva_app.utils.IResponse;
import com.example.uva_app.utils.User;
import com.freddyyao.uva_app.MenuFragment;

public class UserInfoActivity extends Activity {

	private ImageView userImage; // �û�ͼ��
	private EditText input_username;
	private TextView tv; // �û���
	
	/*
	 * william
	 */
	private User user = null;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_info_activity);
		/*
		 * william
		 */
		Intent intent = getIntent();
		this.user = (User)intent.getSerializableExtra("user");
		
		setTitle("�û���Ϣ");
		getActionBar().setDisplayHomeAsUpEnabled(true);// ����Actionbar
		setOverflowShowingAlways();
		//
		input_username = (EditText) findViewById(R.id.input_username);
		input_username.addTextChangedListener(watcher);

		tv = (TextView) findViewById(R.id.user_name1);
		tv.setText(this.user.getNickname());
		input_username.setText(this.user.getNickname());
		userImage = (ImageView) findViewById(R.id.userimage);

		userImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String[] m_Items = { "����", "�������ѡ��" };
				AlertDialog.Builder dialog = new AlertDialog.Builder(UserInfoActivity.this);
				dialog.setItems(m_Items, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:
							Toast.makeText(UserInfoActivity.this, "����", Toast.LENGTH_LONG).show();
							break;
						case 1:
							Toast.makeText(UserInfoActivity.this, "�������ѡ��", Toast.LENGTH_LONG).show();
							break;
						default:
							break;
						}
					}
				});
				dialog.create().show();
			}
		});
	}

	/**
	 * EditText ��������仯
	 */
	TextWatcher watcher = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			// TODO Auto-generated method stub
			tv.setText(s);
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			// TODO Auto-generated method stub
		}

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.userinfo, menu);
		return super.onCreateOptionsMenu(menu);
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

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home: {// �˵���ť����¼���ͨ�����ActionBar��Homeͼ�갴ť���򿪻����˵�
			finish();
		}
			break;
		case R.id.action_save: {
			  /*
		     * william
		     */
			String nickName=input_username.getText().toString();
			//�ж��Ƿ�Ϊ��
			if (!validate(nickName)) {
				
			}else {
			//����������
			    List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("username", this.user.getUsername()));
                params.add(new BasicNameValuePair("nickname", nickName));
                
                try {
                    HttpSender.post(HttpUrl.CHANGE_NICKNAME, params, new UserInfoResponse(), HttpRequestType.POST_DATA);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
			}
           
		}
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * �ж������Ƿ���Ч
	 */
	private boolean validate(String userName) {
		if (userName.equals("")) {
			DialogUtil.showDialog(this, "���벻��Ϊ�գ�", false);
			return false;
		}
		return true;
	}
	
	/*
	 * william
	 */
	class UserInfoResponse implements IResponse
	{
        @Override
        public void onResponse(Object respContent) {
            try {
                JSONObject resp= new JSONObject((String) respContent);
                int status = resp.getInt("status");
                String respInfo = resp.getString("info");
                String nickname = resp.getString("nickname");
                if (status == HttpStatusCode.HTTP_OK)
                {
                    Message message = Message.obtain();
                    //message.obj = input_username.getText().toString();
                    message.obj = nickname;
                    MenuFragment.menuHandler.sendMessage(message);
                    Toast.makeText(getApplicationContext(), respInfo, Toast.LENGTH_LONG).show();
                    finish();
                }
                else if (status == HttpStatusCode.HTTP_ERROR)
                {
                    Toast.makeText(getApplicationContext(), respInfo, Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
	}
}
