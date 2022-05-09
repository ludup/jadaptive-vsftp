package com.jadaptive.plugins.ssh.vsftp.uploads;

import java.util.Collection;

public interface UploadFormService {

	void saveOrUpdate(UploadForm share);

	UploadForm getFormByShortCode(String shortCode);

	Iterable<UploadForm> allObjects();

	Collection<UploadForm> getUserForms();

}
