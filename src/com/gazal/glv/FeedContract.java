package com.gazal.glv;

import android.provider.BaseColumns;

public final class FeedContract {
	public FeedContract(){
		
	}
	
	public static abstract class FeedEntry implements BaseColumns {
		public static final String TABLE_NAME = "entry";
		public static final String COLUMN_NAME_ENTRY_ID = "entry_id";
		public static final String COLUMN_NAME_CATEGORY = "category";
		public static final String COLUMN_NAME_DETAILS = "details";
	}
	
	public static final String SQL_CREATE_ENTRIES = 
			"CREATE TABLE " + FeedEntry.TABLE_NAME + " ( " +
			FeedEntry._ID + " INTEGER PRIMARY KEY, " +
			FeedEntry.COLUMN_NAME_ENTRY_ID + " TEXT, " +
			FeedEntry.COLUMN_NAME_CATEGORY + " TEXT, " +
			FeedEntry.COLUMN_NAME_DETAILS + " TEXT " +
			")";
	
	public static final String SQL_DROP_ENTRIES = 
			"DROP TABLE IF EXITS " + FeedEntry.TABLE_NAME;
	
	
	
}
