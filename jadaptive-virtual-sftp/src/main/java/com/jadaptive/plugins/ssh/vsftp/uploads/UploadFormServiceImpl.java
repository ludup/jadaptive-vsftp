package com.jadaptive.plugins.ssh.vsftp.uploads;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jadaptive.api.db.SearchField;
import com.jadaptive.api.db.TenantAwareObjectDatabase;
import com.jadaptive.utils.Utils;

@Service
public class UploadFormServiceImpl implements UploadFormService {

	@Autowired
	private TenantAwareObjectDatabase<UploadForm> uploadDatabase;
	
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

}
