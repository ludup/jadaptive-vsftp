package com.jadaptive.plugins.ssh.vsftp.upload;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jadaptive.api.app.ApplicationService;
import com.jadaptive.api.db.SearchField;
import com.jadaptive.api.db.TenantAwareObjectDatabase;
import com.jadaptive.api.role.Role;
import com.jadaptive.api.role.RoleService;
import com.jadaptive.api.servlet.Request;
import com.jadaptive.api.template.SortOrder;
import com.jadaptive.api.user.User;
import com.jadaptive.api.user.UserService;
import com.jadaptive.plugins.email.AssignmentNotificationPreference;
import com.jadaptive.plugins.email.MessageService;
import com.jadaptive.plugins.email.RecipientHolder;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.jadaptive.plugins.ssh.vsftp.uploads.UploadForm;
import com.jadaptive.utils.StaticResolver;
import com.jadaptive.utils.Utils;

@Service
public class IncomingFileServiceImpl implements IncomingFileService {

	@Autowired
	private TenantAwareObjectDatabase<IncomingFile> objectDatabase;
	
	static final String NEW_FILES_UPLOADED = "9a1fe4b0-e0c4-4b9e-a9e2-913b25fb39df";
	static final String NEW_FILES_RECEIPT = "662b21b8-6d15-4696-be36-c50cbd240c8c";
	static final String SHARED_FILE_DOWNLOAD = "f5c09928-d9af-480c-83a4-93021e0779cb";
	
	@Autowired
	private ApplicationService applicationService;
	
	@Autowired
	private UserService userService; 
	
	@Autowired
	private RoleService roleService; 
	
	@Override
	public IncomingFile getIncomingFile(String uuid) {
		return objectDatabase.get(uuid, IncomingFile.class);
	}

	@Override
	public Collection<IncomingFile> getLatestFiles() {
		return objectDatabase.searchTable(IncomingFile.class, 0, 5, SortOrder.DESC, "created", SearchField.gt("created", Utils.thirtyDaysAgo()));
	}

	@Override
	public void delete(IncomingFile file) {
		objectDatabase.delete(file);
	}

	@Override
	public void save(IncomingFile file, VirtualFolder folder, UploadForm uploadForm) {
		
		objectDatabase.saveOrUpdate(file);
		
		StaticResolver resolver = new StaticResolver();
		resolver.addToken("serverName", Request.get().getServerName());
		resolver.addToken("files", file.getUploadPaths());
		resolver.addToken("uploaderEmail", file.getEmail());
		resolver.addToken("uploaderName", file.getName());
		resolver.addToken("uploaderRef", file.getReference());
		resolver.addToken("shareName", file.getUploadArea());
		
		List<RecipientHolder> emailAddresses =  new ArrayList<>();
		emailAddresses.add(new RecipientHolder(file.getName(), file.getEmail()));
		
		MessageService messageService = applicationService.getBean(MessageService.class);
		
		messageService.sendMessage(NEW_FILES_RECEIPT, resolver, emailAddresses);
		
		emailAddresses.clear();
		
		/**
		 * Populate from users assigned to public upload area
		 */
		
		AssignmentNotificationPreference preference = uploadForm.getNotifyAssignedUsers();
		var uuids = new HashSet<String>();
		
		if(preference != AssignmentNotificationPreference.DO_NOT_NOTIFY) {
			uuids.addAll(folder.getUsers());
			
			if(preference != AssignmentNotificationPreference.IGNORE_ROLES) {
				for(String uuid : folder.getRoles()) {
					Role role = roleService.getRoleByUUID(uuid);
					
					if(role.isAllUsers()) {
						if(preference != AssignmentNotificationPreference.IGNORE_EVERYONE_ROLE) {
							for(User user : userService.allObjects()) {
								uuids.add(user.getUuid());
							}
						}
					} else {
						uuids.addAll(role.getUsers());
					}
				}
			}
		}

		uuids.forEach((String uuid) -> {
			emailAddresses.add(new RecipientHolder(userService.getObjectByUUID(uuid)));
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
