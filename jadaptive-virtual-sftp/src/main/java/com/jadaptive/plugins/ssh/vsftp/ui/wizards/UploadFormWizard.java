package com.jadaptive.plugins.ssh.vsftp.ui.wizards;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jadaptive.api.app.ApplicationService;
import com.jadaptive.api.db.TransactionService;
import com.jadaptive.api.permissions.AccessDeniedException;
import com.jadaptive.api.permissions.PermissionService;
import com.jadaptive.api.role.Role;
import com.jadaptive.api.role.RoleService;
import com.jadaptive.api.ui.Page;
import com.jadaptive.api.ui.PageCache;
import com.jadaptive.api.ui.wizards.AbstractWizard;
import com.jadaptive.api.ui.wizards.WizardSection;
import com.jadaptive.api.ui.wizards.WizardState;
import com.jadaptive.api.user.User;
import com.jadaptive.api.user.UserService;
import com.jadaptive.plugins.licensing.FeatureEnablementService;
import com.jadaptive.plugins.ssh.vsftp.AnonymousUserDatabaseImpl;
import com.jadaptive.plugins.ssh.vsftp.FileScheme;
import com.jadaptive.plugins.ssh.vsftp.VirtualFileService;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderCredentials;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderPath;
import com.jadaptive.plugins.ssh.vsftp.links.SharedFileService;
import com.jadaptive.plugins.ssh.vsftp.uploads.UploadForm;
import com.jadaptive.plugins.ssh.vsftp.uploads.UploadFormService;
import com.jadaptive.utils.ObjectUtils;
import com.sshtools.common.util.FileUtils;

@Extension
@Component
public class UploadFormWizard extends AbstractWizard {

	public static final String RESOURCE_KEY = "uploadForm";

	public static final String REQUEST_PARAM_TYPE = "type";
	
	@Autowired
	private PageCache pageCache; 
	
	@Autowired
	private PermissionService permissionService; 
	
	@Autowired
	private VirtualFileService fileService; 
	
	@Autowired
	private UserService userService; 
	
	@Autowired
	private RoleService roleService; 
	
	@Autowired
	private UploadFormService uploadService; 
	
	@Autowired
	private TransactionService transactionService; 
	
	@Autowired
	private ApplicationService applicationService; 
	
	public static final String STATE_ATTR = "uploadFormWizardState";
	
	@Override
	public String getResourceKey() {
		return RESOURCE_KEY;
	}

	@Override
	public Page getCompletePage() throws FileNotFoundException {
		return pageCache.resolvePage(UploadFormComplete.class);
	}

	@Override
	protected Class<? extends UploadFormSection> getSectionClass() {
		return UploadFormSection.class;
	}

	@Override
	protected String getStateAttribute() {
		return STATE_ATTR;
	}

	@Override
	protected WizardSection getFinishSection() {
		return new UploadFormSection(getResourceKey(), "publicUploadFinish", "PublicUploadFinish.html");
	}

	@Override
	protected WizardSection getStartSection() {
		return new UploadFormSection(getResourceKey(), "publicUploadStart", "PublicUploadStart.html");
	}

	@Override
	protected Collection<? extends WizardSection> getDefaultSections() {
		return Arrays.asList();
	}

	@Override
	protected void assertPermissions(WizardState state) throws AccessDeniedException {
		if(!permissionService.hasUserContext()) {
			throw new AccessDeniedException("No user context available for public upload wizard!");
		}
		
		applicationService.getBean(FeatureEnablementService.class).assertFeature(SharedFileService.SHARING);
		
	}

	@Override
	public void finish(WizardState state) {
		
		transactionService.executeTransaction(()->{
			
			try {
				String folderType = (String) state.getParameter(REQUEST_PARAM_TYPE);
				FileScheme<?> scheme = fileService.getFileScheme(folderType);

				UploadFormName name = ObjectUtils.assertObject(state.getObject(UploadFormName.class), UploadFormName.class);
				UploadFormAssignment assignments = ObjectUtils.assertObject(state.getObject(UploadFormAssignment.class), UploadFormAssignment.class);
				VirtualFolderPath path = ObjectUtils.assertObject(state.getObject(scheme.getPathClass()), scheme.getPathClass());
				UploadFormOptions options = ObjectUtils.assertObject(state.getObject(UploadFormOptions.class), UploadFormOptions.class);
				
				VirtualFolderCredentials creds = null;
				if(scheme.requiresCredentials()) {
					creds = ObjectUtils.assertObject(state.getObject(scheme.getCredentialsClass()), scheme.getCredentialsClass());
				}

				
				String virtualPath = name.getVirtualPath();
				if(virtualPath.equalsIgnoreCase("/public")) {
					virtualPath = FileUtils.checkEndsWithSlash(name.getVirtualPath()) + name.getName();
				}
								
				VirtualFolder folder = scheme.createVirtualFolder(name.getName(), virtualPath, path, creds);
				
				List<User> users = new ArrayList<>();
				users.add(userService.getUserByUUID(AnonymousUserDatabaseImpl.ANONYMOUS_USER_UUID));
				users.addAll(userService.getUsersByUUID(assignments.getUsers()));
				
				List<Role> roles = new ArrayList<>();
				roles.addAll(roleService.getRolesByUUID(assignments.getRoles()));
				
				fileService.createOrUpdate(folder, 
						users,
						Arrays.asList(roleService.getEveryoneRole()));
				
				UploadForm share = new UploadForm();

				share.setVirtualPath(virtualPath);
				share.setName(folder.getName());
				share.setNotifyAssignedUsers(options.getNotifyAssignedUsers());
				share.setOtherEmails(options.getOtherEmails());
				share.setShortCode(options.getShortCode());
				
				uploadService.saveOrUpdate(share);
				
				state.setCompletedObject(share);
			} catch (IOException e) {
				throw new IllegalStateException(e.getMessage(), e);
			}

		});
		
	}

}
