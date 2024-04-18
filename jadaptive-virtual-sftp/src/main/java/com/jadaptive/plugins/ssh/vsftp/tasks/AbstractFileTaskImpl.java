package com.jadaptive.plugins.ssh.vsftp.tasks;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;

import com.jadaptive.api.tasks.FeedbackService;
import com.jadaptive.api.tasks.Task;
import com.jadaptive.api.tasks.TaskImpl;
import com.sshtools.common.files.AbstractFile;
import com.sshtools.common.files.AbstractFileFactory;
import com.sshtools.common.permissions.PermissionDeniedException;
import com.sshtools.common.util.FileUtils;

public abstract class AbstractFileTaskImpl<T extends Task> implements TaskImpl<T> {

	@Autowired
	private FileSystemJobContext fsContext;
	
	@Autowired
	protected FeedbackService feedbackService; 
	
	
	protected AbstractFile resolveFile(SourceLocation location, String filename)
			throws PermissionDeniedException, IOException {
		AbstractFileFactory<?> fileFactory = fsContext.getFileSystem(location);
		return fileFactory.getFile(filename);
	}
	
	protected AbstractFile resolveFile(TargetLocation location, String filename)
			throws PermissionDeniedException, IOException {
		AbstractFileFactory<?> fileFactory = fsContext.getFileSystem(location);
		return fileFactory.getFile(filename);
	}

	protected AbstractFile resolveParent(SourceLocation location, String filename) throws PermissionDeniedException, IOException {
		String parent = FileUtils.getParentPath(filename);
		return resolveFile(location, parent);
	}
	
	protected AbstractFile resolveParent(TargetLocation location, String filename) throws PermissionDeniedException, IOException {
		String parent = FileUtils.getParentPath(filename);
		return resolveFile(location, parent);
	}
	
	protected AbstractFileFactory<?> getFileFactory(SourceLocation location) {
		return fsContext.getFileSystem(location);
	}
	
	protected AbstractFileFactory<?> getFileFactory(TargetLocation location) {
		return fsContext.getFileSystem(location);
	}
	
	public String getBundle() {
		return AbstractFileTargetTask.BUNDLE;
	}
	
}
