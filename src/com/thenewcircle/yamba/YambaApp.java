package com.thenewcircle.yamba;

import com.marakana.android.yamba.clientlib.YambaClient;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.util.Log;

public class YambaApp extends Application implements OnSharedPreferenceChangeListener {
	private static final String TAG = "Yamba." + YambaApp.class.getSimpleName();
	private YambaClient client;
	private SharedPreferences prefs;
	private PendingIntent timelinePending;

	@Override
	public void onCreate() {
		super.onCreate();
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		prefs.registerOnSharedPreferenceChangeListener(this);
		Intent timelineIntent = new Intent(this, TimelineService.class);
		timelinePending = PendingIntent.getService(this, 0, timelineIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		setupTimer();
	}

	private void setupTimer() {
		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		long interval = getRefreshInterval();
		Log.d(TAG, "setupTimer() " + interval);
		if(interval != 0) {
			alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 2000, 
					interval, timelinePending);
		}
		else {
			alarmManager.cancel(timelinePending);
		}
	}
	
	private long getRefreshInterval() {
		String intervalString = prefs.getString("interval", AlarmManager.INTERVAL_FIFTEEN_MINUTES + "");
		long interval = Long.valueOf(intervalString);
		return interval;
	}

	public YambaClient getYambaClient() {
		if(client == null) {
			String username = prefs.getString("username", null);
			String password = prefs.getString("password", null);
			Log.d(TAG, " username = " + username + " password = " + password);
			
			// Check for valid login
			if(username != null && password != null && username.length() > 0 && password.length() > 0) {
				
				client = new YambaClient(username, password);
			}
		}
		
		return client;
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		Log.d(TAG, "onSharedPreferenceChanged " + key);
		if(key.equals("username") || key.equals("password")) {
			client = null;
		}
		if(key.equals("interval")){
			setupTimer();
		}
	}

}
