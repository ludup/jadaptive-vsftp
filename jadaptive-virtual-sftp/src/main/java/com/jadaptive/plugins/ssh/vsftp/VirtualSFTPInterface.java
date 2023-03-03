package com.jadaptive.plugins.ssh.vsftp;

import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.template.ObjectDefinition;
import com.jadaptive.plugins.sshd.SSHInterface;
import com.jadaptive.plugins.sshd.SSHInterfaceFactory;

@ObjectDefinition(resourceKey = VirtualSFTPInterface.RESOURCE_KEY, 
	bundle = VirtualSFTPInterface.RESOURCE_KEY, 
	type = ObjectType.COLLECTION)
public class VirtualSFTPInterface extends SSHInterface {

	private static final long serialVersionUID = 7735880486970246083L;

	public static final String RESOURCE_KEY = "vsftpInterface";
	
	@Override
	public String getResourceKey() {
		return VirtualSFTPInterface.RESOURCE_KEY;
	}
	
	public Class<? extends SSHInterfaceFactory<?,?>> getContextFactory() {
		return VirtualSFTPInterfaceFactory.class;
	}

	@Override
	public Class<? extends SSHInterfaceFactory<?,?>> getInterfaceFactory() {
		return VirtualSFTPInterfaceFactory.class;
	}
}
