package com.jadaptive.plugins.ssh.vsftp.ui;

import com.jadaptive.api.events.UUIDEntityEvent;
import com.jadaptive.api.repository.UUIDEntity;

public class FileDownloadedEvent extends UUIDEntityEvent<UUIDEntity> {

	private static final long serialVersionUID = -2044630063808224880L;

	public FileDownloadedEvent(TransferResult result) {
		super("fileDownloaded.event", "files", result);
	}

}
