package com.jadaptive.plugins.ssh.vsftp.commands;

import com.jadaptive.plugins.sshd.commands.AbstractTenantAwareCommand;

public abstract class AbstractVFSCommand extends AbstractTenantAwareCommand {

	public AbstractVFSCommand(String name, String subsystem, String signature, String description) {
		super(name, subsystem, signature, description);
	}
}
