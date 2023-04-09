package com.jadaptive.plugins.ssh.vsftp.ui.wizards;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jadaptive.api.repository.UUIDEntity;
import com.jadaptive.api.servlet.Request;
import com.jadaptive.api.template.ObjectTemplate;
import com.jadaptive.api.template.TemplateService;
import com.jadaptive.api.ui.Page;
import com.jadaptive.api.ui.renderers.DropdownInput;
import com.jadaptive.api.ui.renderers.I18nOption;
import com.jadaptive.api.ui.wizards.WizardService;
import com.jadaptive.api.ui.wizards.WizardState;
import com.jadaptive.plugins.ssh.vsftp.FileScheme;
import com.jadaptive.plugins.ssh.vsftp.VirtualFileService;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderPath;
import com.jadaptive.plugins.ssh.vsftp.uploads.UploadForm;
import com.jadaptive.utils.ObjectUtils;

@Component
public class UploadFormStep2 extends UploadFormSection {

	private static final String REQUEST_PARAM_TYPE = "type";

	@Autowired
	private TemplateService templateService; 
	
	@Autowired
	private WizardService wizardService;
	
	@Autowired
	private VirtualFileService fileService; 
	
	public static final String SHORTCODE = "shortcode";
	
	public UploadFormStep2() {
		super(UploadForm.RESOURCE_KEY, "publicUploadStep2", "PublicUploadStep2.html");
	}

	@Override
	public void validateAndSave(UUIDEntity object, WizardState state) {
		super.validateAndSave(object, state);
		
		try {
			FileScheme scheme = fileService.getFileScheme((String) state.getParameter(REQUEST_PARAM_TYPE));
			if(scheme.requiresCredentials()) {
				state.insertNextPage(new UploadFormCredentialsSection(scheme));
			} else {
				state.removePage(UploadFormCredentialsSection.class);
			}
		} catch (IOException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	@Override
	protected void processSection(Document document, Element element, Page page) throws IOException {
		
		WizardState state = wizardService.getWizard(UploadFormWizard.RESOURCE_KEY).getState(Request.get());
		
		if(state.containsPage(UploadFormCredentialsSection.class)) {
			state.removePage(UploadFormCredentialsSection.class);
		}
		
		DropdownInput input = new DropdownInput(REQUEST_PARAM_TYPE, "selectMount");
		
		String folderType = Request.get().getParameter(REQUEST_PARAM_TYPE);
		if(StringUtils.isBlank(folderType)) {
			folderType = (String) state.getParameter(REQUEST_PARAM_TYPE);
		}
		
		List<I18nOption> values = new ArrayList<>();
		
		FileScheme scheme = null;
		if(Objects.nonNull(folderType)) {
			scheme = fileService.getFileScheme(folderType);
		}
		Collection<FileScheme> schemes = fileService.getSchemes();
		FileScheme defaultScheme = schemes.iterator().next();
		
		if(Objects.isNull(scheme)) {
			scheme = defaultScheme;
		}
		
		ObjectTemplate template = templateService.get(scheme.getResourceKey());
		
		UUIDEntity e = state.getObject(this);
		if(Objects.nonNull(e) && !e.getResourceKey().equals(scheme.getResourceKey())) {
			state.setCurrentObject(null);
		}
		
		for(FileScheme child : schemes) {
			ObjectTemplate childTemplate = templateService.get(child.getResourceKey());
			values.add(new I18nOption(childTemplate.getBundle(),
					childTemplate.getResourceKey() + ".name", 
					childTemplate.getResourceKey()));
		}
		
		Element el = input.renderInput();
		input.renderValues(values, scheme.getResourceKey());
		
		Element content = document.selectFirst("#content");
		content.appendChild(el);
		
		
		
		content.appendChild(new Element("div")
				.attr("jad:bundle", template.getBundle())
				.attr("jad:id", "objectRenderer")
				.attr("jad:handler", UploadFormWizard.RESOURCE_KEY)
				.attr("jad:disableViews", "true")
				.attr("jad:ignores", "appendUsername")
				.attr("jad:resourceKey", scheme.getPathTemplate().getResourceKey()));
		
		folderType = scheme.getResourceKey();
		state.setParameter(REQUEST_PARAM_TYPE, folderType);

	}


	@Override
	public void processReview(Document document, WizardState state) {

		try {
			Element content = document.selectFirst("#wizardContent");
			VirtualFolderPath path = ObjectUtils.assertObject(state.getObject(this), VirtualFolderPath.class);
			String folderType = (String) state.getParameter(REQUEST_PARAM_TYPE);
			FileScheme scheme = fileService.getFileScheme(folderType);
			
			content.appendChild(new Element("div")
					.addClass("col-12 w-100 my-3")
					.appendChild(new Element("h4")
						.attr("jad:i18n", "review.homeMount.header")
						.attr("jad:bundle", UploadFormWizard.RESOURCE_KEY))
					.appendChild(new Element("p")
							.attr("jad:bundle", UploadFormWizard.RESOURCE_KEY)
							.attr("jad:i18n", "review.homeMount.desc"))
					.appendChild(new Element("div")
						.addClass("row")
						.appendChild(new Element("div")
								.addClass("col-3")
								.appendChild(new Element("span")
										.attr("jad:bundle", VirtualFolder.RESOURCE_KEY)
										.attr("jad:i18n", "type.name")))
						.appendChild(new Element("div")
									.addClass("col-9")
									.appendChild(new Element("span")
											.appendChild(new Element("strong")
													.attr("jad:bundle", scheme.getBundle())
													.attr("jad:i18n", folderType + ".name"))))
						.appendChild(new Element("div")
								.addClass("col-3")
								.appendChild(new Element("span")
										.attr("jad:bundle", VirtualFolder.RESOURCE_KEY)
										.attr("jad:i18n", "destinationUri.name")))
						.appendChild(new Element("div")
									.addClass("col-9")
									.appendChild(new Element("span")
											.appendChild(new Element("strong")
													.text(path.generatePath()))))));

			
//		if(scheme.requiresCredentials()) {
//			
//			VirtualFolderCredentials creds = (VirtualFolderCredentials) state.getObject(CredentialsSetupSection.class);
//			ObjectTemplate template = templateService.get(creds.getResourceKey());
//			content.appendChild(new Element("div")
//					.attr("jad:bundle", template.getBundle())
//					.attr("jad:id", "objectRenderer")
//					.attr("jad:handler", PublicUploadWizard.RESOURCE_KEY)
//					.attr("jad:disableViews", "true")
//					.attr("jad:resourceKey", template.getResourceKey()));
//		}
//			if(creds instanceof BasicCredentials) {
//				renderBasicCredentials(info, (UsernameAndPasswordCredentials) creds);
//			} else if(creds instanceof WindowsCredentials) { 
//				info.appendChild(new Element("div")
//						.addClass("col-3")
//						.appendChild(new Element("span")
//										.attr("jad:bundle", VirtualFolder.RESOURCE_KEY)
//										.attr("jad:i18n", "domain.name")))
//				.appendChild(new Element("div")
//						.addClass("col-9")
//						.appendChild(new Element("span")
//								.appendChild(new Element("strong")
//								.text(((WindowsCredentials)creds).getDomain()))));
//				renderBasicCredentials(info, (UsernameAndPasswordCredentials) creds);
//			} else if(creds instanceof SftpCredentials) {
//				renderBasicCredentials(info, ((SftpCredentials) creds).getBasicCredentials());
//				
//				try {
//					String privateKey = ((SftpCredentials)creds).getPrivateKeyCredentials().getPrivateKey();
//					
//					if(StringUtils.isNotBlank(privateKey)) {
//
//						String passphrase = ((SftpCredentials)creds).getPrivateKeyCredentials().getPassphrase();
//						
//						SshKeyPair pair = SshKeyUtils.getPrivateKey(privateKey, passphrase);
//						
//						info.appendChild(new Element("div")
//								.addClass("col-3")
//								.appendChild(new Element("span")
//												.attr("jad:bundle", VirtualFolder.RESOURCE_KEY)
//												.attr("jad:i18n", "privatekey.name")))
//						.appendChild(new Element("div")
//								.addClass("col-9")
//								.appendChild(new Element("span")
//										.appendChild(new Element("strong")
//										.text(SshKeyUtils.getFingerprint(pair.getPublicKey())))));
//						
//						info.appendChild(new Element("div")
//								.addClass("col-3")
//								.appendChild(new Element("span")
//												.attr("jad:bundle", VirtualFolder.RESOURCE_KEY)
//												.attr("jad:i18n", "passhrase.name")))
//						.appendChild(new Element("div")
//								.addClass("col-9")
//								.appendChild(new Element("span")
//										.appendChild(new Element("strong")
//										.text(StringUtils.isBlank("") 
//												? "" 
//												: Utils.maskingString(passphrase, 2, "*")))));
//					}
//				} catch (IOException | InvalidPassphraseException e) {
//					throw new IllegalStateException(e.getMessage(), e);
//				}
//			} else {
//				// TODO render different types of credentials
//			}
		} catch (IOException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}
//	private void renderCredentials(Element element, ObjectTemplate objectTemplate) {
//		
//		element.appendChild(Html.div("col-12")
//				.attr("jad:id", "objectRenderer")
//				.attr("jad:resourceKey", objectTemplate.getResourceKey()));
//	
//	}
//	
//	private void renderBasicCredentials(Element element, UsernameAndPasswordCredentials basic) {
//		element.appendChild(new Element("div")
//				.addClass("col-3")
//				.appendChild(new Element("span")
//								.attr("jad:bundle", VirtualFolder.RESOURCE_KEY)
//								.attr("jad:i18n", "username.name")))
//		.appendChild(new Element("div")
//				.addClass("col-9")
//				.appendChild(new Element("span")
//						.appendChild(new Element("strong")
//						.text(basic.getUsername()))));
//		
//		element.appendChild(new Element("div")
//				.addClass("col-3")
//				.appendChild(new Element("span")
//								.attr("jad:bundle", VirtualFolder.RESOURCE_KEY)
//								.attr("jad:i18n", "password.name")))
//		.appendChild(new Element("div")
//				.addClass("col-9")
//				.appendChild(new Element("span")
//						.appendChild(new Element("strong")
//						.text(Utils.maskingString(basic.getPassword(), 2, "*")))));
//	}
	
	
}
