package com.aaronsamueldavis.knowthyself.moodmapper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

public class About extends Activity {
	
	private static final String TAG = "About Activity";
	private static final int ABOUT_PAGE_1 = 0;
	private static final int ABOUT_PAGE_2 = 1;
	private int currentPage;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		currentPage = ABOUT_PAGE_1;
		// Show the Up button in the action bar.
		// Make sure we're running on Honeycomb or higher to use ActionBar APIs
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Show the Up button in the action bar.
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
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
	
	@Override
	protected void onRestart() {
	    super.onRestart();
	    Intent i = new Intent(this, MainActivity.class);
    	startActivity(i);
	}
	
	public void previousPage(View view) {
		switch (currentPage) {
		case ABOUT_PAGE_1:
			Intent i = new Intent(this, MainActivity.class);
        	startActivity(i);
			break;
		case ABOUT_PAGE_2:
			setContentView(R.layout.activity_about);
			currentPage = ABOUT_PAGE_1;
			break;
		default:
			Log.e(TAG, "Page turning error");
		}
	}
	
	public void nextPage(View view) {
		switch (currentPage) {
		case ABOUT_PAGE_1:
			setContentView(R.layout.activity_about_premises);
			currentPage = ABOUT_PAGE_2;
			break;
		case ABOUT_PAGE_2:
			Intent i = new Intent(this, MainActivity.class);
        	startActivity(i);
        	break;
		}
		Log.e(TAG, "Page turning error");
	}

}

