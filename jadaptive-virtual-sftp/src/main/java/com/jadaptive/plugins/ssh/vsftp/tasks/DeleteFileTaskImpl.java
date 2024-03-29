package com.jadaptive.plugins.ssh.vsftp.tasks;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.pf4j.Extension;

import com.jadaptive.api.tasks.TaskResult;
import com.sshtools.common.files.AbstractFile;
import com.sshtools.common.permissions.PermissionDeniedException;
import com.sshtools.common.util.FileUtils;

@Extension
public class DeleteFileTaskImpl extends AbstractFileTaskImpl<DeleteFileTask> {

	public static final String RESOURCE_KEY = "deleteFile";
	
	@Override
	public String getResourceKey() {
		return RESOURCE_KEY;
	}
	
	@Override
	public TaskResult doTask(DeleteFileTask task) {
		
		String targetName = FileUtils.getFilename(task.getTarget().getFilename());
		try {	
			AbstractFile parentFolder = resolveParent(
					task.getTarget().getLocation(), 
					task.getTarget().getFilename());
			AbstractFile file = parentFolder.resolveFile(targetName);
			
			if(file.exists()) {
				if(file.delete(false)) {
					return new DeleteFileTaskResult(task.getTarget().getFilename());
				}
				return new DeleteFileTaskResult(task.getTarget().getFilename(),
						new IOException("The file could not be deleted"));
			} else {
				return new DeleteFileTaskResult(task.getTarget().getFilename(),
						new FileNotFoundException(task.getTarget().getFilename()));
			}
			
			
		} catch(IOException | PermissionDeniedException e) {
			return new DeleteFileTaskResult(task.getTarget().getFilename(), e);
		}
	}

}
