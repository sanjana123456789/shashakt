package com.cetpa.shashakt;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.audiofx.BassBoost.Settings;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class SplashActivity extends Activity implements SensorEventListener {
	

	SensorManager sm;
	Sensor s;
	private long lastUpdate = 0;
	private float last_x, last_y, last_z;
	private static final int SHAKE_THRESHOLD = 1500;
	static String a;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
       /* String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if(!provider.contains("gps")){ //if gps is disabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider"); 
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3")); 
            sendBroadcast(poke);
        } */
       sm=(SensorManager) getSystemService(Context.SENSOR_SERVICE);
       s=sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
       sm.registerListener(this, s, SensorManager.SENSOR_DELAY_NORMAL);
       Intent i=getIntent();
       SplashActivity.a=i.getStringExtra("isCheck");
       
    }
	@Override
	public void onSensorChanged(SensorEvent sensorEvent) {
		Sensor mySensor = sensorEvent.sensor;
		 
	    if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
	        float x = sensorEvent.values[0];
	        float y = sensorEvent.values[1];
	        float z = sensorEvent.values[2];
	 
	        long curTime = System.currentTimeMillis();
	 
	        if ((curTime - lastUpdate) > 100) {
	            long diffTime = (curTime - lastUpdate);
	            lastUpdate = curTime;
	 
	            float speed = Math.abs(x + y + z - last_x - last_y - last_z)/ diffTime * 10000;
	 
	            if (speed > SHAKE_THRESHOLD) {
	            	
	            	//Toast.makeText(SplashActivity.this, "yuppppppp", Toast.LENGTH_SHORT).show();
	            	Intent intent=new Intent(SplashActivity.this,MainActivity.class);
	            	intent.putExtra("is", SplashActivity.a);
	            	startActivity(intent);
	            	finish();
	 
	            }
	 
	            last_x = x;
	            last_y = y;
	            last_z = z;
	        }
	    }

		
	}
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) 
	{
		// TODO Auto-generated method stub
		
	}
	@Override
	protected void onPause() 
	{
		super.onPause();
		sm.unregisterListener(this);
	}
	@Override
	protected void onResume()
	{
	  super.onResume();
	  sm.registerListener(this, s, SensorManager.SENSOR_DELAY_NORMAL);
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
		case R.id.item1:
			//Toast.makeText(this, "select the munber", Toast.LENGTH_SHORT).show();
			Intent intent=new Intent(this,NextActivity.class);
			startActivity(intent);
			finish();
			break;

		default:
			break;
		}
		return true;
		
	}



}
