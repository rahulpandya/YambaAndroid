package com.thenewcircle.yamba;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import static com.thenewcircle.yambacontract.TimelineContract.Columns.*;

public class TimelineOpenHelper extends SQLiteOpenHelper {

	static final String TABLE = "timeline";
	private static final int VERSION = 1;
	private static final String TAG = "Yamba." + TimelineOpenHelper.class.getSimpleName();

	public TimelineOpenHelper(Context context) {
		super(context, TABLE, null, VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = String.format("create table %s ( %s INT PRIMARY KEY," +
				" %s INT, %s TEXT,  %s TEXT, %s DOUBLE, %s DOUBLE);",  
				TABLE, ID, TIME_CREATED, USER, MESSAGE, LAT, LON);
		Log.d(TAG, "sql: " + sql);
		db.execSQL(sql);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
