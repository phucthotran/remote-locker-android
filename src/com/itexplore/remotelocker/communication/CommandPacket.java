package com.itexplore.remotelocker.communication;

public class CommandPacket {

	public static CommandPacket emptyPacket = new CommandPacket();
	
	private Command mCommand;
	private String mInput;
	
	public Command getCommand() {
		return this.mCommand;
	}
	
	public String getInput() {
		return this.mInput;
	}
	
	public CommandPacket() {
		this.mCommand = new Command(Command.NULL);
	}
	
	public CommandPacket(Command commandType) {
		this.mCommand = commandType;
		this.mInput = "";
	}
	
	public CommandPacket(Command commandType, String input) {
		this.mCommand = commandType;
		this.mInput = input;
		
		if(this.mInput == null)
			this.mInput = "";
	}
	
	public CommandPacket(byte[] encodeData, int receivedByte) {
		String packetData = new String(encodeData);
		
		this.mCommand = new Command(packetData.substring(0, 40));
		this.mInput = packetData.substring(40, receivedByte);
	}
	
	public byte[] toBytes() {
		byte[] commandData = this.mCommand.getValue().getBytes();
		byte[] inputData = this.mInput.getBytes();
		
		byte[] encodeData = new byte[commandData.length + inputData.length];
		
		int dataIdx = 0, commandIdx = 0, inputIdx = 0;
		
		while(dataIdx < encodeData.length) {
			while(commandIdx < commandData.length) {
				encodeData[dataIdx] = commandData[commandIdx];
				dataIdx++;
				commandIdx++;
			}
			
			while(inputIdx < inputData.length) {
				encodeData[dataIdx] = inputData[inputIdx];
				dataIdx++;
				inputIdx++;
			}
		}		
		
		return encodeData;
	}
	
}
