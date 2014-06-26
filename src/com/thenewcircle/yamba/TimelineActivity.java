package com.thenewcircle.yamba;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.thenewcircle.yamba.TimelineDetailsFragment.DisplayDetails;

public class TimelineActivity extends Activity implements DisplayDetails {

	private static final String TAG = "Yamba." + TimelineActivity.class.getSimpleName();
	private TimelineDetailsFragment timelineDetailsFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		setContentView(R.layout.activity_timeline);
		
		// View fragmentContainer = findViewById(R.id.fragment_container);
		View detailsContainer = findViewById(R.id.details_container);
		if(detailsContainer != null) {
			FragmentTransaction tx = getFragmentManager().beginTransaction();
			timelineDetailsFragment = new TimelineDetailsFragment();
			tx.replace(R.id.details_container, timelineDetailsFragment);
			tx.commit();
		}
		else {
			timelineDetailsFragment = null;
		}
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		ActionBar actionBar = getActionBar();
		Tab tab = actionBar.newTab();
		tab.setText("Timeline");
		tab.setTabListener(new TabListener<TimelineFragment>(this, "timeline", 
				TimelineFragment.class));
		actionBar.addTab(tab);
		
		tab = actionBar.newTab();
		tab.setText("Status");
		tab.setTabListener(new TabListener<StatusFragment>(this, "status", 
				StatusFragment.class));
		actionBar.addTab(tab);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		getActionBar().removeAllTabs();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.yamba, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.refresh:
			Log.d(TAG, "refresh");
			Intent refreshIntent = new Intent(this, TimelineService.class);
			startService(refreshIntent);
			return true;
		case R.id.action_settings:
			Intent settingsIntent = new Intent(this, SettingsActivity.class);
			startActivity(settingsIntent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void updateDetails(long id) {
		if(timelineDetailsFragment != null) {
			timelineDetailsFragment.updateView(id);
		}
		else {
			FragmentTransaction tx = getFragmentManager().beginTransaction();
			TimelineDetailsFragment details = new TimelineDetailsFragment();
			tx.replace(R.id.fragment_container, details);
			tx.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			tx.addToBackStack("Details");
			tx.commit();
			details.setDetailsId(id);
		}
		
	}

}
