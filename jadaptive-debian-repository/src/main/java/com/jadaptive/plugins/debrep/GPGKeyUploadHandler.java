package com.jadaptive.plugins.debrep;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.jadaptive.api.permissions.AuthenticatedService;
import com.jadaptive.api.session.SessionTimeoutException;
import com.jadaptive.api.session.UnauthorizedException;
import com.jadaptive.api.upload.UploadHandler;
import com.jadaptive.api.upload.UploadIterator;

@Extension
public class GPGKeyUploadHandler extends AuthenticatedService implements UploadHandler {

	static Logger log = LoggerFactory.getLogger(GPGKeyUploadHandler.class);
	
	@Autowired
	private GPGKeyService keyService;  

	@Override
	public boolean isSessionRequired() {
		return true;
	}

	@Override
	public String getURIName() {
		return "gpg";
	}

	@Override
	public void handleUpload(String handlerName, String uri, Map<String, String[]> parameters, UploadIterator uploads)
			throws IOException, SessionTimeoutException, UnauthorizedException {
		
		try { 
			uploads.forEachRemaining((u)->{
				try(InputStream in = u.openStream()) {
					keyService.importKey(in);
				} catch (IOException e) {
					throw new IllegalStateException(e.getMessage(), e);
				}
			});
			

		} catch(Throwable e) {
			log.error("Failed to upload public key", e);
			throw new IOException(e.getMessage(), e);
		} 
	}
}
