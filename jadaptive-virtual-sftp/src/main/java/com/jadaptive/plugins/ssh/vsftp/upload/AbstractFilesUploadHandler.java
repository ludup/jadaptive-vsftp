package com.jadaptive.plugins.ssh.vsftp.upload;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.jadaptive.api.db.SingletonObjectDatabase;
import com.jadaptive.api.events.EventService;
import com.jadaptive.api.permissions.AuthenticatedService;
import com.jadaptive.api.servlet.Request;
import com.jadaptive.api.session.SessionTimeoutException;
import com.jadaptive.api.session.SessionUtils;
import com.jadaptive.api.ui.ErrorPage;
import com.jadaptive.api.ui.PageRedirect;
import com.jadaptive.api.upload.UploadHandler;
import com.jadaptive.plugins.ssh.vsftp.ContentHash;
import com.jadaptive.plugins.ssh.vsftp.VFSConfiguration;
import com.jadaptive.plugins.ssh.vsftp.ui.FileUploadEvent;
import com.jadaptive.plugins.ssh.vsftp.ui.TransferResult;
import com.jadaptive.plugins.sshd.SSHDService;
import com.jadaptive.utils.Utils;
import com.sshtools.common.files.AbstractFile;
import com.sshtools.common.permissions.PermissionDeniedException;
import com.sshtools.common.util.FileUtils;
import com.sshtools.common.util.IOUtils;
import com.sshtools.humanhash.HumanHashGenerator;

public abstract class AbstractFilesUploadHandler extends AuthenticatedService implements UploadHandler {

	static Logger log = LoggerFactory.getLogger(AbstractFilesUploadHandler.class);
	
	@Autowired
	private SSHDService sshdService;
	
	@Autowired
	SessionUtils sessionUtils;
	
	@Autowired
	private SingletonObjectDatabase<VFSConfiguration> configurationService; 
	
	@Autowired
	private EventService eventService; 
	
	protected AbstractFile doUpload(String path, String filename, InputStream in) throws  SessionTimeoutException {

		Date started = Utils.now();
		
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
			
			ContentHash contentHash = configurationService.getObject(VFSConfiguration.class).getDefaultHash();
			MessageDigest digest = MessageDigest.getInstance(contentHash.getAlgorithm());
			long size = 0L;
			
			OutputStream out = file.getOutputStream();
			try(DigestOutputStream digestOutput = new DigestOutputStream(out, digest)) {
				size = in.transferTo(digestOutput);
			} finally {
				IOUtils.closeStream(in);
				IOUtils.closeStream(out);
			}

			byte[] output = digest.digest();
			eventService.publishEvent(new FileUploadEvent(new TransferResult(filename, 
					FileUtils.getParentPath(path), size, started, Utils.now(), "",
					new HumanHashGenerator(output)
					.words(contentHash.getWords())
					.build())));
			
			return file;
		} catch (IOException | PermissionDeniedException | NoSuchAlgorithmException e) {
			
			eventService.publishEvent(new FileUploadEvent(
					new TransferResult(filename, FileUtils.getParentPath(path), 
							0L, started, Utils.now()), e));
			throw new PageRedirect(new ErrorPage(e, Request.get().getHeader("Referer")));
		}
	}

}
