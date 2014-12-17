package com.itexplore.remotelocker;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.itexplore.remotelocker.common.ComputerEntry;
import com.itexplore.remotelocker.communication.Command;
import com.itexplore.remotelocker.communication.ICommandSender;
import com.itexplore.remotelocker.communication.ICommunicationProvider;
import com.itexplore.remotelocker.communication.InvokeCommand;
import com.itexplore.remotelocker.communication.StringCommandSender;
import com.itexplore.remotelocker.communication.TcpCommunication;

import java.io.IOException;

public class RemoteActivity extends Activity implements OnClickListener {
	
	private ComputerEntry mEntry;
	private ICommunicationProvider mCommunicationProvider;
	private ICommandSender mCommandSender;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.layout_remote);
		
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		Button btnUnlock = (Button)findViewById(R.id.remote_btn_unlock);
		Button btnLock = (Button)findViewById(R.id.remote_btn_lock);
		Button btnAppClose = (Button)findViewById(R.id.remote_btn_app_close);
		Button btnShutdown = (Button)findViewById(R.id.remote_btn_shutdown);
		
		btnUnlock.setOnClickListener(this);
		btnLock.setOnClickListener(this);
		btnAppClose.setOnClickListener(this);
		btnShutdown.setOnClickListener(this);
		
		TextView tvComputerName = (TextView)findViewById(R.id.remote_tv_computer_name);
		TextView tvComputerIP = (TextView)findViewById(R.id.remote_tv_computer_ip);
		
		Bundle extras = null;
		
		if(getIntent().getExtras() != null)
			extras = getIntent().getExtras();
		else
			extras = savedInstanceState.getBundle(getString(R.string.bundle_computer));
		 
		mEntry = getEntry(extras);
		
		tvComputerName.setText(mEntry.getName());
		tvComputerIP.setText(mEntry.getIP());
	}
	
	@Override
	protected void onStart() {
		super.onStart();

		connectionEstablish();	
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		releaseResource();		
	}

	@Override
	protected void onStop() {		
		super.onStop();
		
		releaseResource();
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {		
		super.onRestoreInstanceState(savedInstanceState);
		
		Bundle extras = savedInstanceState.getBundle(getString(R.string.bundle_computer));
		mEntry = getEntry(extras);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {		
		Bundle extras = new Bundle();
		extras.putString(getString(R.string.bundle_computer_name), mEntry.getName());
		extras.putString(getString(R.string.bundle_computer_ip), mEntry.getIP());
		
		outState.putBundle(getString(R.string.bundle_computer), extras);
		
		super.onSaveInstanceState(outState);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.remote_menu, menu);		
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		
			case android.R.id.home:
				NavUtils.navigateUpFromSameTask(this);
				break;
				
			case R.id.action_reconnect_computer:
			    if(((TcpCommunication)mCommunicationProvider).isConnected()) {
		            mCommandSender.asyncSend(Command.DISCONNECT, null);
		            
		            try {
                        mCommunicationProvider.stop();
                    } catch (Exception e) {                        
                        Log.w("RemoteActivity.onOptionsItemSelected()", "Error: " + e.getMessage());
                        Log.e("RemoteActivity.onOptionsItemSelected()", e.getStackTrace().toString());
                    }
			    }
			    
			    connectionEstablish();
				break;

		}		
		
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {		
				
			case R.id.remote_btn_unlock:
				mCommandSender.asyncSend(Command.UNLOCK, null);
				break;
				
			case R.id.remote_btn_lock:
				mCommandSender.asyncSend(Command.LOCK, null);
				break;
				
			case R.id.remote_btn_app_close:
				mCommandSender.asyncSend(Command.APP_CLOSE, null);
				RemoteActivity.this.finish();
				break;
				
			case R.id.remote_btn_shutdown:
				mCommandSender.asyncSend(Command.SHUTDOWN, null);
				RemoteActivity.this.finish();
				break;
		
		}
	}

	void connectionEstablish() {
	    Log.i("RemoteActivity.connectionEstablish()", "RemoteLocker server connecting");
	    
		//Restore from onStop()
		if(mEntry == null)
			mEntry = getEntry(getIntent().getExtras());
		
		mCommunicationProvider = new TcpCommunication(mEntry.getIP(), Integer.parseInt(getString(R.string.remotelocker_server_port)));
		mCommunicationProvider.setCommunicationObject(this);
		
		mCommandSender = new StringCommandSender();
		mCommandSender.setProvider(mCommunicationProvider);

		//Thread for Networking
		new Thread(new Runnable(){
			@Override
			public void run() {
			    android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
			    
				try {
				    Log.i("RemoteActivity.connectionEstablish()", "start connect");
				    
					mCommunicationProvider.start();
				} catch (IllegalArgumentException e) {					
				    Log.w("RemoteActivity.connectionEstablish()", "Error: " + e.getMessage());
				    Log.w("RemoteActivity.connectionEstablish()", e.getStackTrace().toString());
				    
					RemoteActivity.this.runOnUiThread(new Runnable(){
						@Override
						public void run() {									
							Toast.makeText(RemoteActivity.this, getString(R.string.remote_network_cannot_resolve_host_name), Toast.LENGTH_LONG).show();							
						}
					});
				} catch (IOException e) {
				    Log.w("RemoteActivity.connectionEstablish()", "Error: " + e.getMessage());
                    Log.w("RemoteActivity.connectionEstablish()", e.getStackTrace().toString());				    
				    
					RemoteActivity.this.runOnUiThread(new Runnable(){
						@Override
						public void run() {
							if(mEntry != null)
								Toast.makeText(RemoteActivity.this, String.format(getString(R.string.remote_network_cannot_connect), mEntry.getName()), Toast.LENGTH_LONG).show();
						}
					});
				} catch (Exception e) {
				    Log.w("RemoteActivity.connectionEstablish()", "Error: " + e.getMessage());
                    Log.w("RemoteActivity.connectionEstablish()", e.getStackTrace().toString());
				    
					RemoteActivity.this.runOnUiThread(new Runnable(){
						@Override
						public void run() {							
							Toast.makeText(RemoteActivity.this, "Unknown error!", Toast.LENGTH_LONG).show();
						}
					});
				}
				
				if(((TcpCommunication)mCommunicationProvider).isConnected()) {
				    Log.i("RemoteActivity.connectionEstablish()", "Authenticate with RemoteLocker server");
				    
                    //Authentication between client and server                  
                    mCommandSender.asyncSend(Command.AUTHENTICATION, mEntry.getIdentifyCode());
                }
			}
		}).start();
	}
	
	void releaseResource() {
		if(mCommandSender != null) {
			mCommandSender.asyncSend(Command.DISCONNECT, null);
			mCommandSender = null;
		}
		
		if(mCommunicationProvider != null) {
			try {
				mCommunicationProvider.stop();
			} catch (IOException e) {				
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			mCommunicationProvider = null;
		}

		mEntry = null;
	}
	
	ComputerEntry getEntry(Bundle extras) {
		String name = extras.getString(this.getString(R.string.bundle_computer_name));
		String ip = extras.getString(this.getString(R.string.bundle_computer_ip));
		String identifyCode = extras.getString(this.getString(R.string.bundle_computer_idcode));
		
		return new ComputerEntry(0, name, ip, identifyCode);
	}

	//COMMAND PROCESS
	
	@InvokeCommand(Command.REJECT)
	public void reject() {
	    Log.i("RemoteActivity.reject()", "Connection has been rejected");
	    
		RemoteActivity.this.runOnUiThread(new Runnable(){
			@Override
			public void run() {
				Toast.makeText(getApplicationContext(), String.format(getString(R.string.remote_reject_login), mEntry.getName()), Toast.LENGTH_LONG).show();
			}
		});
		RemoteActivity.this.finish();
	}
	
	@InvokeCommand(Command.UNLOCK_REQUEST)
	public void unlockRequest() {
	    Log.i("RemoteActivity.reject()", "Unlock request comming");
	    
		RemoteActivity.this.runOnUiThread(new Runnable(){
			@Override
			public void run() {
				AlertDialog.Builder alertBuilder = new AlertDialog.Builder(RemoteActivity.this);
				
				alertBuilder.setTitle(R.string.unlock_request_dialog_title)
				        .setMessage(R.string.unlock_request_dialog_msg);
				
				alertBuilder.setPositiveButton(R.string.unlock_request_dialog_ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					    Log.i("RemoteActivity.reject()", "Send an unlock command to sever");
					    
						mCommandSender.asyncSend(Command.UNLOCK, null);
					}
				});
				
				alertBuilder.setNegativeButton(R.string.unlock_request_dialog_cancel, new DialogInterface.OnClickListener() {					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();						
					}
				});
				
				AlertDialog alert = alertBuilder.create();
				alert.show();
			}
		});
	}
	
}
