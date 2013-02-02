package com.aaronsamueldavis.knowthyself.moodmapper;

public class EntryData {
	public int id;
	public int hour;
	public int minute;
	public String emotion;
	public String trigger;
	
	public EntryData(int h, int m, int i) {
		id = i;
		hour = h;
		minute = m;
		emotion = "";
		trigger = "";
	}
}
