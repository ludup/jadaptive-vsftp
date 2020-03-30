package com.jadaptive.plugins.ssh.vsftp.tasks;

import com.jadaptive.api.tasks.Task;
import com.jadaptive.api.tasks.TaskImpl;
import com.sshtools.common.files.AbstractFileFactory;

public abstract class FileSystemAwareTaskImpl<T extends Task> implements TaskImpl<T> {

	ThreadLocal<AbstractFileFactory<?>> vfs = new ThreadLocal<>();
	ThreadLocal<AbstractFileFactory<?>> systemFs = new ThreadLocal<>();
	
 	private AbstractFileFactory<?> getVFS() {
		return vfs.get();
	}
 	
 	private AbstractFileFactory<?> getSystemFS() {
 		return systemFs.get();
 	}
 	
 	protected AbstractFileFactory<?> getFileSystem(FileLocation location) {
 		switch(location) {
 		case VIRTUAL_PATH:
 			return getVFS();
 		default:
 			return getSystemFS();
 		}
 	}
 	
}
