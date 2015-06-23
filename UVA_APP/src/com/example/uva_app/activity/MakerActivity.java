package com.example.uva_app.activity;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.uva_app.R;
import com.example.uva_app.utils.HttpRequestType;
import com.example.uva_app.utils.HttpSender;
import com.example.uva_app.utils.HttpStatusCode;
import com.example.uva_app.utils.HttpUrl;
import com.example.uva_app.utils.IResponse;
import com.example.uva_app.utils.Maker;
import com.freddyyao.uva_app.MainActivity;

public class MakerActivity extends Activity implements OnClickListener {

	private Button btn_photo;
	private Button btn_marker;
	private EditText txt_info;
	private ImageView new_photo;
	private boolean flag = false; // 文本
	private boolean flag1 = false;// 图片

	/* 用来标识请求照相功能的activity */
	private static final int CAMERA_WITH_DATA = 3023;
	private String path;

	/*
	 * william modify
	 */
	private String picPath = null;
	/* 用来标识请求gallery的activity */
	private static final int UPLOAD_IMAGE = 1;
	/* 拍照的照片存储位置 */
	public static final File PHOTO_DIR = new File(Environment.getExternalStorageDirectory() + "/DCIM/Camera");
	
	/*
	 * william
	 */
	private String username;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.maker);
		/*
		 * william
		 */
		Intent intent = getIntent();
		this.username = intent.getStringExtra("userName");
		btn_photo = (Button) findViewById(R.id.btn_photo);	
		btn_marker=(Button)findViewById(R.id.btn_maker);
		txt_info = (EditText) findViewById(R.id.txt_info);
		new_photo = (ImageView) findViewById(R.id.new_photo);
		setTitle("添加标记");

		btn_photo.setOnClickListener(this);
		btn_marker.setOnClickListener(this);
		txt_info.addTextChangedListener(watcher);
	}

	TextWatcher watcher = new TextWatcher() {
		private CharSequence temp;
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			temp = s;
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			// TODO Auto-generated method stub
		}

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			if (temp.length() > 140) {
				Toast.makeText(MakerActivity.this, "输入文本大于140，请精简！", Toast.LENGTH_LONG).show();
			}
			if (temp.length() > 1) {
				flag = true;
			} else {
				flag = false;
			}
			if (flag) {
				btn_marker.setVisibility(btn_marker.VISIBLE);
				flag = false;
			} else {
				btn_marker.setVisibility(btn_marker.INVISIBLE);
				flag = true;
			}
		}
	};

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_photo:
			doTakePhoto();
			
			break;
		case R.id.btn_maker:
			 /*
		     * william
		     */
		    Maker maker = new Maker();
		    maker.username = username;
		    // test data
		    maker.longitude = 1.23d;
		    maker.latitude = 2.34d;
		    maker.road_id = 1;
		    maker.text = txt_info.getText().toString();
		    maker.imageUrl = this.picPath;
		    if (maker.imageUrl != null)
		    {
    		    try {
                    HttpSender.post(HttpUrl.UPLOAD_IMAGE, maker.imageUrl, new MakerResponse(maker), HttpRequestType.POST_FILE);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
		    }
		    else 
		    {
		        JSONObject respContent = new JSONObject();
		        try {
                    respContent.put("status", HttpStatusCode.HTTP_CONTINUE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
		        new MakerResponse(maker).onResponse(respContent.toString());
		    }
			Intent intent = new Intent(MakerActivity.this, MainActivity.class);
			startActivity(intent);
			intent.putExtra("text",maker.text);
			intent.putExtras(intent);
			startActivity(intent);
			finish();
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK)
			return;
		switch (requestCode) {
		case UPLOAD_IMAGE:
			if (data != null) {
				Bitmap bitmap = data.getParcelableExtra("data");
				new_photo.setImageBitmap(bitmap);
				Uri url;
				try {
					url = Uri.parse(saveBitmapToFile(bitmap));
					picPath = url.toString();
					// uploadImage();最后的保存地址都获取到了，可以写上传
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			break;
		case CAMERA_WITH_DATA: // 照相机程序返回的,再次调用图片剪辑程序去修剪图片
			doCropPhoto(data);
			break;
		}
	}

	/**
	 * 修剪图片
	 * @param data
	 */
	private void doCropPhoto(Intent data) {
		Uri currImageURI = null;
		if (data != null) {
			if (data.getExtras() != null) {
				File file = getFile(getBitmap(data));
				// File mCurrentPhotoFile = new File(PHOTO_DIR,
				// getPhotoFileName());//
				// 给新照的照片文件命名
				currImageURI = Uri.fromFile(file);
			} else {
				currImageURI = data.getData();
			}
		} else {
			currImageURI = Uri.fromFile(new File(path));
		}
		try {
			// 启动gallery去剪辑这个照片
			final Intent intent = getCropImageIntent(currImageURI);
			startActivityForResult(intent, UPLOAD_IMAGE);// 上传
			flag1 = true;
			if (flag1) {
				btn_marker.setVisibility(btn_marker.VISIBLE);
				flag1 = false;
			} else {
				btn_marker.setVisibility(btn_marker.INVISIBLE);
				flag1 = true;
			}
			
		} catch (Exception e) {
			Toast.makeText(this, "获取照片错误", Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * 拍照获取图片
	 */
	private void doTakePhoto() {
		try {
			path = Environment.getExternalStorageDirectory() + "/+" + "maker" + ".jpg";
			final Intent intent = getTakePickIntent(MediaStore.ACTION_IMAGE_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(path)));
			startActivityForResult(intent, CAMERA_WITH_DATA);
			
		} catch (ActivityNotFoundException e) {
			Toast.makeText(this, "获取照片错误", Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * 
	 * @param action
	 * @return
	 */
	private static Intent getTakePickIntent(String action) {
		Intent intent = new Intent();
		intent.putExtra("return-data", true);
		intent.setAction(action);
		return intent;
	}

	/**
	 * Constructs an intent for image cropping. 调用图片剪辑程序
	 */
	private static Intent getCropImageIntent(Uri photoUri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(photoUri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 200);
		intent.putExtra("outputY", 200);
		intent.putExtra("return-data", true);
		return intent;
	}

	/**
	 * 
	 * @param data
	 * @return
	 */
	private Bitmap getBitmap(Intent data) {
		Bundle bundle = data.getExtras();
		Bitmap bitmap = (Bitmap) bundle.get("data");// 获取相机返回的数据，并转换为Bitmap图片格式
		return bitmap;
	}

	/**
	 * 
	 * @param bitmap
	 * @return
	 */
	private File getFile(Bitmap bitmap) {

		String sdStatus = Environment.getExternalStorageState();
		if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用

			return null;
		}
		String name = new DateFormat().format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA)) + ".jpg";

		FileOutputStream b = null;
		if (!PHOTO_DIR.isDirectory()) {
			PHOTO_DIR.mkdirs();// 创建文件夹
		}
		File fileName = new File(PHOTO_DIR, name);

		try {
			b = new FileOutputStream(fileName);
			/*
			 * 压缩函数的三个参数含义分别是，压缩后的图像的格式（png），图像显示的质量（0—100），100表示最高质量，图像处理的输出流（out
			 * ）
			 */
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, b);// 把数据写入文件
			flag1 = true;
			if (flag1) {
				btn_marker.setVisibility(btn_marker.VISIBLE);
				flag1 = false;
			} else {
				btn_marker.setVisibility(btn_marker.INVISIBLE);
				flag1 = true;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				b.flush();
				b.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return fileName;
	}

	/**
	 * Save Bitmap to a file.保存图片到SD卡。
	 * 
	 * @param bitmap
	 * @param file
	 * @return error message if the saving is failed. null if the saving is
	 *         successful.
	 * @throws IOException
	 */
	public static String saveBitmapToFile(Bitmap bitmap) throws IOException {
		BufferedOutputStream os = null;
		String _file = getFilePath().trim() + "/aaa.png";
		try {
			File file = new File(_file);
			// String _filePath_file.replace(File.separatorChar +
			// file.getName(), "");
			int end = _file.lastIndexOf(File.separator);
			String _filePath = _file.substring(0, end);
			File filePath = new File(_filePath);
			if (!filePath.exists()) {
				filePath.mkdirs();
			}
			file.createNewFile();
			os = new BufferedOutputStream(new FileOutputStream(file));
			/*
			 * 压缩函数的三个参数含义分别是，压缩后的图像的格式（png），图像显示的质量（0—100），100表示最高质量，图像处理的输出流（out
			 * ）
			 */
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					e.fillInStackTrace();
				}
			}
		}
		return _file;
	}

	public static String getFilePath() {
		File path = Environment.getExternalStorageDirectory();
		return path.toString();
	}
	
	/*
	 * william
	 */
	class MakerResponse implements IResponse
	{
	    Maker maker = null;
	    public MakerResponse(Maker maker)
	    {
	        this.maker = maker;
	    }
        @Override
        public void onResponse(Object respContent) {
            try {
                JSONObject resp= new JSONObject((String) respContent);
                int status = resp.getInt("status");
                try {
                    maker.imageUrl = resp.getString("url");
                } catch (Exception e)
                {
                    maker.imageUrl = "";
                }
                if (status == HttpStatusCode.HTTP_OK)
                {
                    
                }
                else if (status == HttpStatusCode.HTTP_CONTINUE)
                {   
                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("username", maker.username));
                    params.add(new BasicNameValuePair("text", maker.text));
                    params.add(new BasicNameValuePair("url", maker.imageUrl));
                    params.add(new BasicNameValuePair("longitude", maker.longitude.toString()));
                    params.add(new BasicNameValuePair("latitude", maker.latitude.toString()));
                    params.add(new BasicNameValuePair("road_id", maker.road_id.toString()));
    
                    try {
                        HttpSender.post(HttpUrl.UPLOAD_MAKER, params, new MakerResponse(null), HttpRequestType.POST_DATA);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }
                else if (status == HttpStatusCode.HTTP_ERROR)
                {
                    
                }
            } catch (JSONException e1) {
                e1.printStackTrace();
            } catch (NullPointerException e2) {
                e2.printStackTrace();
            }
        }
	}
}
