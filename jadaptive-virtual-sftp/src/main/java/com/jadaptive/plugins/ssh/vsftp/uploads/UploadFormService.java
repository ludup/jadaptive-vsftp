package com.jadaptive.plugins.ssh.vsftp.uploads;

import java.util.Collection;

public interface UploadFormService {

	String UPLOAD_FORMS = "Upload Forms";

	void saveOrUpdate(UploadForm share);

	UploadForm getFormByShortCode(String shortCode);

	Iterable<UploadForm> allObjects();

	Collection<UploadForm> getUserForms();

}
