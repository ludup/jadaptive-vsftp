package com.jadaptive.plugins.ssh.vsftp.links;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jadaptive.api.db.SearchField;
import com.jadaptive.api.entity.AbstractUUIDObjectServceImpl;
import com.jadaptive.api.entity.ObjectException;
import com.jadaptive.plugins.ssh.vsftp.VirtualFileService;
import com.jadaptive.utils.Utils;
import com.sshtools.common.files.AbstractFile;
import com.sshtools.common.permissions.PermissionDeniedException;
import com.sshtools.common.util.FileUtils;

@Service
public class SharedFileServiceImpl extends AbstractUUIDObjectServceImpl<SharedFile> implements SharedFileService {

	@Autowired
	private VirtualFileService fileService; 
	
	@Override
	protected Class<SharedFile> getResourceClass() {
		return SharedFile.class;
	}

	public SharedFile getDownloadByPath(String path) {
		return objectDatabase.get(getResourceClass(), SearchField.eq("virtualPath", FileUtils.checkEndsWithNoSlash(path)));
	}
	
	@Override
	public String saveOrUpdate(SharedFile object) {
		if(StringUtils.isBlank(object.getShortCode())) {
			object.setShortCode(Utils.generateRandomAlphaNumericString(8));
		}
		try {
			AbstractFile file = fileService.getFile(object.getVirtualPath());
					
			if(file.isDirectory()) {
				object.setFilename(file.getName() + ".zip");
			} else {
				object.setFilename(file.getName());
			}
		} catch (IOException | PermissionDeniedException e) {
			throw new ObjectException(e.getMessage(), e);
		}
		
		object.setVirtualPath(FileUtils.checkEndsWithNoSlash(object.getVirtualPath()));
		objectDatabase.saveOrUpdate(object);
		return object.getUuid();
	}

	@Override
	public SharedFile createDownloadLink(AbstractFile file) throws IOException, PermissionDeniedException {
		SharedFile link = new SharedFile();
		link.setVirtualPath(file.getAbsolutePath());
		if(file.isDirectory()) {
			link.setFilename(file.getName() + ".zip");
		} else {
			link.setFilename(file.getName());
		}
		saveOrUpdate(link);
		return link;
	}

	@Override
	public SharedFile getDownloadByShortCode(String shortCode) {
		return objectDatabase.get(getResourceClass(), SearchField.eq("shortCode", shortCode));
	}

}
