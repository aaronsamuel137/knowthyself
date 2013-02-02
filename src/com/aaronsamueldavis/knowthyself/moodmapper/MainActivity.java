package com.aaronsamueldavis.knowthyself.moodmapper;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class MainActivity extends ListActivity {

	private DbHelper mDbHelper;
	private ListAdapter mListAdapter;
	private boolean deleteMode;
	private String mEmotion;
	private String mTrigger;
	private TextView mTitleView;
	private TextView mEmptyView;
	private Button mButton;
	//private Context mContext;
	
	public final static String EXTRA_MESSAGE = "com.example.testMapper.MESSAGE";
	public final static String TAG = "MainActivity";
	
	// different list view option
	private static final int EMOTION = 1;
	private static final int TRIGGER = 2;
	private int displayListView;
	private String listName;
	private String tableName;
	
	// menu options
	private static final int ABOUT = Menu.FIRST;
    private static final int HISTORY = Menu.FIRST + 1;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		// Make sure we're running on Honeycomb or higher to use ActionBar APIs
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Show the Up button in the action bar.
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        mTitleView = (TextView) findViewById(R.id.list_title);
        mEmptyView = (TextView) findViewById(android.R.id.empty);
        mButton = (Button) findViewById(R.id.left_top_button);
		deleteMode = false;
		mDbHelper = new DbHelper(this);
		mDbHelper.open();
		loadList(EMOTION);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		super.onCreateOptionsMenu(menu);
		menu.add(0, ABOUT, 0, R.string.about);
		//menu.add(0, HISTORY, 0, R.string.history);
	    return true;
	}
	
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
        case ABOUT:
        	Intent i = new Intent(this, About.class);
        	startActivity(i);
            return true;
        case HISTORY:
        	//Intent i2 = new Intent(this, AboutActivity.class);
        	//startActivity(i2);
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Cursor cursor = (Cursor) getListView().getItemAtPosition(position);
		String item = cursor.getString(cursor.getColumnIndex(listName));
		if (deleteMode == true) {
			mDbHelper.deleteTableItem(tableName, listName, item);
			deleteMode = false;
			fillData();
		}
		else {
			switch (displayListView) {
			case EMOTION:
				mEmotion = item;
				loadList(TRIGGER);
				break;
			case TRIGGER:
				mTrigger = item;
				addEntry(l);
				break;
			default:
				Log.e(TAG, "onListClick error");
			}
		}
		/*else {
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("Failure!");
			alert.setMessage("Emotion not found :(");
			alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					// ok
				}
			});
			alert.show();
		}*/
	}
	
	private void addEntry(View view) {
		mDbHelper.addEntry(mEmotion, mTrigger);
		successAlert(view);
	}

	@Override
	protected void onPause() {
	    super.onPause();
	    mDbHelper.close();
	}
	
	@Override
	protected void onStop() {
	    super.onStop();
	}
	
	@Override
	protected void onResume() {
	    super.onResume();
	    mDbHelper.open();
	}

	
	@SuppressWarnings("deprecation")
	private void fillData() {
		Cursor cursor = mDbHelper.fetchAllItems(tableName, listName);
		// Create an array for our food names
		String[] from = new String[] {listName};
					
		// Create an array of fields for binding to
		int[] to = new int[] {R.id.text1};
		
		if (!cursor.moveToFirst())
			cursor = null;
		
		mListAdapter = new SimpleCursorAdapter(this, R.layout.list_row, cursor, from, to);	
		setListAdapter(mListAdapter);
	}
	
	public void startReview(View view) {
		Intent i = new Intent(this, DailyReview.class);
    	startActivity(i);
	}
	
	public void addItem(View view) {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Add " + listName);
		
		final EditText input = new EditText(this);
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String addition = input.getText().toString();
				if (displayListView == EMOTION)
					mDbHelper.addMood(addition);
				if (displayListView == TRIGGER)
					mDbHelper.addTrigger(addition);
				loadList(displayListView);
			}
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// cancelled
			}
		});
		alert.show();
	}
	
	public void deleteItem(View view) {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		
		alert.setTitle("Delete " + listName);
		alert.setMessage("Click an item to delete it");
		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				deleteMode = true;
			}
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// cancelled
			}
		});
		alert.show();
	}
	
	public void loadList(int display) {
		switch (display) {
		case EMOTION:
			displayListView = EMOTION;
			listName = DbHelper.KEY_EMOTION;
			tableName = DbHelper.TABLE_EMOTION;
			mTitleView.setText("What do you feel?");
			mEmptyView.setText(R.string.empty_main);
			mButton.setText("Review");
			mButton.setOnClickListener(new OnClickListener() {
				public void onClick(View view) {
					startReview(view);
				}
			});
			break;
		case TRIGGER:
			displayListView = TRIGGER;
			listName = DbHelper.KEY_TRIGGER;
			tableName = DbHelper.TABLE_TRIGGER;
			mTitleView.setText("What is triggering it?");
			mEmptyView.setText(R.string.empty_trigger);
			mButton.setText("Log without Trigger");
			mButton.setOnClickListener(new OnClickListener() {
				public void onClick(View view) {
					addEntry(view);
				}
			});
			break;
		default:
			Log.e(TAG, "invalid load list parameter");
		}
		fillData();
	}

	public void cancelledAlert(View view) {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Entry Cancelled");
		alert.setMessage("No entry was added");
		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// cancelled
			}
		});
		alert.show();
	}
	
	public void successAlert(View view) {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Success!");
		alert.setMessage("Entry created successfully");
		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				loadList(EMOTION);
			}
		});
		alert.show();
	}
}
