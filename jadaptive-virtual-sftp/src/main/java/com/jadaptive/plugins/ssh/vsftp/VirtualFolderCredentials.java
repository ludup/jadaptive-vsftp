package com.jadaptive.plugins.ssh.vsftp;

import com.jadaptive.api.entity.EntityType;
import com.jadaptive.api.repository.AbstractUUIDEntity;
import com.jadaptive.api.template.Template;

@Template(name="Virtual Folder Credentials", resourceKey = "virtualFolderCredentials", type = EntityType.OBJECT)
public abstract class VirtualFolderCredentials extends AbstractUUIDEntity {

}
