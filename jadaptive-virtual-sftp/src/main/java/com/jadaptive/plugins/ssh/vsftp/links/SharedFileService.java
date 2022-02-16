package com.jadaptive.plugins.ssh.vsftp.links;

import java.io.IOException;

import com.jadaptive.api.entity.AbstractUUIDObjectService;
import com.sshtools.common.files.AbstractFile;
import com.sshtools.common.permissions.PermissionDeniedException;

public interface SharedFileService extends AbstractUUIDObjectService<SharedFile> {

	SharedFile getDownloadByPath(String path);

	SharedFile createDownloadLink(AbstractFile file) throws IOException, PermissionDeniedException;

	SharedFile getDownloadByShortCode(String shortCode);

}
