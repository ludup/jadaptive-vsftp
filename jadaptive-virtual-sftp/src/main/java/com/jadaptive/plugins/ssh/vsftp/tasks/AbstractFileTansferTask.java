package com.jadaptive.plugins.ssh.vsftp.tasks;

import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.ObjectField;

public abstract class AbstractFileTansferTask extends AbstractFileSourceTargetTask {

	private static final long serialVersionUID = 1813559364987803360L;

	@ObjectField(type = FieldType.BOOL, defaultValue = "false")
	Boolean appendContents;

	@ObjectField(type = FieldType.INTEGER, defaultValue = "32768")
	Integer blockSize;
	
	public Boolean getAppendContents() {
		return appendContents;
	}

	public void setAppendContents(Boolean appendContents) {
		this.appendContents = appendContents;
	}

	public Integer getBlockSize() {
		return blockSize;
	}

	public void setBlockSize(Integer blockSize) {
		this.blockSize = blockSize;
	}
	
	
		
}
