package com.jadaptive.plugins.debrep;

import java.io.File;
import java.io.InputStream;

import com.jadaptive.api.entity.AbstractUUIDObjectService;

public interface GPGKeyService extends
		AbstractUUIDObjectService<GPGKeyResource> {

	void publishKey(GPGKeyResource resource) ;

	File getRealmGPGHomeDir();

	String getPublicContent(GPGKeyResource resource);

	GPGKeyResource getKeyByFingerprint(String fp);

	Iterable<GPGKeyResource> getKeys();

	Iterable<GPGKeyResource> getSigningKeys();
	
	GPGKeyResource importKey(InputStream in);

	Iterable<GPGKeyResource> searchKeys(String searchColumn, String search, int start, int length);

	long getKeyCount(String searchColumn, String search);

}
