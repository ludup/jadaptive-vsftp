package com.jadaptive.plugins.ssh.vsftp.ui.wizards;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jadaptive.api.repository.NamedDocument;
import com.jadaptive.api.role.Role;
import com.jadaptive.api.role.RoleService;
import com.jadaptive.api.user.User;
import com.jadaptive.api.user.UserService;
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

@Component
public class PublicUploadStep3 extends PublicUploadSection {

	private static final String REQUEST_PARAM_TYPE = "type";
	
	private static final String EXISTING_UUID = "existingUUID";
	public static final String SHORTCODE = "shortcode";
	
	@Autowired
	private VirtualFileService fileService; 
	
	@Autowired
	private RoleService roleService; 
	
	@Autowired
	private UserService userDatabase;
	
	@Autowired
	private UploadFormService uploadService;
	
	@Autowired
	private UserService userService; 
	
	public PublicUploadStep3() {
		super("publicUploadWizard", "publicUploadStep3", "PublicUploadStep3.html", 3000);
	}
	
	@Override
	public void finish(WizardState state, Integer sectionIndex) {
		
		String folderType = (String) state.getParameter(REQUEST_PARAM_TYPE);
		FileScheme<?> scheme = fileService.getFileScheme(folderType);
	
		PublicUploadName name = ObjectUtils.assertObject(state.getObjectAt(sectionIndex - (!scheme.requiresCredentials() ? 2 : 3)), PublicUploadName.class);
		PublicUploadAssignment assignments = ObjectUtils.assertObject(state.getObjectAt(sectionIndex), PublicUploadAssignment.class);
		VirtualFolderPath path = ObjectUtils.assertObject(state.getObjectAt(sectionIndex- (!scheme.requiresCredentials() ? 1 : 2)), scheme.getPathClass());
	
		VirtualFolderCredentials creds = null;
		if(scheme.requiresCredentials()) {
			creds = ObjectUtils.assertObject(state.getObjectAt(sectionIndex-1), scheme.getCredentialsClass());
		}
		
		String uuid = (String) state.getParameter(EXISTING_UUID);
		if(StringUtils.isNotBlank(uuid)) {
			VirtualFolder f = fileService.getObjectByUUID(uuid);
			fileService.deleteObject(f);
		}
		
		String virtualPath = FileUtils.checkEndsWithSlash(name.getVirtualPath()) + name.getName();
		
		VirtualFolder folder = scheme.createVirtualFolder(name.getName(), virtualPath, path, creds);
		
		List<User> users = new ArrayList<>();
		users.add(userDatabase.getUserByUUID(AnonymousUserDatabaseImpl.ANONYMOUS_USER_UUID));
		users.addAll(userDatabase.getUsersByUUID(assignments.getUsers()));
		
		List<Role> roles = new ArrayList<>();
		roles.addAll(roleService.getRolesByUUID(assignments.getRoles()));
		
		fileService.createOrUpdate(folder, 
				users,
				Arrays.asList(roleService.getEveryoneRole()));
		
		UploadForm share = new UploadForm();
	
		share.setVirtualPath(virtualPath);
		share.setName(folder.getName());
		
		uploadService.saveOrUpdate(share);
		
		state.setParameter(SHORTCODE, share.getShortCode());
		state.setParameter(EXISTING_UUID, folder.getUuid());
	}

	private String getCommaSeparatedNames(Collection<? extends NamedDocument> values) {
		
		StringBuffer buf = new StringBuffer();
		for(NamedDocument value : values) {
			if(buf.length() > 0) {
				buf.append(",");
			}
			buf.append(value.getName());
		}
				
		return buf.toString();
	}
	
	@Override
	public void processReview(Document document, WizardState state, Integer sectionIndex) {

		Element content = document.selectFirst("#setupStep");
		PublicUploadAssignment assignments = ObjectUtils.assertObject(state.getObjectAt(sectionIndex), PublicUploadAssignment.class);

		content.appendChild(new Element("div")
				.addClass("col-12 w-100 my-3")
				.appendChild(new Element("h4")
					.attr("jad:i18n", "review.assignment.header")
					.attr("jad:bundle", PublicUploadWizard.RESOURCE_KEY))
				.appendChild(new Element("p")
						.attr("jad:bundle", PublicUploadWizard.RESOURCE_KEY)
						.attr("jad:i18n", "review.assignment.desc"))
				.appendChild(new Element("div")
					.addClass("row")
					.appendChild(new Element("div")
							.addClass("col-3")
							.appendChild(new Element("span")
									.attr("jad:bundle", PublicUploadWizard.RESOURCE_KEY)
									.attr("jad:i18n", "users.name")))
					.appendChild(new Element("div")
								.addClass("col-9")
								.appendChild(new Element("span")
										.appendChild(new Element("strong")
												.text(getCommaSeparatedNames(userService.getUsersByUUID(assignments.getUsers()))))))
					.addClass("row")
					.appendChild(new Element("div")
							.addClass("col-3")
							.appendChild(new Element("span")
									.attr("jad:bundle", PublicUploadWizard.RESOURCE_KEY)
									.attr("jad:i18n", "roles.name")))
					.appendChild(new Element("div")
								.addClass("col-9")
								.appendChild(new Element("span")
										.appendChild(new Element("strong")
												.text(getCommaSeparatedNames(roleService.getRolesByUUID(assignments.getRoles()))))))));
		
	
	}
	
}
