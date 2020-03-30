package com.jadaptive.plugins.ssh.vsftp.filters;

import com.sshtools.common.events.Event;
import com.sshtools.common.files.AbstractFile;
import com.sshtools.common.files.AbstractFileFactory;

public abstract class FilterFileFactory<T extends AbstractFile> implements AbstractFileFactory<T> {

	protected AbstractFileFactory<T> fileFactory;
	
	public FilterFileFactory(AbstractFileFactory<T> filteredFactory) {
		this.fileFactory = filteredFactory;
	}

	@Override
	public Event populateEvent(Event evt) {
		return fileFactory.populateEvent(evt);
	}
}
