package com.thenewcircle.yamba;

import android.app.Activity;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class StatusActivity extends Activity implements LocationListener {

	protected static final String TAG = "Yamba."
			+ StatusActivity.class.getSimpleName();
	private EditText status;
	private Location currentLocation = null;
	private LocationManager locationManager;
	private Criteria criteria;
	private String provider;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_status);
		status = (EditText) findViewById(R.id.status);
		final TextView count = (TextView) findViewById(R.id.count);

		status.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				String statusText = s.toString();
				int size = statusText.length();
				int remaining = 140 - size;
				count.setText("" + remaining);

				if (remaining < 10) {
					count.setTextColor(getResources().getColor(R.color.warning));
				} else {
					count.setTextColor(getResources().getColor(R.color.normal));
				}

			}
		});

	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG, "onResume()");
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		
		criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_COARSE);
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		provider = locationManager.getBestProvider(criteria, true);
		Log.d(TAG, "Location Provider: " + provider);
		
		if(provider != null) {
			locationManager.requestLocationUpdates(provider, 2000, 2, this);
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		locationManager.removeUpdates(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.status, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_settings:
			Intent settingsIntent = new Intent(this, SettingsActivity.class);
			startActivity(settingsIntent);
			return true;
		case R.id.post:
			Log.d(TAG, "post");
			// Post Intent
			Toast.makeText(StatusActivity.this, status.getText().toString(),
					Toast.LENGTH_LONG).show();

			Intent postIntent = new Intent(StatusActivity.this,
					PostService.class);
			postIntent.putExtra("status", status.getText().toString());
			if(currentLocation == null && provider != null) {
				currentLocation = locationManager.getLastKnownLocation(provider);
			}
			if(currentLocation != null) {
				postIntent.putExtra("lat", currentLocation.getLatitude());
				postIntent.putExtra("lon", currentLocation.getLongitude());
			}
			startService(postIntent);
			status.getText().clear();
			return true;
		case R.id.refresh:
			Log.d(TAG, "refresh");
			Intent refreshIntent = new Intent(this, TimelineService.class);
			startService(refreshIntent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onLocationChanged(Location location) {
		currentLocation  = location;
		Log.d(TAG, "onLocationChanged lat " + location.getLatitude() + " lon=" + location.getLongitude());
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

}
