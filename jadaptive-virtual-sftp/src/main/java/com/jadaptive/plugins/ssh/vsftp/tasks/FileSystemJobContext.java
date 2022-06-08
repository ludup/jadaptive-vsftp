package com.jadaptive.plugins.ssh.vsftp.tasks;

import java.io.File;
import java.io.IOException;

import org.springframework.stereotype.Component;

import com.jadaptive.api.jobs.JobRunnerContext;
import com.sshtools.common.files.AbstractFileFactory;
import com.sshtools.common.files.direct.DirectFileFactory;
import com.sshtools.common.files.vfs.VFSFileFactory;
import com.sshtools.common.files.vfs.VirtualFileFactory;
import com.sshtools.common.files.vfs.VirtualMountTemplate;
import com.sshtools.common.permissions.PermissionDeniedException;

@Component
public class FileSystemJobContext implements JobRunnerContext {

	ThreadLocal<AbstractFileFactory<?>> vfs = new ThreadLocal<>();
	ThreadLocal<AbstractFileFactory<?>> system = new ThreadLocal<>();
	
	@Override
	public void clearContext() {
		vfs.remove();
		system.remove();
	}

	@Override
	public void setupContext() {
		
		try {
			vfs.set(new VirtualFileFactory(
					new VirtualMountTemplate("/", "tmp:///",
							new VFSFileFactory("tmp:///"), true)));
		} catch (IOException | PermissionDeniedException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}

		system.set(new DirectFileFactory(new File(".")));

	}

	public AbstractFileFactory<?> getFileSystem(FileLocation target) {
		switch(target) {
		case SYSTEM_PATH:
			return system.get();
		case VIRTUAL_PATH:
		default:
			return vfs.get();
		}
	}

	
}
