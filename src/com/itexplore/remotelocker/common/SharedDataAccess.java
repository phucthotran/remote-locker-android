package com.itexplore.remotelocker.common;

import android.content.Context;
import android.content.SharedPreferences;

import com.itexplore.remotelocker.R;

public class SharedDataAccess implements IDataAccessProvider {

    private static Context sContext;
	private static SharedDataAccess sSingleObject;	
	
	private SharedPreferences mSharedData;
	private SharedPreferences.Editor mSharedDataEditor;
	
	private SharedDataAccess() {
		mSharedData = sContext.getSharedPreferences(sContext.getString(R.string.app_preferences_key), Context.MODE_PRIVATE);
		mSharedDataEditor = mSharedData.edit();
	}
	
	public static SharedDataAccess getInstance() {
		if(sSingleObject == null)
			sSingleObject = new SharedDataAccess();
		
		return sSingleObject;
	}
	
	public static SharedDataAccess getInstance(Context context) {
		sContext = context;
		
		return getInstance();
	}

	public boolean push(String key, Object value) {
		if(value.getClass() == String.class)
			mSharedDataEditor.putString(key, (String)value);
		else if(value.getClass() == Boolean.class)
			mSharedDataEditor.putBoolean(key, (Boolean)value);
		else if(value.getClass() == Integer.class)
			mSharedDataEditor.putInt(key, (Integer)value);
		else if(value.getClass() == Float.class)
			mSharedDataEditor.putFloat(key, (Float)value);
		else if(value.getClass() == Long.class)
			mSharedDataEditor.putLong(key, (Long)value);
		
		return mSharedDataEditor.commit();
	}

	@SuppressWarnings("unchecked")
	public <T> T get(String key, T type) throws Exception {
		if(type.getClass() == String.class)
			return (T)mSharedData.getString(key, "");
		else if(type.getClass() == Boolean.class)
			return (T)((Boolean)mSharedData.getBoolean(key, false));
		else if(type.getClass() == Integer.class)
			return (T)((Integer)mSharedData.getInt(key, 0));
		else if(type.getClass() == Float.class)
			return (T)((Float)mSharedData.getFloat(key, 0));
		else if(type.getClass() == Long.class)
			return (T)((Long)mSharedData.getLong(key, 0));
		else
			throw new ClassNotFoundException();
	}
	
	@SuppressWarnings("unchecked")
	public <T> T get(String key, Object defaultValue, T type) throws Exception {
		if(type.getClass() == String.class)
			return (T)mSharedData.getString(key, (String)defaultValue);
		else if(type.getClass() == Boolean.class)
			return (T)((Boolean)mSharedData.getBoolean(key, (Boolean)defaultValue));
		else if(type.getClass() == Integer.class)
			return (T)((Integer)mSharedData.getInt(key, (Integer)defaultValue));
		else if(type.getClass() == Float.class)
			return (T)((Float)mSharedData.getFloat(key, (Float)defaultValue));
		else if(type.getClass() == Long.class)
			return (T)((Long)mSharedData.getLong(key, (Long)defaultValue));
		else
			throw new ClassNotFoundException();
	}

	@Override
	public int insert() {
		return mSharedDataEditor.commit() ? 1 : -1;
	}

	@Override
	public boolean update() {
		return mSharedDataEditor.commit();
	}

	@Override
	public boolean delete() {
		mSharedDataEditor.clear();
		
		return true;
	}

}
