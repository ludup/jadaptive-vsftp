package com.jadaptive.plugins.ssh.vsftp.upload;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;

import com.jadaptive.api.session.SessionTimeoutException;
import com.jadaptive.api.session.UnauthorizedException;
import com.jadaptive.plugins.ssh.vsftp.AnonymousUserDatabase;
import com.jadaptive.plugins.ssh.vsftp.VirtualFileService;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;

@Extension
public class PublicUploadHandler extends AbstractFilesUploadHandler {

	public static String MESSAGE_SENDER_FILE_UPLOADED = "publicUploadReceived";
	public static String MESSAGE_NOTIFY_FILE_RECEIVED = "publicUploadNotification";
	
	@Autowired
	private VirtualFileService fileService; 
	
	@Autowired
	private AnonymousUserDatabase anonymousDatabase;
	
//	@Autowired
//	private EmailNotificationService notificationService; 
//	
//	@Autowired
//	private MessageService messageService;
	
	public void handleUpload(String handlerName, String uri, Map<String, String> parameters, String filename,
			InputStream in) throws IOException, SessionTimeoutException, UnauthorizedException {
		
		setupUserContext(anonymousDatabase.getAnonymousUser());
		
		try { 
			
			String name = parameters.get("name");
			String email = parameters.get("email");
			
			/**
			 * Handler "short code" name should be assigned to an anonymous user
			 */
			VirtualFolder folder = fileService.getVirtualFolderByShortCode(uri);
			
			doUpload(folder.getMountPath(), filename, in);
			
			sendNotifications(name, email);
			
		} catch(Throwable e) {
			throw new IOException(e.getMessage(), e);
		} finally {
			clearUserContext();
		}
	}
	
	private void sendNotifications(String name, String email) {
		

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
