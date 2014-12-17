package com.itexplore.remotelocker.communication;

public interface ICommunicationProvider {

	void setCommandSender(ICommandSender sender);
	void setCommunicationObject(Object communicationObject);
	
	void sendCommand();
	void asyncSendCommand();
	void execute(Command commandType, String input);
	
	void start() throws Exception;
	void stop() throws Exception;
	
}
