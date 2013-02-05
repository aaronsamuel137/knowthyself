package com.aaronsamueldavis.knowthyself.moodmapper;

import java.util.Random;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


@SuppressLint("NewApi")
public class ReviewAdapter extends ArrayAdapter<EntryData> {
	 
	private final Context mContext;
    private final EntryData mData[];
    private static final String TAG = "ReviewAdapter";
    private static Random generator;
    private static Typeface mTf;
    //private static final int colors[] = {0xFF4D4D, 0x3838BC, 0x389BBC, 0x339933, 0xFF7519, 0xC46CC4, 0x894C89, 0xDA91FF};
    
    public ReviewAdapter(Context context, int resource, EntryData data[]) {
		super(context, resource, data);
		mData = data;
		mContext = context;
		generator = new Random();
		
	}

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        EntryHolder holder = null;
 
        if(rowView == null)
        {
            // Get a new instance of the row layout view
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            rowView = inflater.inflate(R.layout.review_row, null);
            mTf = Typeface.createFromAsset(rowView.getContext().getAssets(), "fonts/Dinax27s_Handwriting.ttf");
            

            // Hold the view objects in an object,
            // so they don't need to be re-fetched
            
            holder = new EntryHolder();
            holder.time = (TextView) rowView.findViewById(R.id.column_time);
	        holder.emotion = (TextView) rowView.findViewById(R.id.column1);
	        holder.trigger = (TextView) rowView.findViewById(R.id.column2);
	        holder.intensity = (TextView) rowView.findViewById(R.id.column_intensity);

            // Cache the view objects in the tag,
            // so they can be re-accessed later
            rowView.setTag(holder);
        } else {
            holder = (EntryHolder) rowView.getTag();
        }
 
        // Transfer the stock data from the data object
        // to the view objects
        EntryData currentEntry = mData[position];
        
		String minute;
        String hour;
        String am_pm;
        if (currentEntry.hour > 12) {
        	hour = Integer.toString(currentEntry.hour - 12);
            am_pm = " pm";
        }    
        else if (currentEntry.hour == 0) {
        	hour = "12";
        	am_pm = " am";
        }
        else {
        	hour = Integer.toString(currentEntry.hour);
        	if (currentEntry.hour == 12)
        		am_pm = " pm";
        	else
        		am_pm = " am";
        }
        if (currentEntry.minute == 0)
        	minute = "00";
        else if (currentEntry.minute < 10)
        	minute = "0" + currentEntry.minute;
        else
        	minute = Integer.toString(currentEntry.minute);
        
        holder.time.setText(hour + ":" + minute + am_pm);
        holder.emotion.setText((currentEntry.emotion).toUpperCase());
        if (currentEntry.trigger != null && currentEntry.trigger != "") {
        	holder.trigger.setText("Trigger: " + currentEntry.trigger);   
        	holder.trigger.setTypeface(mTf);
        }
        else
        	holder.trigger.setText("");
        if (currentEntry.intensity != 0) {
        	holder.intensity.setText(Integer.toString(currentEntry.intensity));
        	holder.intensity.setTypeface(mTf);
        }
        else
        	holder.intensity.setText("");
        
        holder.time.setTypeface(mTf);
        holder.emotion.setTypeface(mTf);
        
        
        
        rowView.getBackground().setColorFilter(intensity(currentEntry.intensity), PorterDuff.Mode.ADD);
        
        return rowView;
    }
    
    protected static class EntryHolder
    {
    	TextView time;
        TextView emotion;
        TextView trigger;
        TextView intensity;
    }
    
    public int pickRandom (int[] array) {
        int rnd = generator.nextInt(array.length);
        return array[rnd];
    }
    
    public int intensity(int value) {
    	switch (value) {
    	case 0: return 0x00FFFF00;
    	case 1: return 0x08FFFF00;
    	case 2: return 0x10FFFF00;
    	case 3: return 0x18FFFF00;
    	case 4: return 0x20FFFF00;
    	case 5: return 0x28FFFF00;
    	case 6: return 0x30FFFF00;
    	case 7: return 0x38FFFF00;
    	case 8: return 0x40FFFF00;
    	case 9: return 0x48FFFF00;
    	case 10: return 0x50FFFF00;
    	default: return 0;
    	}
    }

}
