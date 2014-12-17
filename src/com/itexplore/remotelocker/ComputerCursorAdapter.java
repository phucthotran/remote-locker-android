package com.itexplore.remotelocker;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.itexplore.remotelocker.common.ComputerEntry;

public class ComputerCursorAdapter extends CursorAdapter {
	
	@SuppressWarnings("deprecation")
    public ComputerCursorAdapter(Context context, Cursor cursor) {
		super(context, cursor);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {	    
		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.layout_list_computer_item, parent, false);
		
		return view;
	}
	
	@Override
    public void bindView(View view, Context context, Cursor cursor) {
        Log.i("ComputerCursorAdapter.bindView()", "Fetch data and binding for row view");
        
        ComputerEntry entry = new ComputerEntry();
        entry.setId(Integer.parseInt(cursor.getString(0)));
        entry.setName(cursor.getString(1));

        TextView tvName = (TextView)view.findViewById(R.id.main_list_tv_computer_name);
        tvName.setText(entry.getName());       
        
        view.setTag(R.id.TAG_LIST_ITEM_DATA, entry.getId());
    }

}
