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

	// ����һ�����ֱã���������������findViewById(),��ͬ����LayoutInflater��������layout��xml�����ļ�������ʵ����
	private LayoutInflater inflater = null;
	private ArrayList<String> items = null;
	private ArrayList<Bitmap> icons = null;
	//��������ѡ�е�item
	private int selectedPosition = -1;

	public void setSelectedPosition(int position) {
		selectedPosition = position;
	}

	/**
	 * ���캯��
	 * @param context
	 * @param arraylist  ��ʾ���ı�
	 * @param iconRes    ��ʾ��ͼ��
	 */
	public ListviewAdapter(Context context, ArrayList<String> arraylist,
			int[] iconRes) {
		// TODO Auto-generated constructor stub
		// LayoutInflater�������ؽ���
		inflater = LayoutInflater.from(context);
		// �����������е�ÿ���������Ϣ
		this.items = arraylist;
		// �����Դ�е�ͼƬ��ΪҪ��ʾ��ͼ��
		Resources res = context.getResources();
		this.icons = new ArrayList<Bitmap>();
		// Ϊÿ��item����һ��icon
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

	// ����ÿ���еĿؼ�������
	class ViewHolder {
		TextView text;
		ImageView icon;
		LinearLayout layout;
	}

	@Override
	public View getView(int position, View convert, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		//��һ�μ���
		if (convert == null) {
			// ����LayoutInflater��inflate��������layout�ļ����еĽ���
			convert =inflater.inflate(R.layout.list_item,null);
			holder = new ViewHolder();
			holder.text = (TextView) convert.findViewById(R.id.text);
			holder.icon = (ImageView) convert.findViewById(R.id.icon);
			holder.layout = (LinearLayout) convert.findViewById(R.id.layout);
			// ���������ǰ��ؼ��Ķ���
			convert.setTag(holder);
			System.out.println("getView-----convert == null");
		} else {
			// ��ȡ������ǰ��ؼ��Ķ���
			holder = (ViewHolder) convert.getTag();
			System.out.println("getView-----convert != null");
		}
		System.out.println("getView-----position = " + position);
		// ���õ�ǰ���ͼ�������
		holder.icon.setImageBitmap(icons.get(position));
		holder.text.setText(items.get(position));
		// ����ѡ��Ч��
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
