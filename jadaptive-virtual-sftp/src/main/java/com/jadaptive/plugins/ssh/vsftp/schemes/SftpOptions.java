package com.jadaptive.plugins.ssh.vsftp.schemes;

import com.jadaptive.api.entity.ObjectScope;
import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.template.ObjectDefinition;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderOptions;

@ObjectDefinition(name = "SFTP Options", resourceKey = SftpOptions.RESOURCE_KEY, scope = ObjectScope.GLOBAL, type = ObjectType.OBJECT)
public class SftpOptions extends VirtualFolderOptions {

	private static final long serialVersionUID = -8772571009383063875L;

	public static final String RESOURCE_KEY = "sftpOptions";

	@Override
	public String getResourceKey() {
		return RESOURCE_KEY;
	}
	
}
