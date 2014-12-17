package com.itexplore.remotelocker;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class RemoteFragment extends ComputerListFragment {
    
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {		
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.layout_remote_fragment, container, false);
		
		return v;
	}
	
	@Override
	Cursor getData() {
	    return mComputerController.cursorGetRemoteEntries();
	}
	
}
