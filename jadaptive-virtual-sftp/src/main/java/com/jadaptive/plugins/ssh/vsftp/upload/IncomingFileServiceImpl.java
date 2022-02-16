package com.jadaptive.plugins.ssh.vsftp.upload;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jadaptive.api.db.SearchField;
import com.jadaptive.api.db.TenantAwareObjectDatabase;
import com.jadaptive.utils.Utils;

@Service
public class IncomingFileServiceImpl implements IncomingFileService {

	@Autowired
	private TenantAwareObjectDatabase<IncomingFile> objectDatabase;
	
	@Override
	public IncomingFile getIncomingFile(String uuid) {
		return objectDatabase.get(uuid, IncomingFile.class);
	}

	@Override
	public Collection<IncomingFile> getLatestFiles() {
		return objectDatabase.searchTable(IncomingFile.class, 0, 5, SearchField.gt("created", Utils.thirtyDaysAgo()));
	}
}
