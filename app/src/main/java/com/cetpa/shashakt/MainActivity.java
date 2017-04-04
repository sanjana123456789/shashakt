package com.cetpa.shashakt;


import java.io.IOException;
import java.util.List;
import java.util.Locale;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;

public class MainActivity extends Activity implements ConnectionCallbacks,
OnConnectionFailedListener, LocationListener
{
	static double latitude,longitude;
	String loc;
	String msg;
	private static final String TAG = MainActivity.class.getSimpleName();

	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
	private static final String DEFAULT = null;
	private static final int REQUEST_LOCATION=101;

	private Location mLastLocation;

	// Google client to interact with Google API
	private GoogleApiClient mGoogleApiClient;

	// boolean flag to toggle periodic location updates
	private boolean mRequestingLocationUpdates = false;

	private LocationRequest mLocationRequest;

	// Location updates intervals in sec
	private static int UPDATE_INTERVAL = 10000; // 10 sec
	private static int FATEST_INTERVAL = 5000; // 5 sec
	private static int DISPLACEMENT = 10; // 10 meters
    static String b;
	// UI elements
	private TextView lblLocation;
	private Button btnShowLocation, btnStartLocationUpdates;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
         Intent i=getIntent();
         MainActivity.b=i.getStringExtra("is");
		lblLocation = (TextView) findViewById(R.id.lblLocation);
		btnShowLocation = (Button) findViewById(R.id.btnShowLocation);
		btnStartLocationUpdates = (Button) findViewById(R.id.btnLocationUpdates);

		// First we need to check availability of play services
		if (checkPlayServices()) {

			// Building the GoogleApi client
			buildGoogleApiClient();


			createLocationRequest();
		}

		// Show location button click listener
		btnShowLocation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				displayLocation();
			}
		});

		// Toggling the periodic location updates
		btnStartLocationUpdates.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				togglePeriodicLocationUpdates();
			}
		});

	}
	

	
	@Override
	protected void onStart() {
		super.onStart();
		if (mGoogleApiClient != null) {
			mGoogleApiClient.connect();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		checkPlayServices();

		// Resuming the periodic location updates
		if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
			startLocationUpdates();
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (mGoogleApiClient.isConnected()) {
			mGoogleApiClient.disconnect();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		stopLocationUpdates();
	}

	/**
	 * Method to display the location on UI
	 * */
	private void displayLocation() {

		mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
				!= PackageManager.PERMISSION_GRANTED) {
			// Check Permissions Now
			ActivityCompat.requestPermissions(this,
					new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
					REQUEST_LOCATION);
		} else {
			// permission has been granted, continue as usual
			Location myLocation =
					LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
		}

		if (mLastLocation != null) {
			MainActivity.latitude = mLastLocation.getLatitude();
			MainActivity.longitude = mLastLocation.getLongitude();

			lblLocation.setText(MainActivity.latitude + ", " + MainActivity.longitude);
			GetCurrentAddress currentadd=new GetCurrentAddress();
			 currentadd.execute();
			

		} else {

			lblLocation
					.setText("(Couldn't get the location. Make sure location is enabled on the device)");
		}

		}



		/**
	 * Method to toggle periodic location updates
	 * */
	private void togglePeriodicLocationUpdates() {
		if (!mRequestingLocationUpdates) {
			// Changing the button text
			btnStartLocationUpdates
					.setText(getString(R.string.btn_stop_location_updates));

			mRequestingLocationUpdates = true;

			// Starting the location updates
			startLocationUpdates();

			Log.d(TAG, "Periodic location updates started!");

		} else {
			// Changing the button text
			btnStartLocationUpdates
					.setText(getString(R.string.btn_start_location_updates));

			mRequestingLocationUpdates = false;

			// Stopping the location updates
			stopLocationUpdates();

			Log.d(TAG, "Periodic location updates stopped!");
		}
	}

	/**
	 * Creating google api client object
	 * */
	protected synchronized void buildGoogleApiClient() {
		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(LocationServices.API).build();
	}

	/**
	 * Creating location request object
	 * */
	protected void createLocationRequest() {
		mLocationRequest = new LocationRequest();
		mLocationRequest.setInterval(UPDATE_INTERVAL);
		mLocationRequest.setFastestInterval(FATEST_INTERVAL);
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
	}

	/**
	 * Method to verify google play services on the device
	 * */
	private boolean checkPlayServices() {
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode, this,
						PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else {
				Toast.makeText(getApplicationContext(),
						"This device is not supported.", Toast.LENGTH_LONG)
						.show();
				finish();
			}
			return false;
		}
		return true;
	}

	/**
	 * Starting the location updates
	 * */
	protected void startLocationUpdates() {

		LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
				!= PackageManager.PERMISSION_GRANTED) {
			// Check Permissions Now
			ActivityCompat.requestPermissions(this,
					new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
					REQUEST_LOCATION);
		} else {
			// permission has been granted, continue as usual
			Location myLocation =
					LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
		}

	}

	/**
	 * Stopping location updates
	 */
	protected void stopLocationUpdates() {
		LocationServices.FusedLocationApi.removeLocationUpdates(
				mGoogleApiClient, this);
	}

	/**
	 * Google api callback methods
	 */
	@Override
	public void onConnectionFailed(ConnectionResult result) {
		Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
				+ result.getErrorCode());
	}

	@Override
	public void onConnected(Bundle arg0) {

		// Once connected with google api, get the location
		displayLocation();

		if (mRequestingLocationUpdates) {
			startLocationUpdates();
		}
	}

	@Override
	public void onConnectionSuspended(int arg0) {
		mGoogleApiClient.connect();
	}

	@Override
	public void onLocationChanged(Location location) {
		// Assign the new location
		mLastLocation = location;

		Toast.makeText(getApplicationContext(), "Location changed!",
				Toast.LENGTH_SHORT).show();

		// Displaying the new location on UI
		displayLocation();
	}

	 private class GetCurrentAddress extends AsyncTask<String,String ,String >
	 {
	 
	  @Override
	  protected String doInBackground(String... urls)
	  {
	 // this lat and log we can get from current location but here we given hard coded
	   
	  // Log.d("LOCATION", String.valueOf(latitude)); 
	  String address= getAddress(MainActivity.this, MainActivity.latitude, MainActivity.longitude);
	   return address;
	  }
	  @Override
	  protected void onPostExecute(String resultString) {
	   //dialog.dismiss();
	    
		  if(MainActivity.b.equals("yesS"))
			 {
				 sendSms(resultString); 
			 }
		  
		  if(MainActivity.b.equals("yesW"))
			 {
				 sendMessage(resultString); 
			 }
		  if(MainActivity.b.equals("yesB"))
			 {
				sendSms(resultString);
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			    sendMessage(resultString);
			 }	
	 // Toast.makeText(MainActivity.this,"Yes we got it ="+resultString, Toast.LENGTH_SHORT).show();
	  }
	 }
 void sendMessage(String s)
	 {
	StringBuffer buffer=new StringBuffer("Hey ,Please save me ... I am in trouble at this Location- ");
	buffer.append(s);
	 
	 //Toast.makeText(MainActivity.this,"Yes we got it ="+s, Toast.LENGTH_SHORT).show();

	//Toast.makeText(this, buffer.toString(), Toast.LENGTH_SHORT).show();
		Intent intent=new Intent(Intent.ACTION_SEND);
		intent.putExtra(Intent.EXTRA_TEXT,buffer.toString());
		//intent.putExtra("chat", true);
		//intent.putExtra(Intent.EXTRA_PHONE_NUMBER, num);
		intent.setType("text/plain");
		intent.setPackage("com.whatsapp");
		//intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	
		MainActivity.this.startActivity(Intent.createChooser(intent,"Send..."));
		
	 }
 void sendSms(String sm)
	{
	    StringBuffer buffer=new StringBuffer("Hey ,Please save me ... I am in trouble at this Location- ");
		buffer.append(sm);
		SharedPreferences sp=getSharedPreferences("MyCred",MODE_PRIVATE);
		String  num1=sp.getString("k1", DEFAULT);
		String  num2=sp.getString("k2", DEFAULT);
		
		
		if(num1.equals(DEFAULT)&&num2.equals(DEFAULT))
		{
			
			Toast.makeText(this, "no such data is found", Toast.LENGTH_SHORT).show();
		}
		else
		{
			Intent intent=new Intent(this,SplashActivity.class);
			PendingIntent pi=PendingIntent.getActivity(getApplicationContext(), 0, intent,0);
			
			SmsManager sms=SmsManager.getDefault();
			sms.sendTextMessage(num1, null,buffer.toString(), pi,null);
			sms.sendTextMessage(num2, null,buffer.toString(), pi,null);
			Toast.makeText(this, "SMS has sent", Toast.LENGTH_SHORT).show();
			
		}

	}

	 
	 public  String getAddress(Context ctx, double latitude, double longitude)
	 {
	        StringBuilder result = new StringBuilder();
	        try {
	            Geocoder geocoder = new Geocoder(ctx, Locale.getDefault());
	            List<Address>
	 addresses = geocoder.getFromLocation(latitude, longitude, 1);
	            if (addresses.size() > 0) 
	            {
	                Address address = addresses.get(0);
	     
	    String locality=address.getLocality();
	    String country=address.getCountryName();
	    String addLine0=address.getAddressLine(0);
	    String addLine1=address.getAddressLine(1);
	    String addLine2=address.getAddressLine(2);

	    Log.d("LOCATION",locality);
	                result.append(addLine0+" "+addLine1+" "+addLine2+" "+country);
	               // result.append(city+" "+ region_code+" ");
	  //  result.append(zipcode);
	    Log.d("LOCATION", result.toString());
	     
	            }
	            else
	            {
	            	 Log.d("LOCATION", "Else vala");	
	            }
	        } catch (IOException e) {
	            Log.e("tag", e.getMessage());
	        }
	 
	        return result.toString();
	    }
public void help(View v)
{
	Intent i=new Intent(this,Main2Activity.class);
	startActivity(i);
}

	}
	 