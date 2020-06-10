package com.jadaptive.plugins.ssh.vsftp.commands;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jline.reader.Candidate;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;
import org.springframework.beans.factory.annotation.Autowired;

import com.jadaptive.api.entity.ObjectException;
import com.jadaptive.api.role.Role;
import com.jadaptive.api.role.RoleService;
import com.jadaptive.api.user.User;
import com.jadaptive.plugins.ssh.vsftp.VirtualFileService;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.jadaptive.utils.FileUtils;
import com.sshtools.common.permissions.PermissionDeniedException;
import com.sshtools.server.vsession.ShellCommand;
import com.sshtools.server.vsession.UsageException;
import com.sshtools.server.vsession.UsageHelper;
import com.sshtools.server.vsession.VirtualConsole;

public class AssignMountRole extends AbstractVFSCommand {

	@Autowired
	private VirtualFileService fileService; 
	
	@Autowired
	private RoleService roleService;  

	
	public AssignMountRole() {
		super("mount-assign-role", ShellCommand.SUBSYSTEM_FILESYSTEM,  UsageHelper.build("mount-assign-role [mount] <name> <name>"), 
				"Assign a mount to a role");
	}

	@Override
	protected void doRun(String[] args, VirtualConsole console)
			throws IOException, PermissionDeniedException, UsageException {
		
		if(args.length < 4) {
			throw new UsageException("Not enough arguments provided");
		}
		
		String mount = FileUtils.checkStartsWithSlash(
				FileUtils.checkEndsWithNoSlash(args[1]));
		
		if(!fileService.checkMountExists(mount, currentUser)) {
			throw new UsageException(String.format("%s is alredy mounted", mount));
		}
	
		
		Set<Role> roles = new HashSet<>();
		
		for(int i=2;i<args.length;i++) {
			roles.add(roleService.getRoleByName(args[i]));
		}

		try {
			
			VirtualFolder folder = fileService.getVirtualFolder(mount);
			fileService.createOrUpdate(folder);
			
		} catch (ObjectException e) {
			throw new IOException(e.getMessage(), e);
		}
	}

	@Override
	public void complete(LineReader reader, ParsedLine line, List<Candidate> candidates) {
		if(line.wordIndex() == 1) {
			for(VirtualFolder mount : fileService.getVirtualFolders()) {
				candidates.add(new Candidate(mount.getMountPath()));
			}
		} else if(line.wordIndex() > 1) {
			for(Role role : roleService.allRoles()) {
				candidates.add(new Candidate(role.getName()));
			}
		}
	}

}
