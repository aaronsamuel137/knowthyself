package com.aaronsamueldavis.knowthyself.moodmapper;

public class EntryData {
	public int hour;
	public int minute;
	public int intensity;
	public String emotion;
	public String trigger;
	
	public EntryData(int h, int m) {
		hour = h;
		minute = m;
		intensity = 0;
		emotion = "";
		trigger = "";
	}
}
