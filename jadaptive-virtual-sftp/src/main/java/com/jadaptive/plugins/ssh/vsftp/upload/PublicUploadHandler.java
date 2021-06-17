package com.jadaptive.plugins.ssh.vsftp.upload;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;

import com.jadaptive.api.permissions.AuthenticatedService;
import com.jadaptive.api.session.SessionTimeoutException;
import com.jadaptive.api.session.UnauthorizedException;
import com.jadaptive.api.upload.UploadHandler;
import com.jadaptive.plugins.ssh.vsftp.AnonymousUserDatabase;
import com.jadaptive.plugins.ssh.vsftp.VirtualFileService;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.jadaptive.plugins.sshd.SSHDService;
import com.sshtools.common.files.AbstractFile;

@Extension
public class PublicUploadHandler extends AuthenticatedService implements UploadHandler {

	@Autowired
	private SSHDService sshdService;
	
	@Autowired
	private VirtualFileService fileService; 
	
	@Autowired
	private AnonymousUserDatabase anonymousDatabase;
	
	@Override
	public void handleUpload(String handlerName, String uri, Map<String, String> parameters, String filename,
			InputStream in) throws IOException, SessionTimeoutException, UnauthorizedException {
		
		setupUserContext(anonymousDatabase.getAnonymousUser());
		
		try { 
			/**
			 * Handler "short code" name should be assigned to an anonymous user
			 */
			VirtualFolder folder = fileService.getVirtualFolderByShortCode(uri);
			AbstractFile file = sshdService.getFileFactory(getCurrentUser()).getFile(folder.getMountPath());

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
		return false;
	}

	@Override
	public String getURIName() {
		return "public";
	}

}
