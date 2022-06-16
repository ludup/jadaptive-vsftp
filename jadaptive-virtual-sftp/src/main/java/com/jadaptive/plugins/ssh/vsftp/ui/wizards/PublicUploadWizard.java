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

import com.jadaptive.api.db.TransactionService;
import com.jadaptive.api.permissions.AccessDeniedException;
import com.jadaptive.api.permissions.PermissionService;
import com.jadaptive.api.role.Role;
import com.jadaptive.api.role.RoleService;
import com.jadaptive.api.setup.WizardSection;
import com.jadaptive.api.ui.Page;
import com.jadaptive.api.ui.PageCache;
import com.jadaptive.api.user.User;
import com.jadaptive.api.user.UserService;
import com.jadaptive.api.wizards.AbstractWizard;
import com.jadaptive.api.wizards.WizardState;
import com.jadaptive.plugins.ssh.vsftp.AnonymousUserDatabaseImpl;
import com.jadaptive.plugins.ssh.vsftp.FileScheme;
import com.jadaptive.plugins.ssh.vsftp.VirtualFileService;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderCredentials;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderPath;
import com.jadaptive.plugins.ssh.vsftp.uploads.UploadForm;
import com.jadaptive.plugins.ssh.vsftp.uploads.UploadFormService;
import com.jadaptive.utils.ObjectUtils;
import com.sshtools.common.util.FileUtils;

@Extension
@Component
public class PublicUploadWizard extends AbstractWizard {

	public static final String RESOURCE_KEY = "publicUploadWizard";

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
	
	
	public static final String STATE_ATTR = "publicUploadWizardState";
	
	@Override
	public String getResourceKey() {
		return RESOURCE_KEY;
	}

	@Override
	public Page getCompletePage() throws FileNotFoundException {
		return pageCache.resolvePage(PublicUploadComplete.class);
	}

	@Override
	protected Class<? extends PublicUploadSection> getSectionClass() {
		return PublicUploadSection.class;
	}

	@Override
	protected String getStateAttribute() {
		return STATE_ATTR;
	}

	@Override
	protected WizardSection getFinishSection() {
		return new PublicUploadSection(getResourceKey(), "publicUploadFinish", "PublicUploadFinish.html");
	}

	@Override
	protected WizardSection getStartSection() {
		return new PublicUploadSection(getResourceKey(), "publicUploadStart", "PublicUploadStart.html");
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
	}

	@Override
	public void finish(WizardState state) {
		
		transactionService.executeTransaction(()->{
			
			try {
				String folderType = (String) state.getParameter(REQUEST_PARAM_TYPE);
				FileScheme<?> scheme = fileService.getFileScheme(folderType);

				PublicUploadName name = ObjectUtils.assertObject(state.getObject(PublicUploadStep1.class), PublicUploadName.class);
				PublicUploadAssignment assignments = ObjectUtils.assertObject(state.getObject(PublicUploadStep3.class), PublicUploadAssignment.class);
				VirtualFolderPath path = ObjectUtils.assertObject(state.getObject(PublicUploadStep2.class), scheme.getPathClass());
				PublicUploadOptions options = ObjectUtils.assertObject(state.getObject(PublicUploadStep4.class), PublicUploadOptions.class);
				
				VirtualFolderCredentials creds = null;
				if(scheme.requiresCredentials()) {
					creds = ObjectUtils.assertObject(state.getObject(CredentialsSetupSection.class), scheme.getCredentialsClass());
				}

				
				String virtualPath = FileUtils.checkEndsWithSlash(name.getVirtualPath()) + name.getName();
				
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
