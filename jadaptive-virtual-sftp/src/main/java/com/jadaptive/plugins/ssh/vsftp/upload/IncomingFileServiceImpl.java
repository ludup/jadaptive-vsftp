package com.jadaptive.plugins.ssh.vsftp.upload;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jadaptive.api.app.ApplicationService;
import com.jadaptive.api.db.AssignableObjectDatabase;
import com.jadaptive.api.db.SearchField;
import com.jadaptive.api.permissions.AuthenticatedService;
import com.jadaptive.api.permissions.PermissionService;
import com.jadaptive.api.role.Role;
import com.jadaptive.api.servlet.Request;
import com.jadaptive.api.template.SortOrder;
import com.jadaptive.api.user.User;
import com.jadaptive.api.user.UserService;
import com.jadaptive.plugins.email.AssignmentNotificationPreference;
import com.jadaptive.plugins.email.MessageService;
import com.jadaptive.plugins.email.RecipientHolder;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.jadaptive.plugins.ssh.vsftp.uploads.UploadForm;
import com.jadaptive.plugins.ssh.vsftp.uploads.UploadFormService;
import com.jadaptive.utils.StaticResolver;
import com.jadaptive.utils.Utils;

@Service
public class IncomingFileServiceImpl extends AuthenticatedService implements IncomingFileService {

	@Autowired
	private AssignableObjectDatabase<IncomingFile> objectDatabase;
	
	static final String NEW_FILES_UPLOADED = "9a1fe4b0-e0c4-4b9e-a9e2-913b25fb39df";
	static final String NEW_FILES_RECEIPT = "662b21b8-6d15-4696-be36-c50cbd240c8c";
	static final String SHARED_FILE_DOWNLOAD = "f5c09928-d9af-480c-83a4-93021e0779cb";
	
	@Autowired
	private ApplicationService applicationService;
	
	@Autowired
	private UserService userService; 
	
	@Autowired
	private UploadFormService uploadFormService; 
	
	@Autowired
	private PermissionService permissionService; 
	
	@Override
	public IncomingFile getIncomingFile(String uuid) {
		return objectDatabase.getObjectByUUID(IncomingFile.class, uuid);
	}

	@Override
	public Iterable<IncomingFile> getLatestFiles() {
		
		List<String> references = new ArrayList<>();
		for(UploadForm form : uploadFormService.getUserForms()) {
			references.add(form.getUuid());
		}
		return objectDatabase.getAssignedObjects(IncomingFile.class, getCurrentUser(), 
				SortOrder.DESC, "created",
				SearchField.gt("created", Utils.thirtyDaysAgo()));
	}

	@Override
	public void delete(IncomingFile file) {
		permissionService.assertAssignment(file);
		objectDatabase.deleteObject(file);
	}

	@Override
	public void save(IncomingFile file, VirtualFolder folder, UploadForm uploadForm) {
		
		file.setUploadReference(uploadForm);
		
		objectDatabase.saveOrUpdate(file);
		
		StaticResolver resolver = new StaticResolver();
		resolver.addToken("serverName", Request.get().getServerName());
		resolver.addToken("files", file.getUploadPaths());
		resolver.addToken("uploaderEmail", file.getEmail());
		resolver.addToken("uploaderName", file.getName());
		resolver.addToken("uploaderRef", file.getReference());
		resolver.addToken("shareName", file.getUploadArea());
		resolver.addToken("uuid", file.getUuid());
		
		List<RecipientHolder> emailAddresses =  new ArrayList<>();
		emailAddresses.add(new RecipientHolder(file.getName(), file.getEmail()));
		
		MessageService messageService = applicationService.getBean(MessageService.class);
		
		messageService.sendMessage(NEW_FILES_RECEIPT, resolver, emailAddresses);
		
		emailAddresses.clear();
		
		/**
		 * Populate from users assigned to public upload area
		 */
		
		AssignmentNotificationPreference preference = uploadForm.getNotifyAssignedUsers();
		var uuids = new HashSet<User>();
		
		if(preference != AssignmentNotificationPreference.DO_NOT_NOTIFY) {
			uuids.addAll(folder.getUsers());
			
			if(preference != AssignmentNotificationPreference.IGNORE_ROLES) {
				for(Role role : folder.getRoles()) {
					if(role.isAllUsers()) {
						if(preference != AssignmentNotificationPreference.IGNORE_EVERYONE_ROLE) {
							for(User user : userService.allObjects()) {
								uuids.add(user);
							}
						}
					} else {
						uuids.addAll(role.getUsers());
					}
				}
			}
		}

		uuids.forEach((User uuid) -> {
			emailAddresses.add(new RecipientHolder(uuid));
        });
		
		Collection<String> other = uploadForm.getOtherEmails();
		if(Objects.nonNull(other)) {
			for(String email : uploadForm.getOtherEmails()) {
				emailAddresses.add(new RecipientHolder(email));
			}
		}
		
		messageService.sendMessage(NEW_FILES_UPLOADED, resolver, emailAddresses);
	}
	
	
}
