package com.jadaptive.plugins.ssh.vsftp.upload;

import java.io.IOException;
import java.util.Map;

import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jadaptive.api.servlet.Request;
import com.jadaptive.api.session.SessionTimeoutException;
import com.jadaptive.api.session.UnauthorizedException;
import com.jadaptive.api.upload.Upload;
import com.jadaptive.api.upload.UploadIterator;
import com.jadaptive.utils.ParameterHelper;
import com.sshtools.common.util.URLUTF8Encoder;

@Extension
public class BrowseFilesUploadHandler extends AbstractFilesUploadHandler {

	static Logger log = LoggerFactory.getLogger(BrowseFilesUploadHandler.class);
	
	@Override
	public void handleUpload(String handlerName, String uri, Map<String, String[]> parameters, UploadIterator uploads) throws IOException, SessionTimeoutException, UnauthorizedException {
		

		setupUserContext(sessionUtils.getActiveSession(Request.get()).getUser());
		
		try { 
			String path = URLUTF8Encoder.decode(ParameterHelper.getValue(parameters, "path"));
			
			while(uploads.hasNext()) {
				Upload upload = uploads.next();
				doUpload(path, upload.getFilename(), upload.openStream());
			}

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
