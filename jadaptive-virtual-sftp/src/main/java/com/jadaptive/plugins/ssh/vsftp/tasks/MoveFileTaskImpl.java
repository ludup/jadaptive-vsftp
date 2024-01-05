package com.jadaptive.plugins.ssh.vsftp.tasks;

import org.pf4j.Extension;

@Extension
public class MoveFileTaskImpl extends FileTransferTaskImpl<MoveFileTask> {

	public static final String RESOURCE_KEY = "moveFile";
	
	@Override
	public String getResourceKey() {
		return RESOURCE_KEY;
	}

}
