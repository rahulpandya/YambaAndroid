package com.thenewcircle.yamba;

import com.thenewcircle.yambacontract.TimelineContract;
import static com.thenewcircle.yambacontract.TimelineContract.Columns.*;

import android.app.Fragment;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TimelineDetailsFragment extends Fragment {
	
	public interface DisplayDetails {
		public void updateDetails(long id);
	}
	
	private static final String TAG = "Yamba." + TimelineDetailsFragment.class.getSimpleName();
	private TextView textUser;
	private TextView textMessage;
	private TextView textLat;
	private TextView textLon;
	private TextView textTime;
	private Long id;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.fragment_details, 
				container, false);
		
		textUser = (TextView) layout.findViewById(R.id.detailsUser);
		textMessage = (TextView) layout.findViewById(R.id.detailsMessage);
		textTime = (TextView) layout.findViewById(R.id.detailsTime);
		textLat = (TextView) layout.findViewById(R.id.detailsLat);
		textLon = (TextView) layout.findViewById(R.id.detailsLon);
		
		return layout;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if(id != null) {
			updateView(id);
			id = null;
		}
	}
	
	public void setDetailsId(Long id) {
		this.id = id;
	}
	
	public void updateView(Long id) {
		Log.d(TAG, "updateView: " + id);
		if(id == -1) {
			textUser.setText("");
			textMessage.setText("Select a message");
			textTime.setText("");
			textLat.setText("");
			textLon.setText("");
		}
		else {
			Uri uri = ContentUris.withAppendedId(TimelineContract.CONTENT_URI, id);
			Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
			if(!cursor.moveToFirst()) return;
			
			String user = cursor.getString(cursor.getColumnIndex(USER));
			String message = cursor.getString(cursor.getColumnIndex(MESSAGE));
			Long createdAt = cursor.getLong(cursor.getColumnIndex(TIME_CREATED));
			Double lat = cursor.getDouble(cursor.getColumnIndex(LAT));
			Double lon = cursor.getDouble(cursor.getColumnIndex(LON));
			
			textUser.setText(user);
			textMessage.setText(message);
			
			textTime.setText(DateUtils.getRelativeTimeSpanString(createdAt));
			if(lat == null) {
				textLat.setVisibility(View.INVISIBLE);
				textLon.setVisibility(View.INVISIBLE);
			}
			else {
				textLat.setVisibility(View.VISIBLE);
				textLon.setVisibility(View.VISIBLE);
				textLat.setText(lat.toString());
				textLon.setText(lon.toString());
			}
		}
	}
}
