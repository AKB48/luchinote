package com.example.uva_app.activity;

import java.lang.reflect.Field;

import com.example.uva_app.R;
import com.example.uva_app.model.Data;
import com.freddyyao.uva_app.MainActivity;
import com.freddyyao.uva_app.MenuFragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.ViewConfiguration;
import android.widget.ImageView;
import android.widget.TextView;

public class ShowInfoActivity extends Activity {

	private String text;
	private ImageView show_image;
	private TextView show_info;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.showinfo_activity);
		
		setTitle("用户信息");
		getActionBar().setDisplayHomeAsUpEnabled(true);// 设置Actionbar
		setOverflowShowingAlways();
		
		Intent intent=getIntent();
		text=intent.getStringExtra("text");
		
		show_image=(ImageView)findViewById(R.id.show_image);
		show_info=(TextView)findViewById(R.id.show_info);
		show_info.setText(text);
		
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
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		// 如果是返回键,直接返回到桌面
		if(keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME){
			Intent intent=new Intent(ShowInfoActivity.this,MainActivity.class);
			startActivity(intent);
			finish();
		}
		
		return super.onKeyDown(keyCode, event);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home: {// 菜单按钮点击事件，通过点击ActionBar的Home图标按钮来打开滑动菜单
			Intent intent=new Intent(ShowInfoActivity.this,MainActivity.class);
			startActivity(intent);
			finish();
		   }break;
		   
		}
		return super.onOptionsItemSelected(item);
	}
}
