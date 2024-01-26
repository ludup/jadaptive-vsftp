package com.jadaptive.plugins.ssh.vsftp.tasks;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.pf4j.Extension;

import com.jadaptive.api.tasks.TaskResult;
import com.sshtools.common.files.AbstractFile;
import com.sshtools.common.permissions.PermissionDeniedException;
import com.sshtools.common.util.FileUtils;

@Extension
public class DeleteFolderTaskImpl extends AbstractFileTaskImpl<DeleteFolderTask> {

	public static final String RESOURCE_KEY = "deleteFolder";
	
	@Override
	public String getResourceKey() {
		return RESOURCE_KEY;
	}
	
	public String getIcon() {
		return "fa-folder-xmark";
	}
	
	@Override
	public TaskResult doTask(DeleteFolderTask task, String executionId) {
		
		String targetName = FileUtils.getFilename(task.getTarget().getFilename());
		
		feedbackService.info(executionId, AbstractFileTargetTask.BUNDLE, "deletingFolder.text", targetName);
		
		try {	
			AbstractFile parentFolder = resolveParent(
					task.getTarget().getLocation(), 
					task.getTarget().getFilename());
			AbstractFile file = parentFolder.resolveFile(targetName);
			
			if(file.exists()) {
				if(file.delete(task.getDeleteContents())) {
					return new FileLocationResult(task.getTarget().getLocation(), task.getTarget().getFilename());
				}
				
				return new FileLocationResult(task.getTarget().getLocation(), task.getTarget().getFilename(),
						new IOException("The folder could not be deleted"));
			} else {
				return new FileLocationResult(task.getTarget().getLocation(), task.getTarget().getFilename(),
						new FileNotFoundException("The folder does not exist"));
			}
			
		} catch(IOException | PermissionDeniedException e) {
			return new FileLocationResult(task.getTarget().getLocation(), task.getTarget().getFilename(), e);
		}
	}


}
