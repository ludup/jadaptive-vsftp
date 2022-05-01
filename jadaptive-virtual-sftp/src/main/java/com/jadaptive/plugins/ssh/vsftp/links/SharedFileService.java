package com.jadaptive.plugins.ssh.vsftp.links;

import java.io.IOException;

import com.jadaptive.api.entity.AbstractUUIDObjectService;
import com.jadaptive.api.user.User;
import com.sshtools.common.files.AbstractFile;
import com.sshtools.common.permissions.PermissionDeniedException;

public interface SharedFileService extends AbstractUUIDObjectService<SharedFile> {

	SharedFile getDownloadByPath(String path, User user);

	SharedFile createDownloadLink(AbstractFile file, User user) throws IOException, PermissionDeniedException;

	SharedFile getDownloadByShortCode(String shortCode);

	String getDirectLink(SharedFile download);

	String getPublicLink(SharedFile share);

	void notifyShareAccess(SharedFile share);
	
	void notifyShareCreation(SharedFile share);

}
