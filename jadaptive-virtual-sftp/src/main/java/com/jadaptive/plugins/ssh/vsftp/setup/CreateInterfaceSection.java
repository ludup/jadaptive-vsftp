package com.jadaptive.plugins.ssh.vsftp.setup;

import org.pf4j.Extension;

import com.jadaptive.plugins.ssh.vsftp.VirtualSFTPInterface;
import com.jadaptive.plugins.sshd.SSHInterface;
import com.jadaptive.plugins.sshd.setup.InterfaceSection;

@Extension
public class CreateInterfaceSection extends InterfaceSection {

	public CreateInterfaceSection() {
		super(VirtualSFTPInterface.RESOURCE_KEY);
	}

	@Override
	protected SSHInterface createInterface(com.jadaptive.plugins.sshd.setup.CreateInterface iface) {
		VirtualSFTPInterface obj = new VirtualSFTPInterface();
		obj.setAddressToBind(iface.getAddressToBind());
		obj.setPortToBind(iface.getPort());
		obj.setName("Default SFTP Interface");
		return obj;
	}

	@Override
	protected int getDefaultPort() {
		return 2222;
	}
}
