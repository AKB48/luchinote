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
	
    //����չʾ ����������̡�ң������Fragment
    private PengSaFragment1 fragment1;
    private PengSaFragment2 fragment2;
    private PengSaFragment3 fragment3;
    //���沼��
    private View pengsaLayout1;
    private View pengsaLayout2;
    private View pengsaLayout3;
	// ��Tab��������ʾ����ͼ��Ŀؼ�
    private ImageView newsImage;
	private ImageView messageImage;
	private ImageView contactsImage;
	
	private TextView duoji_Text;
	private TextView chiluopan_Text;
	private TextView yaokongqi_Text;
	//���ڶ�Fragment���й���
	private FragmentManager fragmentManager;
	
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.pengsa);
		setTitle("�����趨->���");
		getActionBar().setDisplayHomeAsUpEnabled(true);//����Actionbar
	    setOverflowShowingAlways();
        // ��ʼ������Ԫ��
 		initViews();
 		fragmentManager = getSupportFragmentManager();
 		// ��һ������ʱѡ�е�0��tab
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
	 * �������ȡ��ÿ����Ҫ�õ��Ŀؼ���ʵ���������������úñ�Ҫ�ĵ���¼���
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
		// ÿ��ѡ��֮ǰ��������ϴε�ѡ��״̬
		clearSelection();
		// ����һ��Fragment����
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		// �����ص����е�Fragment���Է�ֹ�ж��Fragment��ʾ�ڽ����ϵ����
		hideFragments(transaction);
		switch (index) {
		case 0:
			// ���������Ϣtabʱ���ı�ؼ���ͼƬ��������ɫ
			setTitle("����Լ�->���");
			newsImage.setImageResource(R.drawable.news_selected);
			duoji_Text.setTextColor(Color.WHITE);
			if (fragment1 == null) {
				// ���MessageFragmentΪ�գ��򴴽�һ������ӵ�������
				fragment1 = new PengSaFragment1();
				transaction.add(R.id.content, fragment1);
			} else {
				// ���MessageFragment��Ϊ�գ���ֱ�ӽ�����ʾ����
				transaction.show(fragment1);
			}
			break;
		case 1:
			// ���������ϵ��tabʱ���ı�ؼ���ͼƬ��������ɫ
			setTitle("����Լ�->������");
			messageImage.setImageResource(R.drawable.message_selected);
			chiluopan_Text.setTextColor(Color.WHITE);
			if (fragment2 == null) {
				// ���ContactsFragmentΪ�գ��򴴽�һ������ӵ�������
				fragment2 = new PengSaFragment2();
				transaction.add(R.id.content,fragment2);
			} else {
				// ���ContactsFragment��Ϊ�գ���ֱ�ӽ�����ʾ����
				transaction.show(fragment2);
			}
			break;
		case 2:
			// ������˶�̬tabʱ���ı�ؼ���ͼƬ��������ɫ
			setTitle("����Լ�->ң����");
			contactsImage.setImageResource(R.drawable.contacts_selected);
			yaokongqi_Text.setTextColor(Color.WHITE);
			if (fragment3 == null) {
				// ���NewsFragmentΪ�գ��򴴽�һ������ӵ�������
				fragment3 = new PengSaFragment3();
				transaction.add(R.id.content,fragment3);
			} else {
				// ���NewsFragment��Ϊ�գ���ֱ�ӽ�����ʾ����
				transaction.show(fragment3);
			}
			break;
		}
		transaction.commit();
	}
	
	/**
	 * ��������е�ѡ��״̬��
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
	 * �����е�Fragment����Ϊ����״̬��
	 * 
	 * @param transaction
	 *            ���ڶ�Fragmentִ�в���������
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
		case android.R.id.home: {// �˵���ť����¼���ͨ�����ActionBar��Homeͼ�갴ť���򿪻����˵�
			Intent intent = new Intent(PengSaActivity.this,MainActivity.class);
			startActivity(intent);

		}break;
		}
		return super.onOptionsItemSelected(item);
	}
	/**
	 * �˵����ͼ��
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
