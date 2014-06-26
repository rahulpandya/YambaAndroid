package com.thenewcircle.yamba;

import static com.thenewcircle.yambacontract.TimelineContract.Columns.*;

import com.thenewcircle.yamba.TimelineDetailsFragment.DisplayDetails;
import com.thenewcircle.yambacontract.TimelineContract;

import android.app.ListFragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;

public class TimelineFragment extends ListFragment implements LoaderCallbacks<Cursor> {
	private static int[] TO = {R.id.message, R.id.user, R.id.time };
	private static String[] FROM = {MESSAGE, USER, TIME_CREATED};
	private SimpleCursorAdapter adapter;
	private ViewBinder viewBinder = new ViewBinder() {
		
		@Override
		public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
			if(view.getId() == R.id.time) {
				TextView textView = (TextView)view;
				CharSequence friendlyTime = DateUtils.getRelativeTimeSpanString(getActivity(), 
						cursor.getLong(columnIndex));
				textView.setText(friendlyTime);
				return true;
			}
			return false;
		}
	};

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		adapter = new SimpleCursorAdapter(getActivity(), R.layout.row_firend_status, null,
				FROM, TO, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		adapter.setViewBinder(viewBinder);
		setListAdapter(adapter);
		
		getLoaderManager().initLoader(0, null, this);
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		if(getActivity() instanceof DisplayDetails) {
			((DisplayDetails)getActivity()).updateDetails(id);
		}
		super.onListItemClick(l, v, position, id);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new CursorLoader(getActivity(), TimelineContract.CONTENT_URI, 
				null, null, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		adapter.swapCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		adapter.swapCursor(null);
	}

}
