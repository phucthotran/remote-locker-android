package com.itexplore.remotelocker;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.itexplore.remotelocker.common.NetworkResolve;

public class MainActivity extends Activity implements OnNavigationListener {
    
    private ComputerListFragment mListFragment;
	private NetworkResolve mNetResolve;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		
		setContentView(R.layout.layout_main);
		    		
		SpinnerAdapter spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.action_list, android.R.layout.simple_spinner_dropdown_item);
		
		final ActionBar actionBar = getActionBar();
		
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		actionBar.setListNavigationCallbacks(spinnerAdapter, MainActivity.this);

		mNetResolve = new NetworkResolve(MainActivity.this.getApplicationContext());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {		
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		    
		    case R.id.action_scancomputer:
		    {
		        NetServiceChecker netServiceChecker = new NetServiceChecker();
		        netServiceChecker.execute();		        
		    }
	        break;
		
			case R.id.action_newcomputer:
				AddComputerDialogFragment addComputer = new AddComputerDialogFragment();
				addComputer.show(getFragmentManager(), AddComputerDialogFragment.class.getName());
				break;
				
			case R.id.action_refresh:
			    mListFragment.updateList();
				break;
				
		}
		
		return super.onOptionsItemSelected(item);
	}
	
    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        
        String[] actionList = getResources().getStringArray(R.array.action_list);
        String selectedAction = actionList[itemPosition];
        
        if(selectedAction.equalsIgnoreCase("local")) {
            mListFragment = new LocalFragment();            
        }
        else if(selectedAction.equalsIgnoreCase("remote")) {            
            mListFragment = new RemoteFragment();
        }
        
        ft.replace(R.id.fragment_content, mListFragment);
        ft.commit();
        
        return true;
    }
    
    public void updateListFragment() {
        Log.i("MainActivity.updateListFragment()", "Update list view item in fragment");
        
        mListFragment.updateList();
    }
    
    class NetServiceChecker extends AsyncTask<Void, Void, Void> {
        
        private String[] mAddress;
        private HashMap<CharSequence, String> mComputers;
        
        public HashMap<CharSequence, String> getComputers() {
            return this.mComputers;
        }
        
        public NetServiceChecker() {
            super();
            
            mComputers = new HashMap<CharSequence, String>();
        }

        @Override
        protected void onPreExecute() {
            setProgressBarIndeterminateVisibility(true);
        }
        
        @Override
        protected Void doInBackground(Void... params) {
            try {
                mAddress = mNetResolve.getLocalAddress(); 
            } catch (UnknownHostException e) {                
            } catch (IOException e) { }
                                    
            int idx = 0;
            
            while(idx < mAddress.length) {
                String tmpAddress = mAddress[idx];               
                
                synchronized(this) {
                    if(!(mNetResolve.serviceAvailable(tmpAddress))) {
                        idx++;
                        continue;
                    }
                }

                mComputers.put(mNetResolve.getHostName(), tmpAddress);                
                
                idx++;
            }
            
            return null;
        }
        
        @Override
        protected void onPostExecute(Void result) {
            setProgressBarIndeterminateVisibility(false);
            
            if(mComputers.size() == 0) {
                Toast.makeText(MainActivity.this, R.string.scan_computer_empty_msg, Toast.LENGTH_SHORT).show();
                return;
            }
            
            final CharSequence[] computerNames = new CharSequence[mComputers.size()];
            mComputers.keySet().toArray(computerNames);
            
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
            alertBuilder.setTitle(R.string.scan_computer_title)
                        .setItems(computerNames, new DialogInterface.OnClickListener() {                                
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int idx = 0;
                                String selectedName = "";
                                
                                while(idx < computerNames.length) {
                                    if(which == idx) {
                                        selectedName = computerNames[idx].toString();
                                        break;
                                    }
                                    
                                    idx++;
                                }
                                
                                dialog.dismiss();
                                
                                AddComputerDialogFragment addComputer = new AddComputerDialogFragment();
                                addComputer.setEntry(selectedName, mComputers.get(selectedName));
                                addComputer.show(getFragmentManager(), AddComputerDialogFragment.class.getName());
                            }
                        });             
            
            alertBuilder.create().show();
        }
        
    } 

}
