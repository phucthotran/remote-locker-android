package com.itexplore.remotelocker.common;

import android.content.Context;
import android.database.Cursor;

public class ComputerEntryController {
	
	static final String COL_ID = "_id";
	static final String COL_NAME = "name";
	static final String COL_IP = "ip";
	static final String COL_IDENTIFY_CODE = "identify_code";
	static final String COL_TYPE = "type";

	private SqlLiteDataAccess mDataAccess;
	private Context mContext;
	private static ComputerEntryController sSingleController;
	
	public void setEntryData(ComputerEntry entry){
		mDataAccess.setEntryData(entry);
	}
	
	private ComputerEntryController(Context context) {
		this.mContext = context;
		
		mDataAccess = new SqlLiteDataAccess(this.mContext);
		mDataAccess.open();
	}
	
	public static ComputerEntryController getInstance(Context context) {
		if(sSingleController == null)
			sSingleController = new ComputerEntryController(context);
		
		return sSingleController;
	}
	
	public ComputerEntry get(int id) {
		Cursor c = mDataAccess.get(id);
				
		if(c.getCount() == 0)
		    return null;
		
		int nameIdx = c.getColumnIndex(COL_NAME);
		int ipIdx = c.getColumnIndex(COL_IP);
		int identifyCodeIdx = c.getColumnIndex(COL_IDENTIFY_CODE);
		int typeIdx = c.getColumnIndex(COL_TYPE);
		
		ComputerEntry entry = new ComputerEntry();
		entry.setId(id);
		entry.setName(c.getString(nameIdx));
		entry.setIP(c.getString(ipIdx));
		entry.setIdentifyCode(c.getString(identifyCodeIdx));
		entry.setType(c.getString(typeIdx));
		
		return entry;
	}
	
	public Cursor cursorGet(int id) {
		return mDataAccess.get(id);
	}
	
	public ComputerEntry findByName(String name) {
		Cursor c = mDataAccess.findByName(name);
		
		if(c.getCount() == 0)
		    return null;
		
		int idIdx = c.getColumnIndex(COL_ID);
		int nameIdx = c.getColumnIndex(COL_NAME);
		int ipIdx = c.getColumnIndex(COL_IP);
		int identifyCodeIdx = c.getColumnIndex(COL_IDENTIFY_CODE);
		int typeIdx = c.getColumnIndex(COL_TYPE);
		
		ComputerEntry entry = new ComputerEntry();
		entry.setId(c.getInt(idIdx));
		entry.setName(c.getString(nameIdx));
		entry.setIP(c.getString(ipIdx));
		entry.setIdentifyCode(c.getString(identifyCodeIdx));
		entry.setType(c.getString(typeIdx));
		
		return entry;
	}
	
	public Cursor cursorfindByName(String name) {
		return mDataAccess.findByName(name);
	}
	
	public ComputerEntries getLocalEntries() {
		Cursor c = mDataAccess.getLocalEntries();
		
		if(c.getCount() == 0)
			return null;
		
		ComputerEntries entries = new ComputerEntries();
		
		int idIdx = c.getColumnIndex(COL_ID);
		int nameIdx = c.getColumnIndex(COL_NAME);
		int ipIdx = c.getColumnIndex(COL_IP);
		int identifyCodeIdx = c.getColumnIndex(COL_IDENTIFY_CODE);
		int typeIdx = c.getColumnIndex(COL_TYPE);
		
		int rowIdx = 0;
		
		while(rowIdx < c.getCount()) {
			ComputerEntry entry = new ComputerEntry();
			entry.setId(c.getInt(idIdx));
			entry.setName(c.getString(nameIdx));
			entry.setIP(c.getString(ipIdx));
			entry.setIdentifyCode(c.getString(identifyCodeIdx));
			entry.setType(c.getString(typeIdx));
			
			entries.add(entry);
			
			c.moveToNext();
			rowIdx++;
		}
		
		return entries;		
	}
	
	public Cursor cursorGetLocalEntries() {
		return mDataAccess.getLocalEntries();
	}
	
	public ComputerEntries getRemoteEntries() {
		Cursor c = mDataAccess.getRemoteEntries();
		
		if(c.getCount() == 0)
			return null;
		
		ComputerEntries entries = new ComputerEntries();
		
		int idIdx = c.getColumnIndex(COL_ID);
		int nameIdx = c.getColumnIndex(COL_NAME);
		int ipIdx = c.getColumnIndex(COL_IP);
		int identifyCodeIdx = c.getColumnIndex(COL_IDENTIFY_CODE);
		int typeIdx = c.getColumnIndex(COL_TYPE);
		
		int rowIdx = 0;
		
		while(rowIdx < c.getCount()) {
			ComputerEntry entry = new ComputerEntry();
			entry.setId(c.getInt(idIdx));
			entry.setName(c.getString(nameIdx));
			entry.setIP(c.getString(ipIdx));
			entry.setIdentifyCode(c.getString(identifyCodeIdx));
			entry.setType(c.getString(typeIdx));
			
			entries.add(entry);
			
			c.moveToNext();
			rowIdx++;
		}
		
		return entries;
	}
	
	public Cursor cursorGetRemoteEntries() {
		return mDataAccess.getRemoteEntries();
	}
	
	public int insert() {
		return mDataAccess.insert();
	}
	
	public boolean update() {
		return mDataAccess.update();
	}
	
	public boolean delete() {
		return mDataAccess.delete();
	}
}
