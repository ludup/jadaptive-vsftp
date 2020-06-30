package com.jadaptive.plugins.ssh.vsftp.commands;

import java.util.List;

import org.jline.reader.Candidate;
import org.springframework.beans.factory.annotation.Autowired;

import com.jadaptive.plugins.ssh.vsftp.VirtualFileService;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.jadaptive.plugins.sshd.commands.AbstractRoleAssignmentCommand;
import com.sshtools.server.vsession.ShellCommand;
import com.sshtools.server.vsession.UsageException;
import com.sshtools.server.vsession.UsageHelper;

public class AssignMountRole extends AbstractRoleAssignmentCommand<VirtualFolder> {

	@Autowired
	private VirtualFileService fileService; 
	
	public AssignMountRole() {
		super("mount-assign-role", ShellCommand.SUBSYSTEM_FILESYSTEM,  
			UsageHelper.build("mount-assign-role [mount] <name> <name>"), 
				"Assign a mount to a role");
	}

	@Override
	protected void saveObject(VirtualFolder obj) {
		fileService.createOrUpdate(obj);
	}

	@Override
	protected VirtualFolder loadObject(String name) throws UsageException {
		if(!fileService.checkMountExists(name, getCurrentUser())) {
			throw new UsageException(String.format("%s is alredy mounted", name));
		}
		return fileService.getVirtualFolder(name);
	}

	@Override
	protected void loadCandidates(List<Candidate> candidates) {
		for(VirtualFolder mount : fileService.getVirtualFolders()) {
			candidates.add(new Candidate(mount.getMountPath()));
		}
	}

}
