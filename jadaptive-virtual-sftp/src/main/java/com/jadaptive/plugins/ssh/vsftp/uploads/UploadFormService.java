package com.jadaptive.plugins.ssh.vsftp.uploads;

public interface UploadFormService {

	void saveOrUpdate(UploadForm share);

	UploadForm getFormByShortCode(String shortCode);

	Iterable<UploadForm> allObjects();

}
