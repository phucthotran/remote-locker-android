package com.itexplore.remotelocker.communication;

public interface ICommandSender {
    
    String getCommand();
    String getInput();
    
	void setProvider(ICommunicationProvider provider);	
	void send(String commandType, String input);
	void asyncSend(String commandType, String input);
	
}
