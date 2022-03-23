package com.jadaptive.plugins.ssh.vsftp.ui.wizards;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jadaptive.api.repository.UUIDEntity;
import com.jadaptive.api.role.Role;
import com.jadaptive.api.role.RoleService;
import com.jadaptive.api.servlet.Request;
import com.jadaptive.api.setup.SetupSection;
import com.jadaptive.api.template.ObjectTemplate;
import com.jadaptive.api.template.TemplateService;
import com.jadaptive.api.ui.Page;
import com.jadaptive.api.ui.renderers.DropdownInput;
import com.jadaptive.api.ui.renderers.I18nOption;
import com.jadaptive.api.user.User;
import com.jadaptive.api.user.UserService;
import com.jadaptive.api.wizards.WizardService;
import com.jadaptive.api.wizards.WizardState;
import com.jadaptive.plugins.ssh.vsftp.AnonymousUserDatabaseImpl;
import com.jadaptive.plugins.ssh.vsftp.FileScheme;
import com.jadaptive.plugins.ssh.vsftp.VirtualFileService;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderCredentials;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderPath;
import com.jadaptive.plugins.ssh.vsftp.folders.LocalFolder;
import com.jadaptive.plugins.ssh.vsftp.links.ShareType;
import com.jadaptive.plugins.ssh.vsftp.links.SharedFile;
import com.jadaptive.plugins.ssh.vsftp.links.SharedFileService;
import com.jadaptive.plugins.ssh.vsftp.schemes.BasicCredentials;
import com.jadaptive.plugins.ssh.vsftp.schemes.SftpCredentials;
import com.jadaptive.plugins.ssh.vsftp.schemes.UsernameAndPasswordCredentials;
import com.jadaptive.plugins.ssh.vsftp.schemes.WindowsCredentials;
import com.jadaptive.utils.ObjectUtils;
import com.jadaptive.utils.Utils;
import com.sshtools.common.publickey.InvalidPassphraseException;
import com.sshtools.common.publickey.SshKeyUtils;
import com.sshtools.common.ssh.components.SshKeyPair;
import com.sshtools.common.util.FileUtils;

@Component
public class PublicUploadStep2 extends PublicUploadSection {

	private static final String REQUEST_PARAM_TYPE = "type";

	@Autowired
	private TemplateService templateService; 
	
	@Autowired
	private WizardService wizardService;
	
	@Autowired
	private VirtualFileService fileService; 
	
	@Autowired
	private RoleService roleService; 
	
	@Autowired
	private UserService userDatabase;
	
	@Autowired
	private SharedFileService sharingService;
	
	private static final String EXISTING_UUID = "existingUUID";
	public static final String SHORTCODE = "shortcode";
	
	public PublicUploadStep2() {
		super("publicUploadWizard", "publicUploadStep2", "PublicUploadStep2.html", 2000);
	}

	@Override
	public void validateAndSave(UUIDEntity object, WizardState state) {
		super.validateAndSave(object, state);
		
		FileScheme<?> scheme = fileService.getFileScheme((String) state.getParameter(REQUEST_PARAM_TYPE));
		if(scheme.requiresCredentials()) {
			state.insertNextPage(new CredentialsSetupSection(scheme));
		}
	}
	
	@Override
	public void finish(WizardState state, Integer sectionIndex) {
		
		
	}
	
	@Override
	public void process(Document document, Element element, Page page) throws IOException {
		
		WizardState state = wizardService.getWizard("publicUploadWizard").getState(Request.get());
		
		if(state.containsPage(CredentialsSetupSection.class)) {
			state.removePage(CredentialsSetupSection.class);
		}
		
		String folderType = Request.get().getParameter(REQUEST_PARAM_TYPE);
		if(StringUtils.isBlank(folderType)) {
			folderType = (String) state.getParameter(REQUEST_PARAM_TYPE);
			if(StringUtils.isBlank(folderType)) {
				folderType = LocalFolder.RESOURCE_KEY;
			}
		}
		
		DropdownInput input = new DropdownInput(REQUEST_PARAM_TYPE, "selectMount");
		
		ObjectTemplate template = templateService.get(VirtualFolder.RESOURCE_KEY);
		
		List<I18nOption> values = new ArrayList<>();
		
		for(String child : template.getChildTemplates()) {
			ObjectTemplate childTemplate = templateService.get(child);
			values.add(new I18nOption(childTemplate.getBundle(),
					childTemplate.getResourceKey() + ".name", 
					childTemplate.getResourceKey()));
		}
		
		Element el = input.renderInput();
		input.renderValues(values, folderType);
		
		Element content = document.selectFirst("#content");
		content.appendChild(el);
		
		FileScheme<?> scheme = fileService.getFileScheme(folderType);
		
		content.appendChild(new Element("div")
				.attr("jad:bundle", template.getBundle())
				.attr("jad:id", "objectRenderer")
				.attr("jad:handler", PublicUploadWizard.RESOURCE_KEY)
				.attr("jad:disableViews", "true")
				.attr("jad:ignores", "appendUsername")
				.attr("jad:resourceKey", scheme.getPathTemplate().getResourceKey()));
		
		state.setParameter(REQUEST_PARAM_TYPE, folderType);
		super.process(document, element, page);
	}


	@Override
	public void processReview(Document document, WizardState state, Integer sectionIndex) {

		Element content = document.selectFirst("#setupStep");
		VirtualFolderPath path = ObjectUtils.assertObject(state.getObjectAt(sectionIndex), VirtualFolderPath.class);
		String folderType = (String) state.getParameter(REQUEST_PARAM_TYPE);
		FileScheme<?> scheme = fileService.getFileScheme(folderType);
		
		Element info;
		content.appendChild(new Element("div")
				.addClass("col-12 w-100 my-3")
				.appendChild(new Element("h4")
					.attr("jad:i18n", "review.homeMount.header")
					.attr("jad:bundle", PublicUploadWizard.RESOURCE_KEY))
				.appendChild(new Element("p")
						.attr("jad:bundle", PublicUploadWizard.RESOURCE_KEY)
						.attr("jad:i18n", "review.homeMount.desc"))
				.appendChild(info = new Element("div")
					.addClass("row")
					.appendChild(new Element("div")
							.addClass("col-3")
							.appendChild(new Element("span")
									.attr("jad:bundle", "selectMount")
									.attr("jad:i18n", "type.name")))
					.appendChild(new Element("div")
								.addClass("col-9")
								.appendChild(new Element("span")
										.appendChild(new Element("strong")
												.attr("jad:bundle", "virtualFolder")
												.attr("jad:i18n", folderType + ".name"))))
					.appendChild(new Element("div")
							.addClass("col-3")
							.appendChild(new Element("span")
									.attr("jad:bundle", "selectMount")
									.attr("jad:i18n", "path.name")))
					.appendChild(new Element("div")
								.addClass("col-9")
								.appendChild(new Element("span")
										.appendChild(new Element("strong")
												.text(path.generatePath()))))));
	
		
		if(scheme.requiresCredentials()) {
			
			VirtualFolderCredentials creds = (VirtualFolderCredentials) state.getObjectAt(sectionIndex+1);
			if(creds instanceof BasicCredentials) {
				renderBasicCredentials(info, (UsernameAndPasswordCredentials) creds);
			} else if(creds instanceof WindowsCredentials) { 
				info.appendChild(new Element("div")
						.addClass("col-3")
						.appendChild(new Element("span")
										.attr("jad:bundle", VirtualFolder.RESOURCE_KEY)
										.attr("jad:i18n", "domain.name")))
				.appendChild(new Element("div")
						.addClass("col-9")
						.appendChild(new Element("span")
								.appendChild(new Element("strong")
								.text(((WindowsCredentials)creds).getDomain()))));
				renderBasicCredentials(info, (UsernameAndPasswordCredentials) creds);
			} else if(creds instanceof SftpCredentials) {
				renderBasicCredentials(info, ((SftpCredentials) creds).getBasicCredentials());
				
				try {
					String privateKey = ((SftpCredentials)creds).getPrivateKeyCredentials().getPrivateKey();
					
					if(StringUtils.isNotBlank(privateKey)) {

						String passphrase = ((SftpCredentials)creds).getPrivateKeyCredentials().getPassphrase();
						
						SshKeyPair pair = SshKeyUtils.getPrivateKey(privateKey, passphrase);
						
						info.appendChild(new Element("div")
								.addClass("col-3")
								.appendChild(new Element("span")
												.attr("jad:bundle", VirtualFolder.RESOURCE_KEY)
												.attr("jad:i18n", "privatekey.name")))
						.appendChild(new Element("div")
								.addClass("col-9")
								.appendChild(new Element("span")
										.appendChild(new Element("strong")
										.text(SshKeyUtils.getFingerprint(pair.getPublicKey())))));
						
						info.appendChild(new Element("div")
								.addClass("col-3")
								.appendChild(new Element("span")
												.attr("jad:bundle", VirtualFolder.RESOURCE_KEY)
												.attr("jad:i18n", "passhrase.name")))
						.appendChild(new Element("div")
								.addClass("col-9")
								.appendChild(new Element("span")
										.appendChild(new Element("strong")
										.text(StringUtils.isBlank("") 
												? "" 
												: Utils.maskingString(passphrase, 2, "*")))));
					}
				} catch (IOException | InvalidPassphraseException e) {
					throw new IllegalStateException(e.getMessage(), e);
				}
			} else {
				// TODO render different types of credentials
			}
		}
	}
	
	private void renderBasicCredentials(Element element, UsernameAndPasswordCredentials basic) {
		element.appendChild(new Element("div")
				.addClass("col-3")
				.appendChild(new Element("span")
								.attr("jad:bundle", VirtualFolder.RESOURCE_KEY)
								.attr("jad:i18n", "username.name")))
		.appendChild(new Element("div")
				.addClass("col-9")
				.appendChild(new Element("span")
						.appendChild(new Element("strong")
						.text(basic.getUsername()))));
		
		element.appendChild(new Element("div")
				.addClass("col-3")
				.appendChild(new Element("span")
								.attr("jad:bundle", VirtualFolder.RESOURCE_KEY)
								.attr("jad:i18n", "password.name")))
		.appendChild(new Element("div")
				.addClass("col-9")
				.appendChild(new Element("span")
						.appendChild(new Element("strong")
						.text(Utils.maskingString(basic.getPassword(), 2, "*")))));
	}
	
	class CredentialsSetupSection extends SetupSection {

		FileScheme<?> scheme;
		public CredentialsSetupSection(FileScheme<?> scheme) {
			super("publicUploadWizard", 
					"homeCredentials", 
					"HomeCredentials.html", 
					PublicUploadStep2.this.getPosition()+1);
			this.scheme = scheme;
		}

		@Override
		public void process(Document document, Element element, Page page) throws IOException {
			super.process(document, element, page);
			
			ObjectTemplate template = templateService.get(VirtualFolder.RESOURCE_KEY);
			Element content = document.selectFirst("#content");
			content.appendChild(new Element("div")
					.attr("jad:bundle", template.getBundle())
					.attr("jad:id", "objectRenderer")
					.attr("jad:handler", "setup")
					.attr("jad:disableViews", "true")
					.attr("jad:resourceKey", scheme.getCredentialsTemplate().getResourceKey()));
			
		}
	}
}
