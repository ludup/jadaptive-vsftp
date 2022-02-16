package com.jadaptive.plugins.ssh.vsftp.upload;

import java.util.Collection;

public interface IncomingFileService {

	IncomingFile getIncomingFile(String uuid);

	Collection<IncomingFile> getLatestFiles();

}
