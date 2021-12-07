package com.jadaptive.plugins.ssh.vsftp.upload;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jadaptive.api.servlet.Request;
import com.jadaptive.api.session.SessionTimeoutException;
import com.jadaptive.api.session.UnauthorizedException;
import com.sshtools.common.util.URLUTF8Encoder;

@Extension
public class BrowseFilesUploadHandler extends AbstractFilesUploadHandler {

	static Logger log = LoggerFactory.getLogger(BrowseFilesUploadHandler.class);
	
	@Override
	public void handleUpload(String handlerName, String uri, Map<String, String> parameters, String filename,
			InputStream in) throws IOException, SessionTimeoutException, UnauthorizedException {
		

		setupUserContext(sessionUtils.getActiveSession(Request.get()).getUser());
		
		try { 
			String path = URLUTF8Encoder.decode(parameters.get("path"));
			
			doUpload(path, filename, in);

		} catch(Throwable e) {
			throw new IOException(e.getMessage(), e);
		} finally {
			clearUserContext();
		}
	}
	
	@Override
	public boolean isSessionRequired() {
		return true;
	}

	@Override
	public String getURIName() {
		return "files";
	}

}
