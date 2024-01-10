package com.jadaptive.plugins.ssh.vsftp.tasks;

import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.ObjectField;
import com.jadaptive.api.template.ObjectView;
import com.jadaptive.api.template.ObjectViewDefinition;

@ObjectViewDefinition(value = AbstractFileTransferTask.OPTIONS_VIEW, weight = 10000)
public abstract class AbstractFileTransferTask extends AbstractFileSourceTargetTask {

	private static final long serialVersionUID = 1813559364987803360L;

	public static final String OPTIONS_VIEW = "optionsView";
	
	@ObjectField(type = FieldType.BOOL, defaultValue = "false")
	@ObjectView(OPTIONS_VIEW)
	Boolean appendContents;

	@ObjectField(type = FieldType.INTEGER, defaultValue = "32768")
	@ObjectView(OPTIONS_VIEW)
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
