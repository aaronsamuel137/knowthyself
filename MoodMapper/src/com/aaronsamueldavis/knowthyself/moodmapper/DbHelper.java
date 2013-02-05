package com.aaronsamueldavis.knowthyself.moodmapper;

import java.util.Calendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DbHelper {

	// used in all tables
	public static final String KEY_ROWID = "_id";
	
	// values for emotion table
    public static final String KEY_EMOTION = "emotion";
    
    // values for entry table
    public static final String KEY_HOUR = "hour";
    public static final String KEY_MINUTE = "minute";
    public static final String KEY_DAY = "day";
    public static final String KEY_ENTRY = "entry";
    public static final String KEY_TRIGGER = "trigger"; //used for trigger table too
    public static final String KEY_INTENSITY = "intensity";
    
    // table names
    public static final String TABLE_EMOTION = "emotions";
    public static final String TABLE_ENTRY = "entries";
    public static final String TABLE_TRIGGER = "triggers";

    private static final String TAG = "DbHelper";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    private static final String EMOTION_TABLE_CREATE =
        "create table emotions (_id integer primary key autoincrement, "
        + "emotion text unique not null collate nocase);";
    
    private static final String ENTRY_TABLE_CREATE =
            "create table entries (_id integer primary key autoincrement, "
            + "hour integer not null, minute integer not null, entry text not null, "
            + "trigger text, day text not null, intensity integer);";
    
    private static final String TRIGGER_TABLE_CREATE =
            "create table triggers (_id integer primary key autoincrement, "
            + "trigger text unique not null collate nocase);";

    private static final String DATABASE_NAME = "data";
    
    private static final int DATABASE_VERSION = 1;

    private final Context mContext;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(EMOTION_TABLE_CREATE);
            db.execSQL(ENTRY_TABLE_CREATE);
            db.execSQL(TRIGGER_TABLE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS emotions;");
            db.execSQL("DROP TABLE IF EXISTS entries;");
            db.execSQL("DROP TABLE IF EXISTS triggers;");
            onCreate(db);
        }
    }

    public DbHelper(Context ctx) {
        this.mContext = ctx;
    }

    /**
     * Open the notes database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     * 
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public DbHelper open() throws SQLException {
        mDbHelper = new DatabaseHelper(mContext);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }
    
    public Cursor fetchAllItems(String table, String column) {
    	return mDb.query(table, new String[] {KEY_ROWID, column}
                , column + "<>?", new String[] {""}, null, null, column);
    }   
    
    public Cursor fetchReview(String day) {
    	return mDb.query(TABLE_ENTRY, new String[] {KEY_ROWID, KEY_HOUR, KEY_MINUTE, KEY_ENTRY, KEY_TRIGGER, KEY_INTENSITY, KEY_DAY}
                , KEY_DAY + "=?", new String[] {day}, null, null, null);
    }
	  
    public long addMood(String emotion) {
    	if (emotion == "")
        	return -1; // don't add empty string to database
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_EMOTION, emotion);
        return mDb.insert(TABLE_EMOTION, null, initialValues);
	}
	  
    public boolean deleteTableItem(String table, String column, String item) {
    	return mDb.delete(table, column + "='" + item + "'", null) > 0;
	}
    
    public long addTrigger(String trigger) {
    	if (trigger == "")
        	return -1; // don't add empty string to database
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_TRIGGER, trigger);
        return mDb.insert(TABLE_TRIGGER, null, initialValues);
	}
    
	  
	public long addEntry(String entry, String trigger, int intensity) {
		ContentValues initialValues = new ContentValues();
		  
		// get current time from device
	    Calendar cal = Calendar.getInstance();
	    int hour = cal.get(Calendar.HOUR_OF_DAY);
	    int minute = cal.get(Calendar.MINUTE);
	    String day = (cal.get(Calendar.MONTH) + 1) + "-" + cal.get(Calendar.DAY_OF_MONTH) + "-" + cal.get(Calendar.YEAR);
	      
	    initialValues.put(KEY_ENTRY, entry);
	    initialValues.put(KEY_HOUR, hour);
	    initialValues.put(KEY_MINUTE, minute);
	    initialValues.put(KEY_TRIGGER, trigger);
	    initialValues.put(KEY_DAY, day);
	    initialValues.put(KEY_INTENSITY, intensity);
	    return mDb.insert(TABLE_ENTRY, null, initialValues);
	}
	  
	public String getNameFromId(long id) {
		Cursor name = mDb.rawQuery("SELECT emotion FROM emotions", null);
		if (name.moveToPosition((int) id - 1)) {
			return name.getString(0);
		}
		else
			return null;
	}
}

