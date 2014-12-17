package com.itexplore.remotelocker.common;

public class ComputerEntry {

	public static final String TYPE_LOCAL = "local";
	public static final String TYPE_REMOTE = "remote";
	
	private int mId = 0;
	private String mName = "";
	private String mIp = "";
	private String mIdentifyCode = "";
	private String mType = TYPE_LOCAL;
	
	public void setId(int id) {
		this.mId = id;
	}
	
	public int getId() {
		return this.mId;
	}
	
	public void setName(String name) {
		this.mName = name;
		
		if(this.mName == null)
			this.mName = "";
	}
	
	public String getName() {
		return this.mName;
	}
	
	public void setIP(String ip) {
		this.mIp = ip;
		
		if(this.mIp == null)
			this.mIp = "";
	}
	
	public String getIP() {
		return this.mIp;
	}
	
	public void setIdentifyCode(String identifyCode) {
		this.mIdentifyCode = identifyCode;
		
		if(this.mIdentifyCode == null)
			this.mIdentifyCode = "";
	}
	
	public String getIdentifyCode() {
		return this.mIdentifyCode;
	}
	
	public void setType(String type) {
		this.mType = type;
		
		if(this.mType == null)
			this.mType = TYPE_LOCAL;
	}
	
	public String getType() {
		return this.mType;
	}
	
	public ComputerEntry(){ }
	
	public ComputerEntry(String name, String ip, String identifyCode) {
		this.mName = name;
		this.mIp = ip;
		this.mIdentifyCode = identifyCode;
	}
	
	public ComputerEntry(int id, String name, String ip, String identifyCode) {
		this.mId = id;
		this.mName = name;
		this.mIp = ip;
		this.mIdentifyCode = identifyCode;
	}
	
}
