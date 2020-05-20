package com.jadaptive.plugins.ssh.vsftp.commands;

import java.util.Set;

import com.jadaptive.api.role.Role;
import com.jadaptive.api.user.User;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.sshtools.server.vsession.ShellCommand;
import com.sshtools.server.vsession.UsageHelper;

public class UnassignMount extends AbstractMountCommand {

	public UnassignMount() {
		super("unassign-mount", ShellCommand.SUBSYSTEM_FILESYSTEM,  UsageHelper.build("unassign-mount [mount] [-r|-u] <name> <name>",
				"-r, --roles <names>					A list of role names to assign to",
				"-u, --users <names>					A list of user names to assign to"), 
				"Unassign a mount from a role or user");
	}

	@Override
	protected void doUpdate(VirtualFolder folder, Set<Role> roles, Set<User> users) {

		for(Role role : roles) {
			folder.getRoles().remove(role.getUuid());
		}
		
		for(User user : users) {
			folder.getUsers().remove(user.getUuid());
		}
	}
}
