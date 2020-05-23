package com.jadaptive.plugins.ssh.vsftp.commands;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.jadaptive.api.entity.ObjectException;
import com.jadaptive.api.entity.ObjectNotFoundException;
import com.jadaptive.api.role.Role;
import com.jadaptive.api.role.RoleService;
import com.jadaptive.api.user.User;
import com.jadaptive.api.user.UserService;
import com.jadaptive.plugins.ssh.vsftp.VirtualFileService;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.jadaptive.utils.FileUtils;
import com.sshtools.common.permissions.PermissionDeniedException;
import com.sshtools.server.vsession.CliHelper;
import com.sshtools.server.vsession.UsageException;
import com.sshtools.server.vsession.VirtualConsole;

public abstract class AbstractMountCommand extends AbstractVFSCommand {

	public AbstractMountCommand(String name, String subsystem, String signature, String description) {
		super(name, subsystem, signature, description);
	}

	@Autowired
	private VirtualFileService fileService; 

	@Autowired
	private RoleService roleService; 
	
	@Autowired
	private UserService userService;  

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
		String tmp = CliHelper.getValue(args, 'r', "roles", "");
		if(StringUtils.isNotBlank(tmp)) {
			for(String name : tmp.split(",")) {
				try {
				roles.add(roleService.getRoleByName(name));
				} catch(ObjectNotFoundException e) { 
					throw new UsageException(String.format("%s is not a valid role name", name));
				} 
			}
		}
		
		Set<User> users = new HashSet<>();
		tmp = CliHelper.getValue(args, 'u', "users", "");
		if(StringUtils.isNotBlank(tmp)) {
			for(String name : tmp.split(",")) {
				try {
				users.add(userService.getUser(name));
				} catch(ObjectNotFoundException e) { 
					throw new UsageException(String.format("%s is not a valid user name", name));
				} 
			}
		}


		try {
			
			VirtualFolder folder = fileService.getVirtualFolder(mount);
			
			doUpdate(folder, roles, users);
			
			fileService.createOrUpdate(folder);
			
		} catch (ObjectException e) {
			throw new IOException(e.getMessage(), e);
		}
	}



	protected abstract void doUpdate(VirtualFolder folder, Set<Role> roles, Set<User> users);

}
