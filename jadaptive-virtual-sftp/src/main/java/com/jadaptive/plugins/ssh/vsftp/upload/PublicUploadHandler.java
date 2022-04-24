package com.jadaptive.plugins.ssh.vsftp.upload;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Map;

import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.io.DigestInputStream;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;

import com.jadaptive.api.session.SessionTimeoutException;
import com.jadaptive.api.session.UnauthorizedException;
import com.jadaptive.api.ui.PageRedirect;
import com.jadaptive.plugins.ssh.vsftp.AnonymousUserDatabase;
import com.jadaptive.plugins.ssh.vsftp.VirtualFileService;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.jadaptive.plugins.ssh.vsftp.uploads.UploadForm;
import com.jadaptive.plugins.ssh.vsftp.uploads.UploadFormService;
import com.sshtools.common.files.AbstractFile;

@Extension
public class PublicUploadHandler extends AbstractFilesUploadHandler {

	public static String MESSAGE_SENDER_FILE_UPLOADED = "publicUploadReceived";
	public static String MESSAGE_NOTIFY_FILE_RECEIVED = "publicUploadNotification";
	
	@Autowired
	private VirtualFileService fileService; 
	
	@Autowired
	private UploadFormService uploadService; 
	
	@Autowired
	private AnonymousUserDatabase anonymousDatabase;
	
	@Autowired
	private IncomingFileService incomingService;
	
	ThreadLocal<Collection<FileUpload>> uploadPaths = new ThreadLocal<>();
	ThreadLocal<String> currentEmail = new ThreadLocal<>();
	ThreadLocal<String> currentName = new ThreadLocal<>();
	ThreadLocal<String> currentReference = new ThreadLocal<>();
	ThreadLocal<VirtualFolder> currentVirtualFolder = new ThreadLocal<>();
	ThreadLocal<UploadForm> currentSharedFile = new ThreadLocal<>();

	
	public void handleUpload(String handlerName, String uri, Map<String, String> parameters, String filename, InputStream in) throws IOException, SessionTimeoutException, UnauthorizedException {
		
		setupUserContext(anonymousDatabase.getAnonymousUser());
		
		try { 
			
			if(currentName.get() == null) {
				currentName.set(parameters.get("name"));
			}
			
			if(currentEmail.get() == null) {
				currentEmail.set(parameters.get("email"));
			}
			
			if(currentReference.get() == null) {
				currentReference.set(parameters.get("reference"));
			}

			if(currentVirtualFolder.get() == null) {
				currentSharedFile.set(uploadService.getFormByShortCode(uri));
				currentVirtualFolder.set(fileService.getVirtualFolder(currentSharedFile.get().getVirtualPath()));
			}
			
			try(DigestInputStream din = new DigestInputStream(in, new SHA256Digest())) {
				AbstractFile file = doUpload(currentVirtualFolder.get().getMountPath(), filename, din);
				
				if(uploadPaths.get()==null) {
					uploadPaths.set(new ArrayList<>());
				}
				
				byte[] hash = new byte[din.getDigest().getDigestSize()];
				din.getDigest().doFinal(hash, 0);
				
				uploadPaths.get().add(new FileUpload(file.getName(),
						file.getAbsolutePath(),
						file.length(),
						String.format("SHA256:%s", Base64.getEncoder().encodeToString(hash))));
			}
			
		} catch(Throwable e) {
			if(e instanceof PageRedirect) {
				throw (PageRedirect) e;
			}
			throw new IOException(e.getMessage(), e);
		} finally {
			clearUserContext();
		}
	}

	@Override
	public void onUploadsComplete() {
		
		setupSystemContext();
		
		try {
			Collection<FileUpload> files = uploadPaths.get();	
			
			IncomingFile upload = new IncomingFile();
			
			VirtualFolder virtualFolder = currentVirtualFolder.get();
			UploadForm uploadForm = currentSharedFile.get();
			
			upload.setEmail(currentEmail.get());
			upload.setName(currentName.get());
			upload.setReference(currentReference.get());
			upload.setUploadPaths(files);
			upload.setUploadArea(virtualFolder.getName());
			
			incomingService.save(upload, virtualFolder, uploadForm);
			
			clean();
			
		} finally {
			clearUserContext();
		}
	}

	@Override
	public void onUploadsFailure(Throwable e) {
		clean();
	}

	private void clean() {
		uploadPaths.remove();
		currentEmail.remove();
		currentName.remove();
		currentReference.remove();
		currentVirtualFolder.remove();
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
