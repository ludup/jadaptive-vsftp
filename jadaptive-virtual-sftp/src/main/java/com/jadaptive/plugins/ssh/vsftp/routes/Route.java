package com.jadaptive.plugins.ssh.vsftp.routes;

import com.jadaptive.api.repository.AbstractUUIDEntity;

public class Route extends AbstractUUIDEntity {

	String user;
	
	String remoteAddress;
	
	String localAddress;
	
	String incomingPath;
	
	String outgoingPath;
}
