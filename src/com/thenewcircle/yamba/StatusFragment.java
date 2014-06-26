package com.thenewcircle.yamba;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class StatusFragment extends Fragment implements LocationListener {
	protected static final String TAG = "Yamba."
			+ StatusFragment.class.getSimpleName();
	private EditText status;
	private Location currentLocation = null;
	private LocationManager locationManager;
	private Criteria criteria;
	private String provider;

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View layout = inflater.inflate(R.layout.fragment_status, container, false);
		status = (EditText) layout.findViewById(R.id.status);
		final TextView count = (TextView) layout.findViewById(R.id.count);

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
		return layout;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG, "onResume()");
		locationManager = (LocationManager) 
				getActivity().getSystemService(Context.LOCATION_SERVICE);
		
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
	public void onPause() {
		super.onPause();
		LocationManager locationManager = (LocationManager) 
				getActivity().getSystemService(Context.LOCATION_SERVICE);
		locationManager.removeUpdates(this);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.status, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.post:
			Log.d(TAG, "post");
			// Post Intent
			Toast.makeText(getActivity(), status.getText().toString(),
					Toast.LENGTH_LONG).show();

			Intent postIntent = new Intent(getActivity(),
					PostService.class);
			postIntent.putExtra("status", status.getText().toString());
			if(currentLocation == null && provider != null) {
				currentLocation = locationManager.getLastKnownLocation(provider);
			}
			if(currentLocation != null) {
				postIntent.putExtra("lat", currentLocation.getLatitude());
				postIntent.putExtra("lon", currentLocation.getLongitude());
			}
			getActivity().startService(postIntent);
			status.getText().clear();
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
