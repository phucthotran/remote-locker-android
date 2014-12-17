package com.itexplore.remotelocker;

import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.itexplore.remotelocker.common.ComputerEntry;
import com.itexplore.remotelocker.common.ComputerEntryController;

import java.util.List;
import java.util.Vector;

public abstract class ComputerListFragment extends ListFragment implements AbsListView.MultiChoiceModeListener, OnItemClickListener {
 
    protected ComputerEntryController mComputerController;
    private ComputerCursorAdapter mListAdapter;
    private ListView mListView;
    private Menu mMenu;
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {      
        super.onActivityCreated(savedInstanceState);
        
        mComputerController = ComputerEntryController.getInstance(getActivity());
        
        new Handler().post(new Runnable(){
            @Override
            public void run() {
                Log.i("ComputerListFragment.onActivityCreated()", "fetch data from db and display on fragment");
                
                mListAdapter = new ComputerCursorAdapter(getActivity(), getData());
                setListAdapter(mListAdapter);
                mListView = getListView();              
                mListView.setItemsCanFocus(false);
                mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
                mListView.setMultiChoiceModeListener(ComputerListFragment.this);
                mListView.setOnItemClickListener(ComputerListFragment.this);
            }
        });
    }
    
    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {       
        mMenu = menu;                        
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.main_cab_multi, menu);
        
        return true;
    }
    
    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        // TODO Auto-generated method stub
        return false;
    }
    
    @Override
    public void onDestroyActionMode(ActionMode mode) {
        // TODO Auto-generated method stub        
    }
    
    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch(item.getItemId()) {
            
            case R.id.main_mnuSelectAll:
            {
                int idx = 0;
                int count = mListView.getCount();
                
                while(idx < count) {
                    mListView.setItemChecked(idx, true);
                    
                    idx++;
                }
                                
                return true;
            }
            
            case R.id.main_mnuEditEntry:
            {
                View selectedView = findCheckedViews().get(0);
                int entryId = (Integer)selectedView.getTag(R.id.TAG_LIST_ITEM_DATA);
                ComputerEntry entry = mComputerController.get(entryId);
                
                if(mComputerController.get(entry.getId()) == null) {                                
                    Toast.makeText(getActivity(), getString(R.string.db_proccess_error_msg), Toast.LENGTH_SHORT).show();
                    return false;
                }

                EditComputerDialogFragment editDialog = new EditComputerDialogFragment();
                editDialog.editId(entry.getId());
                editDialog.show(getActivity().getFragmentManager(), EditComputerDialogFragment.class.getName());
                mode.finish();
                return true;
            }
            
            case R.id.main_mnuDeleteEntry:
            {
                final int fCheckedItems = mListView.getCheckedItemCount();
                final List<View> fSelectedViews = findCheckedViews();
                
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
                
                alertBuilder.setTitle(R.string.delele_dialog_title)
                        .setMessage(String.format(getString(R.string.delete_dialog_msg), fCheckedItems));
                
                alertBuilder.setPositiveButton(R.string.delete_dialog_ok, new DialogInterface.OnClickListener() {                   
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int idx = 0;         
                        
                        while(idx < fSelectedViews.size()) {
                            View v = fSelectedViews.get(idx);
                            
                            if(v == null || v.getTag(R.id.TAG_LIST_ITEM_DATA) == null)
                                return;                            
                            
                            int entryId = (Integer)v.getTag(R.id.TAG_LIST_ITEM_DATA);
                            ComputerEntry entry = mComputerController.get(entryId);
                            
                            mComputerController.setEntryData(entry);
                            
                            if(!mComputerController.delete()) {
                                dialog.dismiss();
                                Toast.makeText(getActivity(), getString(R.string.db_proccess_error_msg), Toast.LENGTH_SHORT).show();
                                return;
                            }
                            
                            updateList();                            
                            
                            idx++;
                        }
                        
                        dialog.dismiss();
                        Toast.makeText(getActivity(), String.format(getString(R.string.delete_success_msg), fCheckedItems), Toast.LENGTH_SHORT).show();
                    }
                });
                
                alertBuilder.setNegativeButton(R.string.delete_dialog_cancel, new DialogInterface.OnClickListener() {                   
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();                        
                    }
                });
                
                AlertDialog alert = alertBuilder.create();
                alert.show();
                
                mode.finish();
                return true;
            }
            
            default:
                return false;
                
        }
    }
    
    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
        if(mListView.getCheckedItemCount() == 1) {
            MenuInflater inflater = mode.getMenuInflater();
            mMenu.clear();
            inflater.inflate(R.menu.main_cab_single, mMenu);
        } else if(mListView.getCheckedItemCount() > 1) {
            MenuInflater inflater = mode.getMenuInflater();
            mMenu.clear();
            inflater.inflate(R.menu.main_cab_multi, mMenu);
        }
    }
    
    @Override
    public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
        if(v.getTag(R.id.TAG_LIST_ITEM_DATA) == null)
            return;
        
        Log.i("ComputerCursorAdapter.ItemClickListener.onClick()", "prepare data for sending to RemoteActivity");
        
        int entryId = (Integer)v.getTag(R.id.TAG_LIST_ITEM_DATA);
        ComputerEntry entry = mComputerController.get(entryId);
        
        Intent remoteIntent = new Intent(getActivity(), RemoteActivity.class);
        
        Bundle extras = new Bundle();
        extras.putInt(getResources().getString(R.string.bundle_computer_id), entry.getId());
        extras.putString(getResources().getString(R.string.bundle_computer_name), entry.getName());
        extras.putString(getResources().getString(R.string.bundle_computer_ip), entry.getIP());
        extras.putString(getResources().getString(R.string.bundle_computer_idcode), entry.getIdentifyCode());
        
        remoteIntent.putExtras(extras);         
        
        getActivity().startActivity(remoteIntent);
    }
    
    List<View> findCheckedViews() {
        List<View> views = new Vector<View>();
        SparseBooleanArray checkedItemPositions = mListView.getCheckedItemPositions();
        int idx = 0;
        
        while(idx < mListView.getCount()) {
            boolean isChecked = checkedItemPositions != null ? checkedItemPositions.valueAt(idx) : false;
            
            if(isChecked) {
                int mItemPosition = checkedItemPositions.keyAt(idx);
                views.add(mListView.getChildAt(mItemPosition));
            }
            
            idx++;
        }
        
        return views;
    }
        
    public void updateList() {
        new Handler().post(new Runnable(){
            @Override
            public void run() {                                
                mListAdapter.changeCursor(getData());             
            }           
        });
    }
    
    abstract Cursor getData();
    
}
