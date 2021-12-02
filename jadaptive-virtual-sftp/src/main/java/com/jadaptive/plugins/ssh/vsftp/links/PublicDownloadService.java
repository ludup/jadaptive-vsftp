package com.jadaptive.plugins.ssh.vsftp.links;

import java.io.IOException;

import com.jadaptive.api.entity.AbstractUUIDObjectService;
import com.sshtools.common.files.AbstractFile;
import com.sshtools.common.permissions.PermissionDeniedException;

public interface PublicDownloadService extends AbstractUUIDObjectService<PublicDownload> {

	PublicDownload getDownloadByPath(String path);

	PublicDownload createDownloadLink(AbstractFile file) throws IOException, PermissionDeniedException;

	PublicDownload getDownloadByShortCode(String shortCode);

}
