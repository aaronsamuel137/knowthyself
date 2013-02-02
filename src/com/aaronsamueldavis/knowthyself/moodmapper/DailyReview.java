package com.aaronsamueldavis.knowthyself.moodmapper;

import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class DailyReview extends ListActivity implements OnClickListener{

	private DbHelper mDbHelper;
	private static final String TAG = "ReviewActivity";
	private GestureDetector gestureDetector;
	private ListView mListView;
	private TextView mHeaderText;
	private TextView mEmptyView;
	
	private int[] months = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
	private int day;
	private int month;
	private int year;
	boolean isLeapYear;
	View.OnTouchListener gestureListener;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_daily_review);
		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getCalenderData();
		
		Log.i(TAG, "day = " + getDateString());
		
		
		mListView = (ListView) findViewById(android.R.id.list);
		View header = (View) getLayoutInflater().inflate(R.layout.review_header_row, null);
		mHeaderText = (TextView) header.findViewById(R.id.review_date);
		mHeaderText.setText("Daily Review: " + getDateString());
		mEmptyView = (TextView) findViewById(android.R.id.empty);
		mEmptyView.setText("Nothing logged for " + getDateString());
		//mHeaderText.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
		mListView.addHeaderView(header);
		
	    gestureDetector = new GestureDetector(this,	new SwipeGestureDetector());
	    gestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        };
        
		mDbHelper = new DbHelper(this);
		mDbHelper.open();
		
		Log.i(TAG, "onCreate called");
		Log.i(TAG, "day = " + getDateString());

		
		setViewByDay(getDateString());
	}

	public void setViewByDay(String day) {
		Cursor cursor = mDbHelper.fetchReview(day);
		int numRows = cursor.getCount();
		EntryData entries[] = getData(cursor, numRows);
		
		if (entries != null) {
			ReviewAdapter adapter = new ReviewAdapter(this, R.layout.review_row, entries);
			mHeaderText.setText("Daily Review: " + getDateString());
			mListView.setAdapter(adapter);
		}
	}
		
	
	@Override
	  public boolean onTouchEvent(MotionEvent event) {
	    if (gestureDetector.onTouchEvent(event)) {
	      return true;
	    }
	    return super.onTouchEvent(event);
	  }

	  private void onLeftSwipe() {
		  getTomorrow();
		  mEmptyView.setText("Nothing logged for " + getDateString());
		  setViewByDay(getDateString());
	  }

	  void onRightSwipe() {
		  getYesterday();
		  mEmptyView.setText("Nothing logged for " + getDateString());
		  setViewByDay(getDateString());
	  }
	
	@Override
	protected void onPause() {
	    super.onPause();
	    Log.i(TAG, "onPause called");
	    mDbHelper.close();
	}
	
	@Override
	protected void onStop() {
	    super.onStop();
	    Log.i(TAG, "onStop called");
	}
	
	@Override
	protected void onResume() {
	    super.onResume();
	    Log.i(TAG, "onResume called");
	    mDbHelper.open();
	}
	
	@Override
	protected void onRestart() {
	    super.onRestart();
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_daily_review, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private EntryData[] getData(Cursor cursor, int numRows) {
		EntryData data[] = new EntryData[numRows];
		
		Log.e(TAG, "numRows = " + Integer.toString(numRows));
		
		if (cursor.moveToFirst() && !cursor.isAfterLast()) {
			int hour = cursor.getInt(cursor.getColumnIndex(DbHelper.KEY_HOUR));
			int minute = cursor.getInt(cursor.getColumnIndex(DbHelper.KEY_MINUTE));
			String timeString = Integer.toString(hour) + ":" + Integer.toString(minute);
			float timeFloat = hour + (minute/60);
			//int i = (int) Math.floor(timeFloat);
			
			Log.e(TAG, "time = " + Float.toString(timeFloat));
			
			// for each cursor item, create an array entry
			for (int i = 0; i < numRows; i++) {	
				data[i] = new EntryData(hour, minute, cursor.getInt(i));
				data[i].emotion = cursor.getString(cursor.getColumnIndex(DbHelper.KEY_ENTRY));
				data[i].trigger = cursor.getString(cursor.getColumnIndex(DbHelper.KEY_TRIGGER));
				Log.i(TAG, "hour = " + data[i].hour + " minute = " + data[i].minute +
			        	" emotion = " + data[i].emotion + " trigger = " + data[i].trigger);
				if (cursor.moveToNext() && !cursor.isAfterLast()) {
					hour = cursor.getInt(cursor.getColumnIndex(DbHelper.KEY_HOUR));
					minute = cursor.getInt(cursor.getColumnIndex(DbHelper.KEY_MINUTE));
					timeString = Integer.toString(hour) + ":" + Integer.toString(minute);
					timeFloat = hour + (minute/60);
				}
				
				// if no more cursors, break out of loop
				else
					break;
			}	
		}
		Log.e(TAG, "returning getData");
		return data;
	}
	
	public void getCalenderData() {
		isLeapYear = (year % 4 == 0); // leap years are always multiples of 4
		Calendar cal = Calendar.getInstance();
		day = cal.get(Calendar.DAY_OF_MONTH);
		month = cal.get(Calendar.MONTH); // months index starting with January = 0
		year = cal.get(Calendar.YEAR);
	}
	
	public String getDateString() {
		return (month + 1) + "-" + day + "-" + year;
	}
	
	public void getYesterday() {	
		if (day == 1) {
			if (month == 0) {
				month = 11;
			}
			else month -= 1;
			
			if (month == 2 && isLeapYear)
				day = 29;
			else
				day = months[month];
		}
		else day -= 1;
	}
		
	public void getTomorrow() {
		if (day == months[month]) {
			if (month == 11) {
				month = 0;
			}
			else if (month == 1 && isLeapYear)				
				day = 29;
			else month += 1;

			day = 1;
		}
		else if (month == 1 && day == 29)
			day = 1;
		else day += 1;
	}
	

	//Private class for gestures
	public class SwipeGestureDetector
	       extends SimpleOnGestureListener {
	 // Swipe properties, you can change it to make the swipe
	 // longer or shorter and speed
	 private static final int SWIPE_MIN_DISTANCE = 120;
	 private static final int SWIPE_MAX_OFF_PATH = 200;
	 private static final int SWIPE_THRESHOLD_VELOCITY = 200;
	 //final ViewConfiguration vc = ViewConfiguration.get(getContext());
	 //final int swipeMinDistance = vc.getScaledTouchSlop();
	 //final int swipeThresholdVelocity = vc.getScaledMinimumFlingVelocity();
	 // (there is also vc.getScaledMaximumFlingVelocity() one could check against)

	 @Override
	 public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		 try {
			 float diffAbs = Math.abs(e1.getY() - e2.getY());
			 float diff = e1.getX() - e2.getX();

	     if (diffAbs > SWIPE_MAX_OFF_PATH)
	    	 return false;
	    
	     // Left swipe
	     if (diff > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
	    	 DailyReview.this.onLeftSwipe();

	     // Right swipe
	     } else if (-diff > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
	    	 DailyReview.this.onRightSwipe();
	     }
	   } catch (Exception e) {
		   Log.e("YourActivity", "Error on gestures");
	   }
	   return false;
	}
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
}
