package com.itexplore.remotelocker.common;

import java.util.List;
import java.util.Vector;

public class ComputerEntries {
	private List<ComputerEntry> mEntries;	

	public void setEntries(List<ComputerEntry> entries) {
		this.mEntries = entries;
	}
	
	public int size() {
		return this.mEntries.size();
	}
	
	public ComputerEntries() {
		this.mEntries = new Vector<ComputerEntry>();
	}
	
	public void add(ComputerEntry entry) {
		this.mEntries.add(entry);
	}
	
	public void remove(ComputerEntry entry) {
		this.mEntries.remove(entry);
	}
	
	public void removeAt(int position) {
		this.mEntries.remove(position);
	}
	
	public ComputerEntry get(int position) {
		return this.mEntries.get(position);
	}
	
	public ComputerEntry findName(String name) {
		for(ComputerEntry entry : mEntries) {
			if(entry.getName().equalsIgnoreCase(name))
				return entry;
		}
		
		return null;
	}
}
