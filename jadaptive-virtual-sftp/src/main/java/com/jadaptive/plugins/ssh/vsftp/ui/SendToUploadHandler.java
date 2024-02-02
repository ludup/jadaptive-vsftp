package com.jadaptive.plugins.ssh.vsftp.ui;

import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;

import com.jadaptive.api.session.SessionTimeoutException;
import com.jadaptive.api.session.UnauthorizedException;
import com.jadaptive.plugins.ssh.vsftp.sendto.SendToService;
import com.jadaptive.plugins.ssh.vsftp.upload.AbstractFilesUploadHandler;
import com.sshtools.common.permissions.PermissionDeniedException;

@Extension
public class SendToUploadHandler extends AbstractFilesUploadHandler {

	@Autowired
	private SendToService transferService; 
	
	@Override
	public boolean isSessionRequired() {
		return false;
	}

	@Override
	public String getURIName() {
		return "send-to";
	}

	@Override
	public void handleUpload(String handlerName, String uri, Map<String, String> parameters, String filename,
			InputStream in) throws IOException, SessionTimeoutException, UnauthorizedException {
		
		try {
			transferService.sendFile(uri, filename, in);
		} catch (NoSuchAlgorithmException | IOException | PermissionDeniedException e) {
			throw new IOException(e.getMessage(), e);
		}
		
	}

	@Override
	public void onUploadsComplete(Map<String, String> params) {
		try {
			transferService.completeUpload(params.get("shareCode"));
		} catch (IOException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}
	
	

}
