package com.thenewcircle.yamba;

import java.util.List;

import static com.thenewcircle.yambacontract.TimelineContract.Columns.*;
import com.marakana.android.yamba.clientlib.YambaClient;
import com.marakana.android.yamba.clientlib.YambaClient.Status;
import com.marakana.android.yamba.clientlib.YambaClient.TimelineProcessor;
import com.marakana.android.yamba.clientlib.YambaClientException;
import com.thenewcircle.yambacontract.TimelineContract;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;

public class TimelineService extends IntentService {
	
	private static final String TAG = "Yamba." + TimelineService.class.getSimpleName();

	public TimelineService() {
		super(TimelineService.class.getName());
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// Wakelock
		PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
		WakeLock wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
		wakeLock.acquire(100000);
		try {
			// Get app "singleton"
			YambaApp app = (YambaApp) getApplication();
			// get client from app
			YambaClient client = app.getYambaClient();
			
			// get content provider
			final ContentResolver contentResolver = getContentResolver();
			final ContentValues values = new ContentValues();
			
			if(client != null) {
				try {
					Cursor c = contentResolver.query(TimelineContract.CONTENT_URI,
							TimelineContract.MAX_TIME_CREATED, null, null, null);
					
					final long maxTime = c.moveToFirst()?c.getLong(0): Long.MIN_VALUE;
					client.fetchFriendsTimeline(new TimelineProcessor() {
						@Override
						public void onTimelineStatus(Status status) {
							Log.d(TAG, "status " + status.getMessage());
							long time = status.getCreatedAt().getTime();
							if(time > maxTime) {
								values.put(ID, status.getId());
								values.put(MESSAGE, status.getMessage());
								values.put(TIME_CREATED, time);
								values.put(USER, status.getUser());
								values.put(LAT, status.getLatitude());
								values.put(LON, status.getLongitude());
								
								Uri uri = contentResolver.insert(TimelineContract.CONTENT_URI, values);
								Log.d(TAG, "onTimelineStatus inserted URI " + uri);
							}
							
						}
						
						@Override
						public void onStartProcessingTimeline() {
							Log.d(TAG, "onStartProccessingTimeline()");
						}
						
						@Override
						public void onEndProcessingTimeline() {
							Log.d(TAG, "onEngProcessingTimeline()");
						}
						
						@Override
						public boolean isRunnable() {
							return true;
						}
					});
					
				} catch (YambaClientException e) {
					Log.e(TAG, "Error getting timeline", e);
				}
				
			}
			
		}
		finally {
			wakeLock.release();
		}
	}

}
