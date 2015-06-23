package com.example.uva_app.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.uva_app.R;

public class ListviewAdapter extends BaseAdapter {

	// 定义一个布局泵，它的作用类似于findViewById(),不同点是LayoutInflater是用来找layout下xml布局文件，并且实例化
	private LayoutInflater inflater = null;
	private ArrayList<String> items = null;
	private ArrayList<Bitmap> icons = null;
	//用来保存选中的item
	private int selectedPosition = -1;

	public void setSelectedPosition(int position) {
		selectedPosition = position;
	}

	/**
	 * 构造函数
	 * @param context
	 * @param arraylist  显示的文本
	 * @param iconRes    显示的图标
	 */
	public ListviewAdapter(Context context, ArrayList<String> arraylist,
			int[] iconRes) {
		// TODO Auto-generated constructor stub
		// LayoutInflater用来加载界面
		inflater = LayoutInflater.from(context);
		// 保存适配器中的每项的文字信息
		this.items = arraylist;
		// 获得资源中的图片作为要显示的图标
		Resources res = context.getResources();
		this.icons = new ArrayList<Bitmap>();
		// 为每个item分配一个icon
		for (int i = 0; i <iconRes.length; i++) {
			Bitmap icon = BitmapFactory.decodeResource(res, iconRes[i]);
			this.icons.add(icon);
		}
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return items.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	// 保存每项中的控件的引用
	class ViewHolder {
		TextView text;
		ImageView icon;
		LinearLayout layout;
	}

	@Override
	public View getView(int position, View convert, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		//第一次加载
		if (convert == null) {
			// 调用LayoutInflater的inflate方法加载layout文件夹中的界面
			convert =inflater.inflate(R.layout.list_item,null);
			holder = new ViewHolder();
			holder.text = (TextView) convert.findViewById(R.id.text);
			holder.icon = (ImageView) convert.findViewById(R.id.icon);
			holder.layout = (LinearLayout) convert.findViewById(R.id.layout);
			// 保存包含当前项控件的对象
			convert.setTag(holder);
			System.out.println("getView-----convert == null");
		} else {
			// 获取包含当前项控件的对象
			holder = (ViewHolder) convert.getTag();
			System.out.println("getView-----convert != null");
		}
		System.out.println("getView-----position = " + position);
		// 设置当前项的图标和内容
		holder.icon.setImageBitmap(icons.get(position));
		holder.text.setText(items.get(position));
		// 设置选中效果
		if (selectedPosition == position) {
			holder.text.setTextColor(Color.BLUE);
			holder.layout.setBackgroundColor(Color.YELLOW);
		} else {
			holder.text.setTextColor(Color.GRAY);
			holder.layout.setBackgroundColor(Color.TRANSPARENT);
		}
		return convert;
	}

}
