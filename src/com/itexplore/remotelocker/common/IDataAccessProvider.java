package com.itexplore.remotelocker.common;

public interface IDataAccessProvider {
	
	int insert() throws Exception;
	boolean update() throws Exception;
	boolean delete() throws Exception;
	
}
