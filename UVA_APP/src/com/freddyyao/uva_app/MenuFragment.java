package com.freddyyao.uva_app;

import java.util.ArrayList;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.uva_app.CanShuActivity;
import com.example.uva_app.R;
import com.example.uva_app.activity.UserInfoActivity;
import com.example.uva_app.adapter.ListviewAdapter;
import com.example.uva_app.model.Data;
import com.example.uva_app.model.Model;
import com.example.uva_app.utils.User;

/**
 * @author freddyyao(姚志锋)
 */
public class MenuFragment extends ListFragment {

	private ListviewAdapter listAdapter;
	private ImageView UserImage;
	private View mRootView;
	private TextView name;
	
	public static MenuHandler menuHandler = null;
	private User user = null;

	private final String SHARE_LOGIN_TAG = "MAP_SHARE_LOGIN_TAG";
	private String sHARE_LOGIN_AUTOLOGIN = "MAP_LOGIN_AUTOLOGIN";
	public MenuFragment(User user){
		super();
		this.user=user;
		menuHandler=new MenuHandler(Looper.myLooper());
	}

 
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.list, null); // 添加ListVIEW布局
		return mRootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < Model.function.length; i++)
			list.add(Model.function[i]);
		listAdapter = new ListviewAdapter(getActivity(), list, Model.icon);
		setListAdapter(listAdapter);
		UserImage = (ImageView) mRootView.findViewById(R.id.imageview);
		name = (TextView) mRootView.findViewById(R.id.txt_name);
		name.setText(Data.getName());

		UserImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), UserInfoActivity.class);
				/*
				 * william
				 */
				Bundle userBundle = new Bundle();
				userBundle.putSerializable("user", user);
				intent.putExtras(userBundle);
				startActivity(intent);
			}
		});
	}

	@Override
	public void onListItemClick(ListView lv, View v, int position, long id) {
		Fragment newContent = null;
		switch (position) {

		case 0: {
			Intent intent = new Intent(getActivity(), PengSaActivity.class);
			startActivity(intent);

		}
			break;
		case 1: {
			Intent intent = new Intent(getActivity(), XiTongActivity.class);
			startActivity(intent);
		}
			break;
		case 2: {
			Intent intent = new Intent(getActivity(), CanShuActivity.class);
			startActivity(intent);
		}
			break;
		case 3: {
//			Intent intent = new Intent(getActivity(), XinJiActivity.class);
//			startActivity(intent);
			SharedPreferences share = getActivity().getSharedPreferences(SHARE_LOGIN_TAG, 0);
		    share.edit().putBoolean(sHARE_LOGIN_AUTOLOGIN, false).commit();       
		    share = null;
		    getActivity().finish();
		}
			break;
		}

	}

	// 切换Fragment视图内ring
	private void switchFragment(Fragment fragment) {
		if (getActivity() == null)
			return;

		if (getActivity() instanceof MainActivity) {
			MainActivity fca = (MainActivity) getActivity();
			fca.switchContent(fragment);
		}
	}
	/*
	 * william
	 */
	public class MenuHandler extends Handler {
	    public MenuHandler(Looper looper)
        {
            super(looper);
        }
        
        public void handleMessage(Message msg)
        {
            if (msg.obj != null)
            {
                name.setText(msg.obj.toString());
                user.setNickname(msg.obj.toString());
            }
        }
	}
}
