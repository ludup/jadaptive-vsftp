package com.jadaptive.plugins.ssh.vsftp.links;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.jadaptive.api.db.SearchField;
import com.jadaptive.api.entity.AbstractUUIDObjectServceImpl;
import com.jadaptive.utils.Utils;
import com.sshtools.common.util.FileUtils;

@Service
public class PublicDownloadServiceImpl extends AbstractUUIDObjectServceImpl<PublicDownload> implements PublicDownloadService {

	@Override
	protected Class<PublicDownload> getResourceClass() {
		return PublicDownload.class;
	}

	public PublicDownload getDownloadByPath(String path) {
		return objectDatabase.get(getResourceClass(), SearchField.eq("virtualPath", FileUtils.checkEndsWithNoSlash(path)));
	}
	
	@Override
	public String saveOrUpdate(PublicDownload object) {
		if(StringUtils.isBlank(object.getShortCode())) {
			object.setShortCode(Utils.generateRandomAlphaNumericString(16));
		}
		object.setVirtualPath(FileUtils.checkEndsWithNoSlash(object.getVirtualPath()));
		objectDatabase.saveOrUpdate(object);
		return object.getUuid();
	}

	@Override
	public PublicDownload createDownloadLink(String path) {
		PublicDownload link = new PublicDownload();
		link.setVirtualPath(path);
		saveOrUpdate(link);
		return link;
	}

	@Override
	public PublicDownload getDownloadByShortCode(String shortCode) {
		return objectDatabase.get(getResourceClass(), SearchField.eq("shortCode", shortCode));
	}

}
