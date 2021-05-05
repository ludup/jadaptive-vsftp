package com.jadaptive.plugins.ssh.vsftp.commands;

import java.util.List;

import org.jline.reader.Candidate;
import org.springframework.beans.factory.annotation.Autowired;

import com.jadaptive.plugins.ssh.vsftp.VirtualFileService;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.jadaptive.plugins.sshd.commands.AbstractUserAssignmentCommand;
import com.sshtools.server.vsession.ShellCommand;
import com.sshtools.server.vsession.UsageException;
import com.sshtools.server.vsession.UsageHelper;

public class AssignMountUser extends AbstractUserAssignmentCommand<VirtualFolder> {

	@Autowired
	private VirtualFileService fileService;  

	
	public AssignMountUser() {
		super("mount-assign-user", ShellCommand.SUBSYSTEM_FILESYSTEM,  UsageHelper.build("mount-assign-user [mount] <name> <name>"), 
				"Assign a mount to a user");
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
		for(VirtualFolder mount : fileService.allObjects()) {
			candidates.add(new Candidate(mount.getMountPath()));
		}
	}

}
