package com.jadaptive.plugins.ssh.vsftp.uploads;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jadaptive.api.db.SearchField;
import com.jadaptive.api.db.TenantAwareObjectDatabase;
import com.jadaptive.api.stats.ResourceService;
import com.jadaptive.plugins.ssh.vsftp.VirtualFileService;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.jadaptive.utils.Utils;

@Service
public class UploadFormServiceImpl implements UploadFormService, ResourceService {

	@Autowired
	private TenantAwareObjectDatabase<UploadForm> uploadDatabase;
	
	@Autowired
	private VirtualFileService fileService; 
	
	@Override
	public void saveOrUpdate(UploadForm object) {
		
		if(StringUtils.isBlank(object.getShortCode())) {
			object.setShortCode(Utils.generateRandomAlphaNumericString(8));
		}
		
		uploadDatabase.saveOrUpdate(object);
	}

	@Override
	public UploadForm getFormByShortCode(String shortCode) {
		return uploadDatabase.get(UploadForm.class, SearchField.eq("shortCode", shortCode));
	}

	@Override
	public Iterable<UploadForm> allObjects() {
		return uploadDatabase.list(UploadForm.class);
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public String getResourceKey() {
		return UploadForm.RESOURCE_KEY;
	}

	@Override
	public long getTotalResources() {
		return uploadDatabase.count(UploadForm.class);
	}

	@Override
	public Collection<UploadForm> getUserForms() {
		
		List<String> paths = new ArrayList<>();
		for(VirtualFolder folder : fileService.getPersonalFolders()) {
			paths.add(folder.getMountPath());
		}
		return uploadDatabase.searchObjects(UploadForm.class, SearchField.in("virtualPath", paths));
	}

}
