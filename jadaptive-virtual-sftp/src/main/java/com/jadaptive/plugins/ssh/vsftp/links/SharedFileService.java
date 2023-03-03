package com.jadaptive.plugins.ssh.vsftp.links;

import java.io.IOException;
import java.util.Date;

import com.jadaptive.api.entity.AbstractUUIDObjectService;
import com.jadaptive.api.user.User;
import com.sshtools.common.files.AbstractFile;
import com.sshtools.common.permissions.PermissionDeniedException;

public interface SharedFileService extends AbstractUUIDObjectService<SharedFile> {

	String SHARING = "Sharing";

	SharedFile getDownloadByPath(String path, User user);

	SharedFile createDownloadLink(AbstractFile file, User user) throws IOException, PermissionDeniedException;

	SharedFile getDownloadByShortCode(String shortCode);

	String getDirectLink(SharedFile download);

	String getPublicLink(SharedFile share);

	void notifyShareCreation(SharedFile share);

	void notifyShareAccess(SharedFile share, Date started, String... paths);

	String getDirectLink(SharedFile download, String virtualPath);

	Iterable<SharedFile> getUserShares();

}
