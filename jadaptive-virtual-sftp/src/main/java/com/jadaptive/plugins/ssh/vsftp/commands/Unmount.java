package com.jadaptive.plugins.ssh.vsftp.commands;

import java.io.IOException;

import com.sshtools.common.permissions.PermissionDeniedException;
import com.sshtools.server.vsession.UsageException;
import com.sshtools.server.vsession.VirtualConsole;

public class Unmount extends AbstractVFSCommand {

	public Unmount() {
		super("umount", "VFS", "", "");
	}

	@Override
	protected void doRun(String[] args, VirtualConsole console)
			throws IOException, PermissionDeniedException, UsageException {
		
		
		
		
		
	}

}
