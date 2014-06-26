package com.thenewcircle.yamba;

import com.thenewcircle.yambacontract.TimelineContract;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

public class TimelineContentProvider extends ContentProvider {

	private static final String TAG = "Yamba." + TimelineContentProvider.class.getSimpleName();
	private TimelineOpenHelper dbHelper;
	private static final int TIMELINE_DIR = 1;
	private static final int TIMELINE_ITEM = 2;
	
	private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
	
	static {
		// bla.com.thenewcircle.yamba.contentprovider/timeline
		URI_MATCHER.addURI(TimelineContract.AUTHORITY, TimelineContract.PATH.substring(1), TIMELINE_DIR);
		// bla.com.thenewcircle.yamba.contentprovider/timeline/234
		URI_MATCHER.addURI(TimelineContract.AUTHORITY, TimelineContract.PATH.substring(1) + "/#", 
				TIMELINE_ITEM);
	}

	@Override
	public boolean onCreate() {
		dbHelper = new TimelineOpenHelper(getContext());
		return true;
	}


	@Override
	public Uri insert(Uri uri, ContentValues values) {
		Log.d(TAG, "insert " + uri + " values " + values);
		Uri result = null;
		if(URI_MATCHER.match(uri) != TIMELINE_DIR) {
			throw new IllegalArgumentException("Unsupported URI " + uri);
		}
		
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		long rowId = db.insert(TimelineOpenHelper.TABLE, null, values);
		if(rowId > 0) {
			result = ContentUris.withAppendedId(TimelineContract.CONTENT_URI, rowId);
			getContext().getContentResolver().notifyChange(uri, null);
		}
		return result;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		Log.d(TAG, "qurey: " + uri);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(TimelineOpenHelper.TABLE);
		switch(URI_MATCHER.match(uri)) {
		case TIMELINE_DIR:
			// Default sort order
			if(sortOrder == null || sortOrder.trim().length() == 0) {
				sortOrder = TimelineContract.DEFAULT_SORT_ORDER;
			}
			break;
		case TIMELINE_ITEM:
			qb.appendWhere(TimelineContract.Columns.ID + "=" + uri.getLastPathSegment());
			break;
		default:
			break;
		}
		
		Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
		return c;
	}
	
	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}
}
