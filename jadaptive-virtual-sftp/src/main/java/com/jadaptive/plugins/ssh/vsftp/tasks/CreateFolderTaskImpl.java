package com.jadaptive.plugins.ssh.vsftp.tasks;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;

import org.pf4j.Extension;

import com.jadaptive.api.tasks.TaskResult;
import com.sshtools.common.files.AbstractFile;
import com.sshtools.common.permissions.PermissionDeniedException;
import com.sshtools.common.util.FileUtils;

@Extension
public class CreateFolderTaskImpl extends AbstractFileTaskImpl<CreateFolderTask> {

	public static final String RESOURCE_KEY = "createFolder";
	
	@Override
	public String getResourceKey() {
		return RESOURCE_KEY;
	}
	
	@Override
	public TaskResult doTask(CreateFolderTask task, String executionId) {
		
		String targetName = FileUtils.getFilename(task.getTarget().getFilename());
		
		feedbackService.info(executionId, AbstractFileTargetTask.BUNDLE, "creatingFolder.text", targetName);
		
		try {	
			AbstractFile parentFolder = resolveParent(
					task.getTarget().getLocation(), 
					task.getTarget().getFilename());
			AbstractFile file = parentFolder.resolveFile(targetName);
			
			if(file.exists() && task.getErrorIfExists()) {
				return new FileLocationResult(task.getTarget().getLocation(), task.getTarget().getFilename(),
						new FileAlreadyExistsException(task.getTarget().getFilename()));
			}
			
			if(file.createFolder()) {
				return new FileLocationResult(task.getTarget().getLocation(), task.getTarget().getFilename());
			}
			
			return new FileLocationResult(task.getTarget().getLocation(), task.getTarget().getFilename(),
					new IOException("The folder could not be created"));
		} catch(IOException | PermissionDeniedException e) {
			return new FileLocationResult(task.getTarget().getLocation(), task.getTarget().getFilename(), e);
		}
	}


}
