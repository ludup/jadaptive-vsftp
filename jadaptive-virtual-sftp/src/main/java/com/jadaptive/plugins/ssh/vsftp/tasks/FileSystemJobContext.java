package com.jadaptive.plugins.ssh.vsftp.tasks;

import java.io.File;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jadaptive.api.jobs.TaskRunnerContext;
import com.jadaptive.plugins.ssh.vsftp.VirtualFileService;
import com.sshtools.common.files.AbstractFileFactory;
import com.sshtools.common.files.direct.NioFileFactory.NioFileFactoryBuilder;

@Component
public class FileSystemJobContext implements TaskRunnerContext {

	ThreadLocal<AbstractFileFactory<?>> vfs = new ThreadLocal<>();
	ThreadLocal<AbstractFileFactory<?>> system = new ThreadLocal<>();
	
	@Autowired
	private VirtualFileService fileService; 
	
	@Override
	public void clearContext() {
		vfs.remove();
		system.remove();
	}

	@Override
	public void setupContext() {
		
		vfs.set(fileService.getFactory());

		system.set(NioFileFactoryBuilder.create()
				.withHome(File.listRoots()[0]).build());

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
