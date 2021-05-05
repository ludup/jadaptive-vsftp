package com.jadaptive.plugins.ssh.vsftp;

import java.io.IOException;

import com.jadaptive.plugins.sshd.SSHInterface;
import com.sshtools.common.ssh.SshException;
import com.sshtools.server.SshServerContext;
import com.sshtools.synergy.nio.SshEngine;

public class VirtualSFTPContext extends SshServerContext {

	SSHInterface intf;
	public VirtualSFTPContext(SshEngine engine, SSHInterface intf) throws IOException, SshException {
		super(engine);
		this.intf = intf;
	}
	
	public SSHInterface getInterface() {
		return intf;
	}

}
