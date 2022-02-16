package com.jadaptive.plugins.ssh.vsftp.upload;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;

import com.jadaptive.api.db.TenantAwareObjectDatabase;
import com.jadaptive.api.session.SessionTimeoutException;
import com.jadaptive.api.session.UnauthorizedException;
import com.jadaptive.plugins.ssh.vsftp.AnonymousUserDatabase;
import com.jadaptive.plugins.ssh.vsftp.VirtualFileService;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.sshtools.common.files.AbstractFile;

@Extension
public class PublicUploadHandler extends AbstractFilesUploadHandler {

	public static String MESSAGE_SENDER_FILE_UPLOADED = "publicUploadReceived";
	public static String MESSAGE_NOTIFY_FILE_RECEIVED = "publicUploadNotification";
	
	@Autowired
	private VirtualFileService fileService; 
	
	@Autowired
	private AnonymousUserDatabase anonymousDatabase;
	
	@Autowired
	private TenantAwareObjectDatabase<IncomingFile> objectDatabase;
	
	ThreadLocal<Collection<FileUpload>> uploadPaths = new ThreadLocal<>();
	ThreadLocal<String> currentEmail = new ThreadLocal<>();
	ThreadLocal<String> currentName = new ThreadLocal<>();
	ThreadLocal<String> currentReference = new ThreadLocal<>();
	ThreadLocal<VirtualFolder> currentVirtualFolder = new ThreadLocal<>();
	
//	@Autowired
//	private EmailNotificationService notificationService; 
//	
//	@Autowired
//	private MessageService messageService;
	
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
				currentVirtualFolder.set(fileService.getVirtualFolderByShortCode(uri));
			}
			
			AbstractFile file = doUpload(currentVirtualFolder.get().getMountPath(), filename, in);
			
			if(uploadPaths.get()==null) {
				uploadPaths.set(new ArrayList<>());
			}
			
			uploadPaths.get().add(new FileUpload(file.getName(),
					file.getAbsolutePath(),
					file.length()));
			
		} catch(Throwable e) {
			throw new IOException(e.getMessage(), e);
		} finally {
			clearUserContext();
		}
	}

	@Override
	public void onUploadsComplete() {
		Collection<FileUpload> files = uploadPaths.get();	
		
		IncomingFile upload = new IncomingFile();
		
		upload.setEmail(currentEmail.get());
		upload.setName(currentName.get());
		upload.setReference(currentReference.get());
		upload.setUploadPaths(files);
		upload.setUploadArea(currentVirtualFolder.get().getName());
		
		objectDatabase.saveOrUpdate(upload);
		
		clean();
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
