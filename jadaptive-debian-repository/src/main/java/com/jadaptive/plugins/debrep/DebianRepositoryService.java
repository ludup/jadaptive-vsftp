package com.jadaptive.plugins.debrep;

import java.io.File;
import java.io.IOException;

public interface DebianRepositoryService {

	void rebuildConfiguration(DebianRelease object);

	void retryIncoming(DebianRepository rep) throws IOException;

	void rebuildConfiguration(DebianRepository resource);

	void pushToRemote(DebianRepository repo) throws IOException;

	void addPackage(DebianRepository repo, DebianRelease release, File file, boolean deleteOriginal) throws IOException;

	void promote(DebianRelease resource, DebianRepository fromRepo, DebianRepository toRepo) throws IOException;

}
