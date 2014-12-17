package com.itexplore.remotelocker.communication;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class TcpCommunication implements ICommunicationProvider {
    
    private final int CONNECTION_TIME_OUT = 5 * 1000; // 5s

	private boolean mConnected;
	private String mIp;
	private int mPort;
	private Socket mSocket;
	
	private OutputStream mOutput;
	private InputStream mInput;
	
	private BackgroundWorker mBackgroundWorker;
	private AsyncReceiveCommand mReceiveCommand;
	
	private ICommandSender mCommmandSender;
	private Object mCommunicationObject;
	
	@Override
	public void setCommandSender(ICommandSender commandSender) {
		this.mCommmandSender = commandSender;
	}
	
	@Override
	public void setCommunicationObject(Object communicationObject) {
		this.mCommunicationObject = communicationObject;
	}
	
	public boolean isConnected() {
		return this.mConnected;
	}
	
	public TcpCommunication(String ip, int port) {
		this.mIp = ip;
		this.mPort = port;
	}
	
	@Override
	public void start() throws IllegalArgumentException, IOException {		
		mSocket = new Socket();
		mSocket.connect(new InetSocketAddress(mIp, mPort), CONNECTION_TIME_OUT);
		
		mConnected = true;
		
		mOutput = mSocket.getOutputStream();
		mInput = mSocket.getInputStream();
				
		//Keep tcp/ip working in background
		mBackgroundWorker = new BackgroundWorker();
		mBackgroundWorker.start();
		
		//Receive command from server
		mReceiveCommand = new AsyncReceiveCommand();		
		mReceiveCommand.start();
	}
	
	@Override
	public void stop() throws IOException {
		if(mBackgroundWorker != null) {
		    mBackgroundWorker.stopWorking();
		    mBackgroundWorker = null;
		}
		
		if(mReceiveCommand != null) {
    		mReceiveCommand.stopReceive();
    		mReceiveCommand = null;
		}
		
		mConnected = false;
		
		if(mOutput != null) { 
		    mOutput.close();
		    mOutput = null;
		}
		if(mSocket != null) {
		    mSocket.close();
		    mSocket = null;
		}
	}
	
	public void sendCommand() {
	    if(!mConnected)
            return;
        
        Log.i("TcpCommunication.sendCommand()", "create command packet");

        Command command = new Command(mCommmandSender.getCommand());
        String input = mCommmandSender.getInput();
        
        CommandPacket commandPacket = new CommandPacket(command, input);        
                
        Log.i("TcpCommunication.sendCommand()", "command sending");
        
        try {
            mOutput.write(commandPacket.toBytes());         
        } catch (IOException e) {
            Log.w("TcpCommunication.sendCommand()", "Error: " + e.getMessage());
            Log.e("TcpCommunication.sendCommand()", e.getStackTrace().toString());
        } catch (Exception e) {
            Log.w("TcpCommunication.sendCommand()", "Error: " + e.getMessage());
            Log.e("TcpCommunication.sendCommand()", e.getStackTrace().toString());
        }
        
        //Waiting for result
        int receivedByte = 0;
        
        while(receivedByte == 0) {
            try {
                receivedByte = mInput.available();
            } catch (IOException e) {                   
                e.printStackTrace();
            }
            
            if(receivedByte > 0) {
                byte[] data = new byte[receivedByte];
                
                Log.i("TcpCommunication.sendCommand()", "fetch command");
                
                try {
                    mInput.read(data, 0, data.length);
                } catch (IOException e) {                        
                    e.printStackTrace();
                }
                
                Log.i("TcpCommunication.sendCommand()", "execute command");                    
                
                commandPacket = new CommandPacket(data, receivedByte);
                execute(commandPacket.getCommand(), commandPacket.getInput());
                
                Log.i("TcpCommunication.sendCommand()", "execute command completed");                                    
            }
        }
	}

	@Override
	public void asyncSendCommand() {	    
	    if(!mConnected)
	        return;
	    
	    Log.i("TcpCommunication.asyncSendCommand()", "create command packet");

	    Command command = new Command(mCommmandSender.getCommand());
	    String input = mCommmandSender.getInput();
	    
		CommandPacket commandPacket = new CommandPacket(command, input);
				
		Log.i("TcpCommunication.asyncSendCommand()", "command sending");
		
		try {
			mOutput.write(commandPacket.toBytes());
		} catch (IOException e) {
		    Log.w("TcpCommunication.asyncSendCommand()", "Error: " + e.getMessage());
            Log.e("TcpCommunication.asyncSendCommand()", e.getStackTrace().toString());
		} catch (Exception e) {
		    Log.w("TcpCommunication.asyncSendCommand()", "Error: " + e.getMessage());
            Log.e("TcpCommunication.asyncSendCommand()", e.getStackTrace().toString());
		}
	}
	
	@Override
	public synchronized void execute(Command commandType, String input) {
		CommandResolve.invoke(mCommunicationObject, commandType, input);
	}
	
	class AsyncReceiveCommand extends Thread {

		private boolean mAvailable = true;
		
		public boolean canReceive() {
			return this.mAvailable;
		}
		
		public void stopReceive() {
			this.mAvailable = false;
		}
		
		@Override
		public void run() {
		    android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
		    
			while(mAvailable && mConnected) {
				int receivedByte = 0;
				
				try {
					receivedByte = mInput.available();				
				} catch (IOException e) {					
					e.printStackTrace();
				}
				
				if(receivedByte > 0) {
                    byte[] data = new byte[receivedByte];
                    
                    Log.i("TcpCommunication.ReceiveCommandTask.run()", "fetch command");
                    
                    try {
                        mInput.read(data, 0, data.length);
                    } catch (IOException e) {                        
                        e.printStackTrace();
                    }
                    
                    Log.i("TcpCommunication.ReceiveCommandTask.run()", "execute command");                    
                    
                    CommandPacket commandPacket = new CommandPacket(data, receivedByte);
                    execute(commandPacket.getCommand(), commandPacket.getInput());
                    
                    Log.i("TcpCommunication.ReceiveCommandTask.run()", "execute command completed");
                                        
                }
			}
		}
		
	}
	
	class BackgroundWorker extends Thread {

		private boolean mStart = true;
		
		public void startWorking() {
			this.mStart = true;
		}
		
		public void stopWorking() {
			this.mStart = false;
		}
		
		@Override
		public void run() {
		    android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
		    
			while(mStart) {}			
		}
		
	}
	
}
