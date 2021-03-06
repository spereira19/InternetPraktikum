package com.javacodegeeks.androidaccelerometerexample;

import java.io.IOException;
import java.util.ArrayList;







import com.androidclient.pojo.AccelerometerData;
import com.androidclient.util.MqttConnection;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class AndroidAccelerometerExample extends Activity implements SensorEventListener {

	private float lastX, lastY, lastZ;
	private static String logtag = "TwoButtonApp";//for use as the tag when logging 
	private SensorManager sensorManager;
	private ArrayList<AccelerometerData> gObjArraylistOfSensorData;
	private Sensor accelerometer;
	private TelephonyManager gObjTelephonyManager;
	private String gStrDeviceId;
	private float deltaXMax = 0;
	private float deltaYMax = 0;
	private float deltaZMax = 0;

	private float deltaX = 0;
	private float deltaY = 0;
	private float deltaZ = 0;

	private float vibrateThreshold = 0;

	private TextView currentX, currentY, currentZ, maxX, maxY, maxZ;

//	public Vibrator vibrator;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initializeViews();
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		gObjTelephonyManager = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
		gStrDeviceId = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
		
		if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
 			// success! we have an accelerometer
 			accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
 			vibrateThreshold = accelerometer.getMaximumRange() / 2;
 		} else {
 			// fai! we dont have an accelerometer!
 		}
	//	vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
		
		
		OnClickListener startListener = new OnClickListener() {
	        public void onClick(View v) {
	         Log.d(logtag,"onClick() called - start button"); 
	        // Toast.makeText(AndroidAccelerometerExample.this, sensorManager.toString() , Toast.LENGTH_LONG).show();
	         gObjArraylistOfSensorData = new ArrayList();
	         sensorManager.registerListener(AndroidAccelerometerExample.this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
	         Log.d(logtag,"onClick() ended - start button");
	        } 
	    };
		
		
		OnClickListener stopListener = new OnClickListener() {
	        public void onClick(View v) {
	         Log.d(logtag,"onClick() called - stop button"); 
	         Toast.makeText(AndroidAccelerometerExample.this, "The Stop button was clicked.", Toast.LENGTH_LONG).show();
	         try {
				MqttConnection.createConnection(gObjTelephonyManager,gObjArraylistOfSensorData,gStrDeviceId);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	         sensorManager.unregisterListener(AndroidAccelerometerExample.this);
	         Log.d(logtag,"onClick() ended - stop button");
	        } 
	    };
		
		
		 Button buttonStart = (Button)findViewById(R.id.buttonStart);
		 buttonStart.setOnClickListener(startListener); // Register the onClick listener with the implementation above
			
		Button buttonStop = (Button)findViewById(R.id.buttonStop);
		buttonStop.setOnClickListener(stopListener); // Register the onClick listener
		

	}

	public void initializeViews() {
		currentX = (TextView) findViewById(R.id.currentX);
		currentY = (TextView) findViewById(R.id.currentY);
		currentZ = (TextView) findViewById(R.id.currentZ);

		maxX = (TextView) findViewById(R.id.maxX);
		maxY = (TextView) findViewById(R.id.maxY);
		maxZ = (TextView) findViewById(R.id.maxZ);
	}

	/*
	//onResume() register the accelerometer for listening the events
	protected void onResume() {
		super.onResume();
		sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
	}

	//onPause() unregister the accelerometer for stop listening the events
	protected void onPause() {
		super.onPause();
		sensorManager.unregisterListener(this);
	}
*/
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {

		// clean current values
		displayCleanValues();
		// display the current x,y,z accelerometer values
		displayCurrentValues();
		// display the max x,y,z accelerometer values
		displayMaxValues();

		// get the change of the x,y,z values of the accelerometer
		deltaX = Math.abs(lastX - event.values[0]);
		deltaY = Math.abs(lastY - event.values[1]);
		deltaZ = Math.abs(lastZ - event.values[2]);

		// if the change is below 2, it is just plain noise
		if (deltaX < 2)
			deltaX = 0;
		if (deltaY < 2)
			deltaY = 0;
		if (deltaZ < 2)
			deltaZ = 0;

		// set the last know values of x,y,z
		lastX = event.values[0];
		lastY = event.values[1];
		lastZ = event.values[2];
		
		long timestamp = System.currentTimeMillis();
		AccelerometerData data = new AccelerometerData(timestamp, lastX, lastY, lastZ);
        gObjArraylistOfSensorData.add(data);
		

	//	vibrate();

	}

	// if the change in the accelerometer value is big enough, then vibrate!
	// our threshold is MaxValue/2
//	public void vibrate() {
//		if ((deltaX > vibrateThreshold) || (deltaY > vibrateThreshold) || (deltaZ > vibrateThreshold)) {
//			vibrator.vibrate(50);
//		}
//	}

	public void displayCleanValues() {
		currentX.setText("0.0");
		currentY.setText("0.0");
		currentZ.setText("0.0");
	}

	// display the current x,y,z accelerometer values
	public void displayCurrentValues() {
		currentX.setText(Float.toString(deltaX));
		currentY.setText(Float.toString(deltaY));
		currentZ.setText(Float.toString(deltaZ));
	}

	// display the max x,y,z accelerometer values
	public void displayMaxValues() {
		if (deltaX > deltaXMax) {
			deltaXMax = deltaX;
			maxX.setText(Float.toString(deltaXMax));
		}
		if (deltaY > deltaYMax) {
			deltaYMax = deltaY;
			maxY.setText(Float.toString(deltaYMax));
		}
		if (deltaZ > deltaZMax) {
			deltaZMax = deltaZ;
			maxZ.setText(Float.toString(deltaZMax));
		}
	}
}