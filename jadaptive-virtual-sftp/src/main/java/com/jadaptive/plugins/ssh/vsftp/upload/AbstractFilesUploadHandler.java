package com.jadaptive.plugins.ssh.vsftp.upload;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.jadaptive.api.permissions.AuthenticatedService;
import com.jadaptive.api.session.SessionTimeoutException;
import com.jadaptive.api.session.SessionUtils;
import com.jadaptive.api.session.UnauthorizedException;
import com.jadaptive.api.upload.UploadHandler;
import com.jadaptive.plugins.sshd.SSHDService;
import com.sshtools.common.files.AbstractFile;
import com.sshtools.common.permissions.PermissionDeniedException;
import com.sshtools.common.util.IOUtils;
import com.sshtools.common.util.URLUTF8Encoder;

public abstract class AbstractFilesUploadHandler extends AuthenticatedService implements UploadHandler {

	static Logger log = LoggerFactory.getLogger(AbstractFilesUploadHandler.class);
	
	@Autowired
	private SSHDService sshdService;
	
	@Autowired
	SessionUtils sessionUtils;
	
	
	protected void doUpload(String path, String filename, InputStream in) throws IOException, SessionTimeoutException, UnauthorizedException {

		try {

			if(StringUtils.isBlank(path)) {
				throw new IOException("No path parameter provided!");
			}
			
			AbstractFile file = sshdService.getFileFactory(getCurrentUser()).getFile(path);

			if(!file.exists()) {
				throw new FileNotFoundException(String.format("No upload area at %s", path));
			}
			
			if(!file.isWritable()) {
				throw new IOException(String.format("%s is not writable", path));
			}
			
			file = file.resolveFile(filename);
			if(!file.exists()) {
				file.createNewFile();
			}
			
			OutputStream out = file.getOutputStream();
			try {
				long written = in.transferTo(out);
				log.info("Wrote {} bytes of data to file {}", written, path);
			} finally {
				IOUtils.closeStream(in);
				IOUtils.closeStream(out);
			}
		} catch (PermissionDeniedException e) {
			throw new IOException(e.getMessage(), e);
		}
	}

}
