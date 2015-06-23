package com.example.uva_app.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.example.uva_app.R;

public class Info implements Serializable {
	private static final long serialVersionUID = -758459502806858414L;
	/**
	 * 精度
	 */
	private double latitude;
	/**
	 * 纬度
	 */
	private double longitude;
	/**
	 * 图片ID，真实项目中可能是图片路径
	 */
	 private int imgId;
	/**
	 * 商家名称
	 */
	private String name;
	/**
	 * 距离
	 */
	private String distance;
	/**
	 * 赞数量
	 */
	private int zan;

	public static int[] icon = { R.drawable.icon_map_location, R.drawable.icon_map_location,
			R.drawable.icon_map_location, R.drawable.icon_map_location};
	public static List<Info> infos = new ArrayList<Info>();

	static {
		infos.add(new Info(22.538017, 113.932148,icon[0], "英伦贵族小旅馆", "距离209米", 1456));
		infos.add(new Info(22.538017, 113.932471,icon[1],  "沙井国际洗浴会所", "距离897米", 456));
		infos.add(new Info(22.538017, 113.932947,icon[2],  "五环服装城", "距离249米", 1456));
		infos.add(new Info(22.538017, 113.933289,icon[3],  "老米家泡馍小炒", "距离679米", 1456));
	}

	public Info() {
	}

	public Info(double latitude, double longitude, int imgId, String name, String distance, int zan) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
		this.name = name;
		this.imgId=imgId;
		this.distance = distance;
		this.zan = zan;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDistance() {
		return distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}

	public int getImgId() {
		return imgId;
	}

	public void setImgId(int imgId) {
		this.imgId = imgId;
	}

	public int getZan() {
		return zan;
	}

	public void setZan(int zan) {
		this.zan = zan;
	}

}
