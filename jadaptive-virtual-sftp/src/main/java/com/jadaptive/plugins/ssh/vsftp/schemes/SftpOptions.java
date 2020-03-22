package com.jadaptive.plugins.ssh.vsftp.schemes;

import com.jadaptive.api.entity.EntityScope;
import com.jadaptive.api.entity.EntityType;
import com.jadaptive.api.template.Template;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderOptions;

@Template(name = "SFTP Options", resourceKey = SftpOptions.RESOURCE_KEY, scope = EntityScope.GLOBAL, type = EntityType.OBJECT)
public class SftpOptions extends VirtualFolderOptions {

	public static final String RESOURCE_KEY = "sftpOptions";
	
}
