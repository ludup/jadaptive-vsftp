package com.jadaptive.plugins.ssh.vsftp;

import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.repository.AbstractUUIDEntity;
import com.jadaptive.api.template.ObjectDefinition;

@ObjectDefinition(name="Virtual Folder Credentials", resourceKey = "virtualFolderCredentials", type = ObjectType.OBJECT)
public abstract class VirtualFolderCredentials extends AbstractUUIDEntity {
	
	private static final long serialVersionUID = -6867303892310998231L;

}
