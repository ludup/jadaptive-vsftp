package com.jadaptive.plugins.ssh.vsftp.upload;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jadaptive.api.json.RequestStatus;
import com.jadaptive.api.json.RequestStatusImpl;
import com.jadaptive.api.permissions.AuthenticatedService;
import com.jadaptive.api.servlet.Request;
import com.jadaptive.api.session.SessionTimeoutException;
import com.jadaptive.api.session.SessionUtils;
import com.jadaptive.api.session.UnauthorizedException;
import com.jadaptive.api.upload.UploadHandler;
import com.jadaptive.plugins.sshd.SSHDService;
import com.sshtools.common.files.AbstractFile;

@Extension
public class BrowseFilesUploadHandler extends AuthenticatedService implements UploadHandler {

	@Autowired
	private SSHDService sshdService;
	
	@Autowired
	SessionUtils sessionUtils;
	
	@Override
	public void handleUpload(String handlerName, String uri, Map<String, String> parameters, String filename,
			InputStream in) throws IOException, SessionTimeoutException, UnauthorizedException {
		

		setupUserContext(sessionUtils.getActiveSession(Request.get()).getUser());
		
		try { 
			String path = parameters.get("path");
			
			if(StringUtils.isBlank(path)) {
				throw new IOException("No path parameter provided!");
			}
			
			AbstractFile file = sshdService.getFileFactory(getCurrentUser()).getFile(path);

			if(!file.exists()) {
				throw new FileNotFoundException("No public area to place files");
			}
			
			file = file.resolveFile(filename);
			if(!file.exists()) {
				file.createNewFile();
			}
			IOUtils.copy(in, file.getOutputStream());

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
