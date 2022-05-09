package com.jadaptive.plugins.ssh.vsftp.upload;

import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.jadaptive.plugins.ssh.vsftp.uploads.UploadForm;

public interface IncomingFileService {

	IncomingFile getIncomingFile(String uuid);

	Iterable<IncomingFile> getLatestFiles();

	void delete(IncomingFile file);

	void save(IncomingFile file, VirtualFolder folder, UploadForm form);

}
