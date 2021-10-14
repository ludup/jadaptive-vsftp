package com.jadaptive.plugins.ssh.vsftp.setup;

import org.pf4j.Extension;

import com.jadaptive.plugins.ssh.vsftp.VirtualSFTPInterface;
import com.jadaptive.plugins.sshd.SSHInterface;
import com.jadaptive.plugins.sshd.setup.InterfaceSection;

@Extension
public class CreateInterfaceSection extends InterfaceSection {

	@Override
	protected SSHInterface createInterface(com.jadaptive.plugins.sshd.setup.CreateInterface iface) {
		VirtualSFTPInterface obj = new VirtualSFTPInterface();
		obj.setAddressToBind(iface.getAddressToBind());
		obj.setPortToBind(iface.getPort());
		obj.setName("Default VSFTP Interface");
		return obj;
	}

	@Override
	public Integer getPosition() {
		return SelectMount.SETUP_WIZARD_POSITION + 1;
	}
	
	

}
