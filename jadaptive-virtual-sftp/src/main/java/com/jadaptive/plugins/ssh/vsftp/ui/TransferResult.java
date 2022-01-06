package com.jadaptive.plugins.ssh.vsftp.ui;

import java.util.Date;

import com.jadaptive.api.repository.UUIDEntity;

public class TransferResult extends UUIDEntity {

	private static final long serialVersionUID = 7395109201689299682L;

	String filename;
	
	String path;
	
	Date started;
	
	Date ended;
	
	String contentHash;
	
	long size;
	
	public TransferResult(String filename, String path, long size, long started, long ended, byte[] digest) {
		
	}

	@Override
	public String getResourceKey() {
		return "transferResult";
	}

}
