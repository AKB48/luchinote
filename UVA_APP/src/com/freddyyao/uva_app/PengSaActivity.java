package com.freddyyao.uva_app;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.uva_app.R;
import com.example.uva_app.fragment.PengSaFragment1;
import com.example.uva_app.fragment.PengSaFragment2;
import com.example.uva_app.fragment.PengSaFragment3;

public class PengSaActivity extends FragmentActivity {
	
    //用于展示 舵机、磁罗盘、遥控器的Fragment
    private PengSaFragment1 fragment1;
    private PengSaFragment2 fragment2;
    private PengSaFragment3 fragment3;
    //界面布局
    private View pengsaLayout1;
    private View pengsaLayout2;
    private View pengsaLayout3;
	// 在Tab布局上显示设置图标的控件
    private ImageView newsImage;
	private ImageView messageImage;
	private ImageView contactsImage;
	
	private TextView duoji_Text;
	private TextView chiluopan_Text;
	private TextView yaokongqi_Text;
	//用于对Fragment进行管理
	private FragmentManager fragmentManager;
	
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.pengsa);
		setTitle("喷洒设定->舵机");
		getActionBar().setDisplayHomeAsUpEnabled(true);//设置Actionbar
	    setOverflowShowingAlways();
        // 初始化布局元素
 		initViews();
 		fragmentManager = getSupportFragmentManager();
 		// 第一次启动时选中第0个tab
 		setTabSelection(0);
	    
	   
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
	 * 在这里获取到每个需要用到的控件的实例，并给它们设置好必要的点击事件。
	 */
	private void initViews() {
		pengsaLayout1 = findViewById(R.id.pengsaLayout1);
		pengsaLayout2 = findViewById(R.id.pengsaLayout2);
		pengsaLayout3 = findViewById(R.id.pengsaLayout3);
		
		newsImage = (ImageView) findViewById(R.id.news_image);
		messageImage = (ImageView) findViewById(R.id.message_image);
		contactsImage = (ImageView) findViewById(R.id.contacts_image);
		
		duoji_Text = (TextView) findViewById(R.id.duoji_text);
		chiluopan_Text = (TextView) findViewById(R.id.chiluopan_text);
		yaokongqi_Text = (TextView) findViewById(R.id.yaokongqi_text);
	
		pengsaLayout1.setOnClickListener(myClickListener);
		pengsaLayout2.setOnClickListener(myClickListener);
		pengsaLayout3.setOnClickListener(myClickListener);
		
	}
	OnClickListener myClickListener=new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.pengsaLayout1:
				setTabSelection(0);
				break;
			case R.id.pengsaLayout2:
				setTabSelection(1);
				break;
			case R.id.pengsaLayout3:
				setTabSelection(2);
				break;
			
			default:
				break;
			}
		}
	};
	
	private void setTabSelection(int index) {
		// 每次选中之前先清楚掉上次的选中状态
		clearSelection();
		// 开启一个Fragment事务
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		// 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
		hideFragments(transaction);
		switch (index) {
		case 0:
			// 当点击了消息tab时，改变控件的图片和文字颜色
			setTitle("起飞自检->舵机");
			newsImage.setImageResource(R.drawable.news_selected);
			duoji_Text.setTextColor(Color.WHITE);
			if (fragment1 == null) {
				// 如果MessageFragment为空，则创建一个并添加到界面上
				fragment1 = new PengSaFragment1();
				transaction.add(R.id.content, fragment1);
			} else {
				// 如果MessageFragment不为空，则直接将它显示出来
				transaction.show(fragment1);
			}
			break;
		case 1:
			// 当点击了联系人tab时，改变控件的图片和文字颜色
			setTitle("起飞自检->磁罗盘");
			messageImage.setImageResource(R.drawable.message_selected);
			chiluopan_Text.setTextColor(Color.WHITE);
			if (fragment2 == null) {
				// 如果ContactsFragment为空，则创建一个并添加到界面上
				fragment2 = new PengSaFragment2();
				transaction.add(R.id.content,fragment2);
			} else {
				// 如果ContactsFragment不为空，则直接将它显示出来
				transaction.show(fragment2);
			}
			break;
		case 2:
			// 当点击了动态tab时，改变控件的图片和文字颜色
			setTitle("起飞自检->遥控器");
			contactsImage.setImageResource(R.drawable.contacts_selected);
			yaokongqi_Text.setTextColor(Color.WHITE);
			if (fragment3 == null) {
				// 如果NewsFragment为空，则创建一个并添加到界面上
				fragment3 = new PengSaFragment3();
				transaction.add(R.id.content,fragment3);
			} else {
				// 如果NewsFragment不为空，则直接将它显示出来
				transaction.show(fragment3);
			}
			break;
		}
		transaction.commit();
	}
	
	/**
	 * 清除掉所有的选中状态。
	 */
	private void clearSelection() {
		newsImage.setImageResource(R.drawable.news_unselected);
		duoji_Text.setTextColor(Color.parseColor("#82858b"));
		messageImage.setImageResource(R.drawable.message_unselected);
		chiluopan_Text.setTextColor(Color.parseColor("#82858b"));
		contactsImage.setImageResource(R.drawable.contacts_unselected);
		yaokongqi_Text.setTextColor(Color.parseColor("#82858b"));
		
	}

	/**
	 * 将所有的Fragment都置为隐藏状态。
	 * 
	 * @param transaction
	 *            用于对Fragment执行操作的事务
	 */
	private void hideFragments(FragmentTransaction transaction) {
		if (fragment1 != null) {
			transaction.hide(fragment1);
		}
		if (fragment2 != null) {
			transaction.hide(fragment2);
		}
		if (fragment3 != null) {
			transaction.hide(fragment3);
		}
		
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home: {// 菜单按钮点击事件，通过点击ActionBar的Home图标按钮来打开滑动菜单
			Intent intent = new Intent(PengSaActivity.this,MainActivity.class);
			startActivity(intent);

		}break;
		}
		return super.onOptionsItemSelected(item);
	}
	/**
	 * 菜单添加图标
	 */
	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		// TODO Auto-generated method stub
		if (featureId == Window.FEATURE_ACTION_BAR && menu != null) {
			if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
				try {
					Method m = menu.getClass().getDeclaredMethod(
							"setOptionalIconsVisible", Boolean.TYPE);
					m.setAccessible(true);
					m.invoke(menu, true);
				} catch (Exception e) {
				}
			}
		}
		return super.onMenuOpened(featureId, menu);
	}
	
}
