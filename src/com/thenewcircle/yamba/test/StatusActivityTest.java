package com.thenewcircle.yamba.test;

import com.thenewcircle.yamba.R;
import com.thenewcircle.yamba.StatusActivity;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;
import android.widget.TextView;

public class StatusActivityTest extends 
	ActivityInstrumentationTestCase2<StatusActivity>{

	private StatusActivity statusActivity;
	private TextView count;
	private EditText status;

	public StatusActivityTest() {
		super(StatusActivity.class);
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		statusActivity = getActivity();
		count = (TextView) statusActivity.findViewById(R.id.count);
		status = (EditText) statusActivity.findViewById(R.id.status);
	}
	
	public void testStatusCount() throws Throwable {
		String expected = getActivity().getString(R.string.maxchars);
		assertEquals(expected, count.getText().toString());

		Runnable cmd = new Runnable() {
			
			@Override
			public void run() {
				status.getText().append("hello");
				String newString = count.getText().toString();
				int actual = Integer.parseInt(newString);
				assertEquals(135, actual);
			}
		};
		
		runTestOnUiThread(cmd);
	}

}
