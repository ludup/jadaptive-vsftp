package com.jadaptive.plugins.ssh.vsftp.commands;

import org.pf4j.Extension;

import com.jadaptive.api.permissions.AccessDeniedException;
import com.jadaptive.plugins.sshd.PluginCommandFactory;
import com.jadaptive.plugins.sshd.commands.AbstractAutowiredCommandFactory;
import com.sshtools.server.vsession.CommandFactory;
import com.sshtools.server.vsession.ShellCommand;

@Extension
public class VirtualFileCommandFactory extends AbstractAutowiredCommandFactory implements PluginCommandFactory {

	@Override
	public CommandFactory<ShellCommand> buildFactory() throws AccessDeniedException {
		tryCommand("mount", Mount.class, "vfolder.readWrite");
		tryCommand("umount", Unmount.class, "vfolder.readWrite");
		tryCommand("vfs", Vfs.class, "vfolder.readWrite");
		return this;
	}

}
