package com.thenewcircle.yamba;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import com.marakana.android.yamba.clientlib.YambaClient;
import com.marakana.android.yamba.clientlib.YambaClientException;

public class PostService extends IntentService {

	private static final String TAG = "Yamba." + PostService.class.getSimpleName();
	private static final int POST_SERVICE_NOTIFICATION_ID = 100;

	public PostService() {
		super(PostService.class.getName());
		Log.d(TAG, "PostService constructed");
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "onCreate()");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d(TAG, "got Intent" + intent);
		
		Notification.Builder notification = new Notification.Builder(this);
		notification.setSmallIcon(R.drawable.ic_launcher);
		notification.setContentTitle(getString(R.string.app_name));
		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		Intent settingsIntent = new Intent(this, SettingsActivity.class);
		PendingIntent settingsPending = PendingIntent.getActivity(this,  0, settingsIntent, 
				PendingIntent.FLAG_UPDATE_CURRENT);

		// Get app "singleton"
		YambaApp app = (YambaApp) getApplication();
		// get client from app
		YambaClient client = app.getYambaClient();
		if(client != null) {
			// get data from intent
			String status = intent.getStringExtra("status");
			try {
				// check for location data in intent
				if(intent.hasExtra("lat")) {
					double lat = intent.getDoubleExtra("lat", 0.0);
					double lon = intent.getDoubleExtra("lon", 0.0);  
					Log.d(TAG, "posting " + status + " lat " + lat + " lon " + lon);
					client.postStatus(status, lat, lon);
				}
				else {
					Log.d(TAG, "posting " + status);
					client.postStatus(status);
				}
				// post notification of successful tx
				notification.setContentText("Posted: " + status);
			} catch (YambaClientException e) {
				// notifiying user of error
				Log.e(TAG, "Error " + status, e);
				notification.setContentText("Eorror Posting " + status + " " + e.getLocalizedMessage());
				notification.setContentIntent(settingsPending);
			}
		}
		else {
			// notifying user of no login
			notification.setContentText("No username or password set");
			notification.setContentIntent(settingsPending);
		}
		notificationManager.notify(POST_SERVICE_NOTIFICATION_ID, notification.build());
	}

}
