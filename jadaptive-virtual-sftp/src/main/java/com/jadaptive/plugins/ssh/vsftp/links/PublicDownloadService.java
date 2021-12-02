package com.jadaptive.plugins.ssh.vsftp.links;

import com.jadaptive.api.entity.AbstractUUIDObjectService;

public interface PublicDownloadService extends AbstractUUIDObjectService<PublicDownload> {

	PublicDownload getDownloadByPath(String path);

	PublicDownload createDownloadLink(String path);

	PublicDownload getDownloadByShortCode(String shortCode);

}
