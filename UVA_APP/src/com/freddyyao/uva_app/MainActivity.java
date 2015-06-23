package com.freddyyao.uva_app;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.example.uva_app.R;
import com.example.uva_app.activity.MakerActivity;
import com.example.uva_app.activity.ShowInfoActivity;
import com.example.uva_app.utils.User;
import com.freddyyao.uva_app.MyOrientationListener.OnOrientationListener;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

public class MainActivity extends SlidingFragmentActivity {

	// ��ͼ�ؼ�
	private MapView mMapView = null;
	// ��ͼʵ��
	private BaiduMap mBaiduMap;
	// ��λ�Ŀͻ���
	private LocationClient mLocationClient;
	// ��λ�ļ�����
	public MyLocationListener mMyLocationListener;
	// ��ǰ��λ��ģʽ
	private LocationMode mCurrentMode = LocationMode.NORMAL;
	// �Ƿ��ǵ�һ�ζ�λ
	private volatile boolean isFristLocation = true;
	// ����һ�εľ�γ��
	private double mCurrentLantitude;
	private double mCurrentLongitude;
	// ��ǰ�ľ���
	private float mCurrentAccracy;
	// ���򴫸����ļ�����
	private MyOrientationListener myOrientationListener;
	// ���򴫸���X�����ֵ
	private int mXDirection;
	// ��ͼ��λ��ģʽ
	private String[] mStyles = new String[] { "��ͼģʽ��������", "��ͼģʽ�����桿", "��ͼģʽ�����̡�" };
	private int mCurrentStyle = 0;
	private TextView conaddress;
	private GeoCoder mSearch;
	private Button btn_mark_start; // ������ǩ
	private Button btn_mark;// ���

	private Boolean flag = true;
	private String text;
	// ���ڶ�Fragment���й���
	private FragmentManager fragmentManager;
	private Fragment mContent;
	private List<LatLng> points = new ArrayList<LatLng>();
	private LatLng ll;
	// ��ʼ��ȫ�� bitmap ��Ϣ������ʱ��ʱ recycle
	/*
	 * william
	 */
	// �û����
	private User user = null;
	private BitmapDescriptor mIconMaker;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// ��ʹ��SDK�����֮ǰ��ʼ��context��Ϣ������ApplicationContext
		// ע��÷���Ҫ��setContentView����֮ǰʵ��
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.activity_main);
		conaddress = (TextView) findViewById(R.id.conaddress);
		conaddress.setText("�ص�");
		btn_mark_start = (Button) findViewById(R.id.btn_mark_start);
		btn_mark = (Button) findViewById(R.id.btn_mark);
		// ��ȡ�û���
		/*
		 * william
		 */
		Intent intent = getIntent();
		user = new User(intent.getStringExtra("userName"), intent.getStringExtra("nickName"));
		// String userName = intent.getStringExtra("userName");
		// Data.setName(userName);

		// ��ȡ��ͼ�ؼ�����
		mMapView = (MapView) findViewById(R.id.id_bmapView);
		initSlidingMenu(savedInstanceState);
		setTitle("�ҵı��"); // ���ñ���
		getActionBar().setDisplayHomeAsUpEnabled(true);// ����Actionbar
		setOverflowShowingAlways();

		// ��һ�ζ�λ
		isFristLocation = true;
		// ��ȡ��ͼ�ؼ�����
		mMapView = (MapView) findViewById(R.id.id_bmapView);
		// mMapView.showScaleControl(false);//�Ƿ���ʾ�����ߣ�Ĭ����ʾ
		mMapView.showZoomControls(false);// ���طŴ� ��С��ť

		// ��õ�ͼ��ʵ��
		if (mBaiduMap == null) {
			mBaiduMap = mMapView.getMap();
		}
		// ���ŵȼ�
		MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(18.0f);// 15.0f
		mBaiduMap.setMapStatus(msu);
		mIconMaker = BitmapDescriptorFactory.fromResource(R.drawable.icon_map_location);
		// �Զ��������·
		// LatLng p1 = new LatLng(22.538046, 113.933289);//γ�� ����
		// LatLng p2 = new LatLng(22.538046, 113.932947);
		// LatLng p3= new LatLng(22.539046, 113.932947);
		// points.add(p1);
		// points.add(p2);
		// points.add(p3);
		// OverlayOptions ooPolyline = new
		// PolylineOptions().width(10).color(0xAAFF0000).points(points);
		// mBaiduMap.addOverlay(ooPolyline);
		// points.add(ll);

		mBaiduMap.setOnMapLoadedCallback(new BaiduMap.OnMapLoadedCallback() {

			@Override
			public void onMapLoaded() {
				// TODO Auto-generated method stub
				mMapView.setScaleControlPosition(new Point(20, 100));// ���ñ�����λ��
			}
		});
		// ��������������ʵ��
		mSearch = GeoCoder.newInstance();
		mSearch.setOnGetGeoCodeResultListener(listener);// ����������ѯ����ص�����

		// ��ʼ����λ
		initMyLocation();
		// ��ʼ��������
		initOritationListener();

		final OnMapClickListener listener = new OnMapClickListener() {
			/**
			 * ��ͼ�����¼��ص�����
			 * 
			 * @param point
			 *            ����ĵ�������
			 */
			@SuppressLint("ShowToast")
			public void onMapClick(LatLng point) {
				if (flag) {
					LatLng ptCenter = point;
					Toast.makeText(getApplicationContext(), "" + point.longitude + "    " + point.latitude, 1).show();
					mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(ptCenter));
					popWin(point);
					flag = false;
				}
			}

			@Override
			public boolean onMapPoiClick(MapPoi arg0) {
				// TODO �Զ����ɵķ������
				return false;
			}
		};

		// �Ƿ�����ǩ
		btn_mark_start.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if ("�������".equals((String) btn_mark_start.getText())) {
					btn_mark.setVisibility(btn_mark.VISIBLE);
					btn_mark_start.setText("�رձ��");
					// flag=false;
					Log.i("�������", (String) btn_mark_start.getText());
				} else if ("�رձ��".equals((String) btn_mark_start.getText())) {
					btn_mark_start.setText("�������");
					// flag=true;
					Log.i("�رձ��", (String) btn_mark_start.getText());
					btn_mark.setVisibility(btn_mark.INVISIBLE);
					// mBaiduMap.setOnMapClickListener(listener);
				}
			}
		});

		btn_mark.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				flag = true;
				mBaiduMap.setOnMapClickListener(listener);
				Intent intent = new Intent(MainActivity.this, MakerActivity.class);
				startActivity(intent);

			}
		});
		// mark
		mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
			@Override
			public boolean onMarkerClick(Marker marker) {
				// TODO Auto-generated method stub
				Log.i("Location", "location");
				InfoWindow mInfoWindow;
				// ����һ��TextView�û��ڵ�ͼ����ʾInfoWindow
				TextView location = new TextView(getApplicationContext());
				location.setBackgroundResource(R.drawable.custom_loc);
				location.setPadding(30, 20, 30, 50);
				Log.i("Location", location.toString());

				// ��marker���ڵľ�γ�ȵ���Ϣת������Ļ�ϵ�����
				final LatLng ll = marker.getPosition();
				// location.setText("Maker"+ll.latitude+ll.longitude);
				Point p = mBaiduMap.getProjection().toScreenLocation(ll);
				p.y -= 47;
				LatLng llInfo = mBaiduMap.getProjection().fromScreenLocation(p);
				// Ϊ������InfoWindow��ӵ���¼�
				mInfoWindow = new InfoWindow(location, llInfo, 0);
				// ��ʾInfoWindow
				// mBaiduMap.hideInfoWindow();
				mBaiduMap.showInfoWindow(mInfoWindow);

				location.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						// mBaiduMap.hideInfoWindow();
						Intent intent = new Intent(MainActivity.this, ShowInfoActivity.class);
						intent.putExtra("text", text);
						startActivity(intent);
						// conaddress.setText(text+" "+ll.latitude+" "+ll.longitude);
						// Toast.makeText(MainActivity.this,
						// "Maker"+ll.latitude+ll.longitude,Toast.LENGTH_LONG).show();
					}
				});
				return true;
			}
		});
	}

	/**
	 * MainActivity ʹ��SingleTask
	 */
	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		text = intent.getStringExtra("text");// �ı���Ϣ
		if (text != null) {
			Log.i("text", text);
		}
		super.onNewIntent(intent);
	}

	/**
      * 
     */
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
	 * ��ʼ�������˵�
	 */
	private void initSlidingMenu(Bundle savedInstanceState) {

		// ���û����˵���ͼ����
		setBehindContentView(R.layout.menu_frame);
		getSupportFragmentManager().beginTransaction().replace(R.id.menu_frame, new MenuFragment(this.user)).commit();
		// ���û�����Ļ��Χ
		// getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);//ȫ��
		getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);// ��Ե
		getSlidingMenu().setShadowWidthRes(R.dimen.shadow_width);
		getSlidingMenu().setShadowDrawable(R.drawable.shadow);
		getSlidingMenu().setBehindOffsetRes(R.dimen.slidingmenu_offset);
		// �����Ƿ��뵭��
		getSlidingMenu().setFadeEnabled(true);
		getSlidingMenu().setFadeDegree(0.35f);// 0.35f
		getSlidingMenu().setBehindScrollScale(0);
		// ��ʱ û��
		getSlidingMenu().setOnClosedListener(new SlidingMenu.OnClosedListener() {
			@Override
			public void onClosed() {

			}
		});

		/**
		 * william
		 */
		Message nicknameMessage = Message.obtain();
		nicknameMessage.obj = user.getNickname();
		MenuFragment.menuHandler.sendMessage(nicknameMessage);
	}

	/**
	 * �л�Fragment��Ҳ���л���ͼ������
	 */
	public void switchContent(Fragment fragment) {
		mContent = fragment;
		getSupportFragmentManager().beginTransaction().replace(R.id.content, fragment).commit();
		getSlidingMenu().showContent();
	}

	@Override
	protected void onStart() {
		// ����ͼ�㶨λ
		mBaiduMap.setMyLocationEnabled(true);
		if (!mLocationClient.isStarted()) {
			mLocationClient.start();
		}
		// �������򴫸���
		myOrientationListener.start();
		super.onStart();
	}

	@Override
	protected void onStop() {
		// �ر�ͼ�㶨λ
		mBaiduMap.setMyLocationEnabled(false);
		mLocationClient.stop();
		// �رշ��򴫸���
		myOrientationListener.stop();
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mMapView.onDestroy();
		mIconMaker.recycle();
		mMapView = null;
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mMapView.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mMapView.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * ����Fragment��״̬
	 */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		// getSupportFragmentManager().putFragment(outState, "mContent",
		// mContent);
	}

	/**
	 * ��ʼ�����򴫸���
	 */
	private void initOritationListener() {
		myOrientationListener = new MyOrientationListener(getApplicationContext());
		myOrientationListener.setOnOrientationListener(new OnOrientationListener() {
			@Override
			public void onOrientationChanged(float x) {
				mXDirection = (int) x;
				// ���춨λ����
				MyLocationData locData = new MyLocationData.Builder().accuracy(mCurrentAccracy)
				// �˴����ÿ����߻�ȡ���ķ�����Ϣ��˳ʱ��0-360
						.direction(mXDirection).latitude(mCurrentLantitude).longitude(mCurrentLongitude).build();
				// ���ö�λ����
				mBaiduMap.setMyLocationData(locData);
				// �����Զ���ͼ�� (��λģʽ �Ƿ��з�����Ϣ �Զ���)
				BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory.fromResource(R.drawable.navi_map_gps_locked);
				MyLocationConfiguration config = new MyLocationConfiguration(mCurrentMode, true, mCurrentMarker);
				mBaiduMap.setMyLocationConfigeration(config);
			}
		});
	}

	/**
	 * ��ʼ����λ��ش���
	 */
	private void initMyLocation() {
		// ��λ��ʼ��
		mLocationClient = new LocationClient(this);
		mMyLocationListener = new MyLocationListener();
		mLocationClient.registerLocationListener(mMyLocationListener);
		// ���ö�λ���������
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// ��gps
		option.setCoorType("bd09ll"); // ������������ ��bd0911 �ٶȾ�γ������ϵ��
		// ���ö�λʱ���� ��λ�����û�䣬�Ͳ��ᷢ����������
		option.setScanSpan(1000);
		mLocationClient.setLocOption(option);
	}

	/**
	 * ʵ��ʵλ�ص�����
	 */
	public class MyLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view ���ٺ��ڴ����½��յ�λ��
			if (location == null || mMapView == null)
				return;
			// ���춨λ����
			MyLocationData locData = new MyLocationData.Builder().accuracy(location.getRadius())
			// �˴����ÿ����߻�ȡ���ķ�����Ϣ��˳ʱ��0-360
					.direction(mXDirection).latitude(location.getLatitude()).longitude(location.getLongitude()).build();
			mCurrentAccracy = location.getRadius();
			// ���ö�λ����
			// delay(200);
			mBaiduMap.setMyLocationData(locData);
			mCurrentLantitude = location.getLatitude();
			Log.i("Latitude", mCurrentLantitude + " " + mCurrentLongitude + " " + mCurrentMode);
			mCurrentLongitude = location.getLongitude();

			// �����Զ���ͼ��
			BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory.fromResource(R.drawable.navi_map_gps_locked);
			MyLocationConfiguration config = new MyLocationConfiguration(mCurrentMode, true, mCurrentMarker);
			mBaiduMap.setMyLocationConfigeration(config);
			// String loc_info=location.getAddrStr();
			// ��һ�ζ�λʱ������ͼλ���ƶ�����ǰλ��
			if (isFristLocation) {
				isFristLocation = false;
				LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				mBaiduMap.animateMapStatus(u);
				// Log.i("AddrStr",location.getAddrStr());
				conaddress.setText(location.getAddrStr());
				// popWin(ll);
			}
		}
	}

	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		if (featureId == Window.FEATURE_OPTIONS_PANEL && menu != null) {
			if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
				try {
					Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
					m.setAccessible(true);
					m.invoke(menu, true);
				} catch (Exception e) {
				}
			}
		}
		return super.onMenuOpened(featureId, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home: // �˵���ť����¼���ͨ�����ActionBar��Homeͼ�갴ť���򿪻����˵�
			toggle();
			break;
		case R.id.id_menu_map_common:
			// ��ͨ��ͼ
			mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
			break;
		case R.id.id_menu_map_site:// ���ǵ�ͼ
			mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
			break;
		case R.id.id_menu_map_traffic:
			// ������ͨͼ
			if (mBaiduMap.isTrafficEnabled()) {
				item.setTitle("����ʵʱ��ͨ");
				mBaiduMap.setTrafficEnabled(false);
			} else {
				item.setTitle("�ر�ʵʱ��ͨ");
				mBaiduMap.setTrafficEnabled(true);
			}
			break;
		case R.id.id_menu_map_myLoc:
			center2myLoc();
			break;
		case R.id.id_menu_map_style:
			mCurrentStyle = (++mCurrentStyle) % mStyles.length;
			item.setTitle(mStyles[mCurrentStyle]);
			// �����Զ���ͼ��
			switch (mCurrentStyle) {
			case 0:
				// mCurrentMode = LocationMode.NORMAL;
				// mBaiduMap.clear();
				break;
			case 1:
				// mCurrentMode = LocationMode.FOLLOWING;
				LatLng latLng = null;
				OverlayOptions overlayOptions = null;
				Marker marker = null;

				for (com.example.uva_app.model.Info info : com.example.uva_app.model.Info.infos) {
					// λ��
					latLng = new LatLng(info.getLatitude(), info.getLongitude());
					// ͼ��
					overlayOptions = new MarkerOptions().position(latLng).icon(mIconMaker).zIndex(5);
					marker = (Marker) (mBaiduMap.addOverlay(overlayOptions));
					Bundle bundle = new Bundle();
					bundle.putSerializable("info", info);
					marker.setExtraInfo(bundle);
					Log.i("points", info.getLatitude() + " " + info.getLongitude());
					latLng = new LatLng(info.getLatitude(), info.getLongitude());

					if (latLng != null) {
						Log.i("points", "point1");
						points.add(latLng);
					}
					Log.i("points", "point2");

				}

				OverlayOptions ooPolyline = new PolylineOptions().width(10).color(0xAAFF0000).points(points);
				mBaiduMap.addOverlay(ooPolyline);
				// ����ͼ�Ƶ������һ����γ��λ��
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(latLng);
				mBaiduMap.setMapStatus(u);
				break;
			case 2:
				// mCurrentMode = LocationMode.COMPASS;

				break;
			}
			BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory.fromResource(R.drawable.navi_map_gps_locked);
			MyLocationConfiguration config = new MyLocationConfiguration(mCurrentMode, true, mCurrentMarker);
			mBaiduMap.setMyLocationConfigeration(config);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * ��ͼ�ƶ����ҵ�λ��,�˴��������·���λ����Ȼ��λ�� ֱ�������һ�ξ�γ�ȣ������ʱ��û�ж�λ�ɹ������ܻ���ʾЧ������
	 */
	private void center2myLoc() {
		LatLng ll = new LatLng(mCurrentLantitude, mCurrentLongitude);
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
		mBaiduMap.animateMapStatus(u);
	}

	// ����λ�ü�������
	OnGetGeoCoderResultListener listener = new OnGetGeoCoderResultListener() {
		public void onGetGeoCodeResult(GeoCodeResult result) {
			if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
				// û�м��������
			}
			// ��ȡ���������
			Toast.makeText(MainActivity.this, result.getLocation() + "LLL", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
			if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
				Toast.makeText(MainActivity.this, "��Ǹ��δ���ҵ����", Toast.LENGTH_LONG).show();
				return;
			}
			mBaiduMap.clear();
			mBaiduMap.addOverlay(new MarkerOptions().position(result.getLocation()).icon(
					BitmapDescriptorFactory.fromResource(R.drawable.icon_map_location)));
			mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result.getLocation()));
			conaddress.setText(result.getAddress());
			Toast.makeText(MainActivity.this, result.getAddress(), Toast.LENGTH_LONG).show();
		}
	};

	// �Զ�����ʾͼ��
	public void popWin(LatLng point) {
		// ����Markerͼ��
		BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_map_location);
		// ����MarkerOption�������ڵ�ͼ�����Marker
		OverlayOptions option = new MarkerOptions().position(point).icon(bitmap);
		// �ڵ�ͼ�����Marker������ʾ
		// mBaiduMap.clear();
		mBaiduMap.addOverlay(option);
	}

	/**
	 * william -->freddyyao ���η���
	 */
	private long mExitTime;
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if ((System.currentTimeMillis() - mExitTime) > 2000) {
				Toast.makeText(this, "�ٰ�һ���˳�����", Toast.LENGTH_SHORT).show();
				mExitTime = System.currentTimeMillis();
			} else {
				 //moveTaskToBack(false);
				finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
