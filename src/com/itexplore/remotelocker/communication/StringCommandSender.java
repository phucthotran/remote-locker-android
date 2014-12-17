package com.itexplore.remotelocker.communication;

import android.util.Log;

public class StringCommandSender implements ICommandSender {

	private String mCommandType;
	private String mInput;
	private ICommunicationProvider mProvider;
	
	@Override
	public String getCommand() {		
		return this.mCommandType;
	}

	@Override
	public String getInput() {
		return this.mInput;
	}
	
	@Override
	public void send(String commandType, String input) {
	    this.mCommandType = commandType;
	    this.mInput = input;
	    
	    Log.i("StringCommandSender.send()", "prepare to sending command");
        
        if(this.mProvider != null) {
            this.mProvider.sendCommand();
            
            Log.i("StringCommandSender.send()", "command sent");
        }
	}

	@Override
	public void asyncSend(String commandType, String input) {
		this.mCommandType = commandType;
		this.mInput = input;
		
		Log.i("StringCommandSender.asyncSend()", "prepare to sending command");
		
		if(this.mProvider != null) {
			this.mProvider.asyncSendCommand();
			
			Log.i("StringCommandSender.asyncSend()", "command sent");
		}
	}

	@Override
	public void setProvider(ICommunicationProvider provider) {
		this.mProvider = provider;
		this.mProvider.setCommandSender(this);
	}

}
