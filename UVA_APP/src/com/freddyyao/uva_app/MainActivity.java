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

	// 地图控件
	private MapView mMapView = null;
	// 地图实例
	private BaiduMap mBaiduMap;
	// 定位的客户端
	private LocationClient mLocationClient;
	// 定位的监听器
	public MyLocationListener mMyLocationListener;
	// 当前定位的模式
	private LocationMode mCurrentMode = LocationMode.NORMAL;
	// 是否是第一次定位
	private volatile boolean isFristLocation = true;
	// 最新一次的经纬度
	private double mCurrentLantitude;
	private double mCurrentLongitude;
	// 当前的精度
	private float mCurrentAccracy;
	// 方向传感器的监听器
	private MyOrientationListener myOrientationListener;
	// 方向传感器X方向的值
	private int mXDirection;
	// 地图定位的模式
	private String[] mStyles = new String[] { "地图模式【正常】", "地图模式【跟随】", "地图模式【罗盘】" };
	private int mCurrentStyle = 0;
	private TextView conaddress;
	private GeoCoder mSearch;
	private Button btn_mark_start; // 开启标签
	private Button btn_mark;// 标记

	private Boolean flag = true;
	private String text;
	// 用于对Fragment进行管理
	private FragmentManager fragmentManager;
	private Fragment mContent;
	private List<LatLng> points = new ArrayList<LatLng>();
	private LatLng ll;
	// 初始化全局 bitmap 信息，不用时及时 recycle
	/*
	 * william
	 */
	// 用户相关
	private User user = null;
	private BitmapDescriptor mIconMaker;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// 在使用SDK各组件之前初始化context信息，传入ApplicationContext
		// 注意该方法要再setContentView方法之前实现
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.activity_main);
		conaddress = (TextView) findViewById(R.id.conaddress);
		conaddress.setText("地点");
		btn_mark_start = (Button) findViewById(R.id.btn_mark_start);
		btn_mark = (Button) findViewById(R.id.btn_mark);
		// 获取用户名
		/*
		 * william
		 */
		Intent intent = getIntent();
		user = new User(intent.getStringExtra("userName"), intent.getStringExtra("nickName"));
		// String userName = intent.getStringExtra("userName");
		// Data.setName(userName);

		// 获取地图控件引用
		mMapView = (MapView) findViewById(R.id.id_bmapView);
		initSlidingMenu(savedInstanceState);
		setTitle("我的标记"); // 设置标题
		getActionBar().setDisplayHomeAsUpEnabled(true);// 设置Actionbar
		setOverflowShowingAlways();

		// 第一次定位
		isFristLocation = true;
		// 获取地图控件引用
		mMapView = (MapView) findViewById(R.id.id_bmapView);
		// mMapView.showScaleControl(false);//是否显示比例尺，默认显示
		mMapView.showZoomControls(false);// 隐藏放大 缩小按钮

		// 获得地图的实例
		if (mBaiduMap == null) {
			mBaiduMap = mMapView.getMap();
		}
		// 缩放等级
		MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(18.0f);// 15.0f
		mBaiduMap.setMapStatus(msu);
		mIconMaker = BitmapDescriptorFactory.fromResource(R.drawable.icon_map_location);
		// 自定义绘制线路
		// LatLng p1 = new LatLng(22.538046, 113.933289);//纬度 经度
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
				mMapView.setScaleControlPosition(new Point(20, 100));// 设置比例尺位置
			}
		});
		// 创建地理编码检索实例
		mSearch = GeoCoder.newInstance();
		mSearch.setOnGetGeoCodeResultListener(listener);// 反地理编码查询结果回调函数

		// 初始化定位
		initMyLocation();
		// 初始化传感器
		initOritationListener();

		final OnMapClickListener listener = new OnMapClickListener() {
			/**
			 * 地图单击事件回调函数
			 * 
			 * @param point
			 *            点击的地理坐标
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
				// TODO 自动生成的方法存根
				return false;
			}
		};

		// 是否开启标签
		btn_mark_start.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if ("开启标记".equals((String) btn_mark_start.getText())) {
					btn_mark.setVisibility(btn_mark.VISIBLE);
					btn_mark_start.setText("关闭标记");
					// flag=false;
					Log.i("开启标记", (String) btn_mark_start.getText());
				} else if ("关闭标记".equals((String) btn_mark_start.getText())) {
					btn_mark_start.setText("开启标记");
					// flag=true;
					Log.i("关闭标记", (String) btn_mark_start.getText());
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
				// 生成一个TextView用户在地图中显示InfoWindow
				TextView location = new TextView(getApplicationContext());
				location.setBackgroundResource(R.drawable.custom_loc);
				location.setPadding(30, 20, 30, 50);
				Log.i("Location", location.toString());

				// 将marker所在的经纬度的信息转化成屏幕上的坐标
				final LatLng ll = marker.getPosition();
				// location.setText("Maker"+ll.latitude+ll.longitude);
				Point p = mBaiduMap.getProjection().toScreenLocation(ll);
				p.y -= 47;
				LatLng llInfo = mBaiduMap.getProjection().fromScreenLocation(p);
				// 为弹出的InfoWindow添加点击事件
				mInfoWindow = new InfoWindow(location, llInfo, 0);
				// 显示InfoWindow
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
	 * MainActivity 使用SingleTask
	 */
	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		text = intent.getStringExtra("text");// 文本信息
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
	 * 初始化滑动菜单
	 */
	private void initSlidingMenu(Bundle savedInstanceState) {

		// 设置滑动菜单视图界面
		setBehindContentView(R.layout.menu_frame);
		getSupportFragmentManager().beginTransaction().replace(R.id.menu_frame, new MenuFragment(this.user)).commit();
		// 设置滑动屏幕范围
		// getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);//全屏
		getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);// 边缘
		getSlidingMenu().setShadowWidthRes(R.dimen.shadow_width);
		getSlidingMenu().setShadowDrawable(R.drawable.shadow);
		getSlidingMenu().setBehindOffsetRes(R.dimen.slidingmenu_offset);
		// 设置是否淡入淡出
		getSlidingMenu().setFadeEnabled(true);
		getSlidingMenu().setFadeDegree(0.35f);// 0.35f
		getSlidingMenu().setBehindScrollScale(0);
		// 暂时 没用
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
	 * 切换Fragment，也是切换视图的内容
	 */
	public void switchContent(Fragment fragment) {
		mContent = fragment;
		getSupportFragmentManager().beginTransaction().replace(R.id.content, fragment).commit();
		getSlidingMenu().showContent();
	}

	@Override
	protected void onStart() {
		// 开启图层定位
		mBaiduMap.setMyLocationEnabled(true);
		if (!mLocationClient.isStarted()) {
			mLocationClient.start();
		}
		// 开启方向传感器
		myOrientationListener.start();
		super.onStart();
	}

	@Override
	protected void onStop() {
		// 关闭图层定位
		mBaiduMap.setMyLocationEnabled(false);
		mLocationClient.stop();
		// 关闭方向传感器
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
	 * 保存Fragment的状态
	 */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		// getSupportFragmentManager().putFragment(outState, "mContent",
		// mContent);
	}

	/**
	 * 初始化方向传感器
	 */
	private void initOritationListener() {
		myOrientationListener = new MyOrientationListener(getApplicationContext());
		myOrientationListener.setOnOrientationListener(new OnOrientationListener() {
			@Override
			public void onOrientationChanged(float x) {
				mXDirection = (int) x;
				// 构造定位数据
				MyLocationData locData = new MyLocationData.Builder().accuracy(mCurrentAccracy)
				// 此处设置开发者获取到的方向信息，顺时针0-360
						.direction(mXDirection).latitude(mCurrentLantitude).longitude(mCurrentLongitude).build();
				// 设置定位数据
				mBaiduMap.setMyLocationData(locData);
				// 设置自定义图标 (定位模式 是否含有方向信息 自定义)
				BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory.fromResource(R.drawable.navi_map_gps_locked);
				MyLocationConfiguration config = new MyLocationConfiguration(mCurrentMode, true, mCurrentMarker);
				mBaiduMap.setMyLocationConfigeration(config);
			}
		});
	}

	/**
	 * 初始化定位相关代码
	 */
	private void initMyLocation() {
		// 定位初始化
		mLocationClient = new LocationClient(this);
		mMyLocationListener = new MyLocationListener();
		mLocationClient.registerLocationListener(mMyLocationListener);
		// 设置定位的相关配置
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型 （bd0911 百度经纬度坐标系）
		// 设置定位时间间隔 ，位置如果没变，就不会发起网络请求
		option.setScanSpan(1000);
		mLocationClient.setLocOption(option);
	}

	/**
	 * 实现实位回调监听
	 */
	public class MyLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location == null || mMapView == null)
				return;
			// 构造定位数据
			MyLocationData locData = new MyLocationData.Builder().accuracy(location.getRadius())
			// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(mXDirection).latitude(location.getLatitude()).longitude(location.getLongitude()).build();
			mCurrentAccracy = location.getRadius();
			// 设置定位数据
			// delay(200);
			mBaiduMap.setMyLocationData(locData);
			mCurrentLantitude = location.getLatitude();
			Log.i("Latitude", mCurrentLantitude + " " + mCurrentLongitude + " " + mCurrentMode);
			mCurrentLongitude = location.getLongitude();

			// 设置自定义图标
			BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory.fromResource(R.drawable.navi_map_gps_locked);
			MyLocationConfiguration config = new MyLocationConfiguration(mCurrentMode, true, mCurrentMarker);
			mBaiduMap.setMyLocationConfigeration(config);
			// String loc_info=location.getAddrStr();
			// 第一次定位时，将地图位置移动到当前位置
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
		case android.R.id.home: // 菜单按钮点击事件，通过点击ActionBar的Home图标按钮来打开滑动菜单
			toggle();
			break;
		case R.id.id_menu_map_common:
			// 普通地图
			mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
			break;
		case R.id.id_menu_map_site:// 卫星地图
			mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
			break;
		case R.id.id_menu_map_traffic:
			// 开启交通图
			if (mBaiduMap.isTrafficEnabled()) {
				item.setTitle("开启实时交通");
				mBaiduMap.setTrafficEnabled(false);
			} else {
				item.setTitle("关闭实时交通");
				mBaiduMap.setTrafficEnabled(true);
			}
			break;
		case R.id.id_menu_map_myLoc:
			center2myLoc();
			break;
		case R.id.id_menu_map_style:
			mCurrentStyle = (++mCurrentStyle) % mStyles.length;
			item.setTitle(mStyles[mCurrentStyle]);
			// 设置自定义图标
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
					// 位置
					latLng = new LatLng(info.getLatitude(), info.getLongitude());
					// 图标
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
				// 将地图移到到最后一个经纬度位置
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
	 * 地图移动到我的位置,此处可以重新发定位请求，然后定位； 直接拿最近一次经纬度，如果长时间没有定位成功，可能会显示效果不好
	 */
	private void center2myLoc() {
		LatLng ll = new LatLng(mCurrentLantitude, mCurrentLongitude);
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
		mBaiduMap.animateMapStatus(u);
	}

	// 地理位置检索监听
	OnGetGeoCoderResultListener listener = new OnGetGeoCoderResultListener() {
		public void onGetGeoCodeResult(GeoCodeResult result) {
			if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
				// 没有检索到结果
			}
			// 获取地理编码结果
			Toast.makeText(MainActivity.this, result.getLocation() + "LLL", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
			if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
				Toast.makeText(MainActivity.this, "抱歉，未能找到结果", Toast.LENGTH_LONG).show();
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

	// 自定义提示图标
	public void popWin(LatLng point) {
		// 构建Marker图标
		BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_map_location);
		// 构建MarkerOption，用于在地图上添加Marker
		OverlayOptions option = new MarkerOptions().position(point).icon(bitmap);
		// 在地图上添加Marker，并显示
		// mBaiduMap.clear();
		mBaiduMap.addOverlay(option);
	}

	/**
	 * william -->freddyyao 两次返回
	 */
	private long mExitTime;
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if ((System.currentTimeMillis() - mExitTime) > 2000) {
				Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
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
