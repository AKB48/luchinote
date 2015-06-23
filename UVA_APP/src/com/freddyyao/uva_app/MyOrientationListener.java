package com.freddyyao.uva_app;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class MyOrientationListener implements SensorEventListener
{

	private Context context;
	private SensorManager sensorManager;
	private Sensor asensor;
	private Sensor msensor;
	float[] accelerometerValues = new float[3];  
    float[] magneticFieldValues = new float[3]; 

	
	private float lastX ; 
	
	private OnOrientationListener onOrientationListener ; 

	public MyOrientationListener(Context context)
	{
		this.context = context;
	}

	// 开始
	public void start()
	{
		// 获得传感器管理器
		sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		if (sensorManager != null)
		{
			// 获得方向传感器
			asensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			msensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		}
		// 注册
		if (asensor != null)
		{
			sensorManager.registerListener(this, asensor,Sensor.TYPE_ACCELEROMETER);
		}
		if (msensor != null)
		{//SensorManager.SENSOR_DELAY_UI
			sensorManager.registerListener(this, msensor,Sensor.TYPE_MAGNETIC_FIELD);
		}
	}

	// 停止检测
	public void stop()
	{
		sensorManager.unregisterListener(this);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy)
	{
		
	}

	private String TAG="TAG";
	@Override
	public void onSensorChanged(SensorEvent event)
	{
		// 接受方向感应器的类型  
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)  
        {  
            // 这里我们可以得到数据，然后根据需要来处理  
            float x = event.values[0];  
            if( Math.abs(x- lastX) > 1.0 )
            {
            	//onOrientationListener.onOrientationChanged(x);
            }
//            Log.e("DATA_X", x+"");
            //lastX = x ; 
            accelerometerValues=event.values;
            
        }  
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
			magneticFieldValues=event.values;
			  float x = event.values[0];  
	            if( Math.abs(x- lastX) > 1.0 )
	            {
	              onOrientationListener.onOrientationChanged(x);
	            }
//	            Log.e("DATA_X", x+"");
	            lastX = x ; 
	            
	            if (x >= -5 && x < 5) {
	    		} else if (x >= 5 && x < 85) {
	    			 Log.i(TAG, "东北");
	    			
	    		} else if (x >= 85 && x <= 95) {
	    			 Log.i(TAG, "正东");
	    			
	    		} else if (x >= 95 && x< 175) {
	    			 Log.i(TAG, "东南");
	    			
	    		} else if ((x>= 175 &&x <= 180)
	    				|| (x) >= -180 && x < -175) {
	    			 Log.i(TAG, "正南");
	    		
	    		} else if (x >= -175 && x < -95) {
	    			 Log.i(TAG, "西南");
	    			
	    		} else if (x >= -95 && x < -85) {
	    			 Log.i(TAG, "正西");
	    			
	    		} else if (x >= -85 && x < -5) {
	    			 Log.i(TAG, "西北");
	    		}
		}
	}
	
	public void setOnOrientationListener(OnOrientationListener onOrientationListener)
	{
		this.onOrientationListener = onOrientationListener ;
	}
	
	
	public interface OnOrientationListener 
	{
		void onOrientationChanged(float x);
	}

}
