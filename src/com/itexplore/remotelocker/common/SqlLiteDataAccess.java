package com.itexplore.remotelocker.common;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SqlLiteDataAccess implements IDataAccessProvider {
	
	static final String DATABASE_NAME = "com.itexplore.remotelocker.remotelocker_db";
	static final String DATABASE_TABLE = "computer";
	static final int DATABASE_VERSION = 1;
	
	static final String KEY_ID = "_id";
	static final String KEY_NAME = "name";
	static final String KEY_IP = "ip";
	static final String KEY_IDENTIFY_CODE = "identify_code";
	static final String KEY_TYPE = "type";
	
	static final String DATABASE_CREATE = 
			"create table " + DATABASE_TABLE + " (" 
			+ KEY_ID + " integer primary key autoincrement, "
			+ KEY_NAME + " text not null, "
			+ KEY_IP + " text not null, "
			+ KEY_IDENTIFY_CODE + " text not null, "
			+ KEY_TYPE + " text not null);";
	
	private ComputerEntry mComputerEntry;
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	
	public void setEntryData(ComputerEntry entry) {
		this.mComputerEntry = entry;
	}
	
	public SqlLiteDataAccess(Context context) {		
		mDbHelper = new DatabaseHelper(context);
	}
	
	public SqlLiteDataAccess open() throws SQLException {
	    Log.i("SqlLiteDataAccess.open()", "Create new SqlLiteDatabase connection and open for processing");
	    
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}
	
	public void close() {
	    Log.i("SqlLiteDataAccess.open()", "Close current SqlLiteDatabase connection");
	    
		mDbHelper.close();
	}
	
	public Cursor get(int id) {
		Cursor c = mDb.query(true, DATABASE_TABLE, null, KEY_ID + "=" + id, null, null, null, KEY_NAME, null);
		
		if(c == null)
			return null;
		
		c.moveToFirst();
		
		return c;
	}
	
	public Cursor findByName(String name) {
		Cursor c = mDb.query(true, DATABASE_TABLE, null, KEY_NAME + "=" + name, null, null, null, KEY_NAME, null);
		
		if(c == null)
			return null;
		
		c.moveToFirst();		
		
		return c;
	}
	
	public Cursor getRemoteEntries() {
		Cursor c = mDb.query(true, DATABASE_TABLE, null, KEY_TYPE + "='remote'", null, null, null, KEY_NAME, null);
		
		if(c == null)
			return null;
		
		c.moveToFirst();
		
		return c;
	}
	
	public Cursor getLocalEntries() {
		Cursor c = mDb.query(true, DATABASE_TABLE, null, KEY_TYPE + "='local'", null, null, null, KEY_NAME, null);
		
		if(c == null)
			return null;
		
		c.moveToFirst();
		
		return c;
	}
	
	@Override
	public int insert() {
	    Log.i("SqlLiteDataAccess.insert()", "Db: Insert new computer");
	    
		ContentValues entryValues = new ContentValues();
		entryValues.put(KEY_NAME, mComputerEntry.getName());
		entryValues.put(KEY_IP, mComputerEntry.getIP());
		entryValues.put(KEY_IDENTIFY_CODE, mComputerEntry.getIdentifyCode());
		entryValues.put(KEY_TYPE, mComputerEntry.getType());

		return (int)mDb.insert(DATABASE_TABLE, null, entryValues);
	}

	@Override
	public boolean update() {
	    Log.i("SqlLiteDataAccess.update()", "Db: update a computer");
	    
		ContentValues entryValues = new ContentValues();
		entryValues.put(KEY_NAME, mComputerEntry.getName());
		entryValues.put(KEY_IP, mComputerEntry.getIP());
		entryValues.put(KEY_IDENTIFY_CODE, mComputerEntry.getIdentifyCode());		

		return mDb.update(DATABASE_TABLE, entryValues, KEY_ID + "=" + mComputerEntry.getId(), null) > 0;
	}

	@Override
	public boolean delete() {
	    Log.i("SqlLiteDataAccess.delete()", "Db: delete a computer");
	    
		return mDb.delete(DATABASE_TABLE, KEY_ID + "=" + mComputerEntry.getId(), null) > 0;
	}
	
	static class DatabaseHelper extends SQLiteOpenHelper {

		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}
		
		@Override
		public void onCreate(SQLiteDatabase db) {
			try {
				db.execSQL(DATABASE_CREATE);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
			onCreate(db);
		}
		
	}
	
}
