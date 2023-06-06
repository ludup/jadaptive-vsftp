
package com.jadaptive.plugins.ssh.vsftp.setup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;

import com.jadaptive.api.repository.UUIDEntity;
import com.jadaptive.api.role.RoleService;
import com.jadaptive.api.servlet.Request;
import com.jadaptive.api.setup.SetupSection;
import com.jadaptive.api.template.ObjectTemplate;
import com.jadaptive.api.template.TemplateService;
import com.jadaptive.api.ui.Page;
import com.jadaptive.api.ui.renderers.DropdownInput;
import com.jadaptive.api.ui.renderers.I18nOption;
import com.jadaptive.api.ui.wizards.WizardService;
import com.jadaptive.api.ui.wizards.WizardState;
import com.jadaptive.api.user.User;
import com.jadaptive.plugins.ssh.vsftp.FileScheme;
import com.jadaptive.plugins.ssh.vsftp.VirtualFileService;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderCredentials;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderPath;
import com.jadaptive.utils.ObjectUtils;

public class CreateMount extends SetupSection {
	
	public static final String RESOURCE_KEY = "createMount";
	
	public static final String REQUEST_PARAM_TYPE = "type";
	
	@Autowired
	private TemplateService templateService; 
	
	@Autowired
	private WizardService wizardService;
	
	@Autowired
	private VirtualFileService fileService; 
	
	@Autowired
	private RoleService roleService; 
	
	public CreateMount() {
		super(RESOURCE_KEY, RESOURCE_KEY, "CreateMount.html");
	}
	
	@Override
	public void validateAndSave(UUIDEntity object, WizardState state) {
		super.validateAndSave(object, state);
		
		try {
			FileScheme scheme = fileService.getFileScheme((String) state.getParameter(REQUEST_PARAM_TYPE));
			if(scheme.requiresCredentials()) {
				state.insertNextPage(new CredentialsSetupSection(scheme));
			} else {
				state.removePage(CredentialsSetupSection.class);
			}
		} catch (IOException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}
	
	@Override
	public void finish(WizardState state) {
		
		try {
			String folderType = (String) state.getParameter(REQUEST_PARAM_TYPE);
			FileScheme scheme = fileService.getFileScheme(folderType);
			
			VirtualFolderPath path = ObjectUtils.assertObject(state.getObject(this), scheme.getPathClass());
			VirtualFolderCredentials creds = null;
			if(scheme.requiresCredentials()) {
				creds = ObjectUtils.assertObject(state.getObject(scheme.getCredentialsClass()), scheme.getCredentialsClass());
			}
			
			ChooseFilesystem obj = ObjectUtils.assertObject(state.getObject(ChooseFilesystem.class), ChooseFilesystem.class);
			
			VirtualFolder folder;
			switch(obj.getFilesystemType()) {
			case 1:
				path.setAppendUsername(true);
				folder  = createVirtualFolder(scheme, path, creds, "Home", "/home");
				break;
			default:
				path.setAppendUsername(false);
				folder  = createVirtualFolder(scheme, path, creds, "Home", "/");
				break;
			}

			fileService.createOrUpdate(folder, 
					Collections.<User>emptySet(),
					Arrays.asList(roleService.getEveryoneRole()));
		} catch (IOException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}

	}
	
	private VirtualFolder createVirtualFolder(FileScheme scheme, VirtualFolderPath path, VirtualFolderCredentials creds, String name, String mount) {
		VirtualFolder folder = scheme.createVirtualFolder(name, mount, path, creds);
		return folder;
	}

	@Override
	protected void processSection(Document document, Element element, Page page) throws IOException {
		
		WizardState state = wizardService.getWizard("setup").getState(Request.get());
		
		if(state.containsPage(CredentialsSetupSection.class)) {
			state.removePage(CredentialsSetupSection.class);
		}
		
		
		ObjectTemplate template = templateService.get(VirtualFolder.RESOURCE_KEY);
		
		List<I18nOption> values = new ArrayList<>();
		
		
		Collection<FileScheme> schemes = fileService.getSchemes();
		FileScheme defaultScheme = schemes.iterator().next();
		
		for(FileScheme child : schemes) {
			ObjectTemplate childTemplate = templateService.get(child.getResourceKey());
			if(child.isEnabled()) {
				values.add(new I18nOption(childTemplate.getBundle(),
						childTemplate.getResourceKey() + ".name", 
						childTemplate.getResourceKey()));
			}
		}
		
		String folderType = Request.get().getParameter(REQUEST_PARAM_TYPE);
		if(StringUtils.isBlank(folderType)) {
			folderType = (String) state.getParameter(REQUEST_PARAM_TYPE);
			if(StringUtils.isBlank(folderType)) {
				folderType = values.iterator().next().getValue();
			}
		}
		
		FileScheme scheme = fileService.getFileScheme(folderType);
		
		UUIDEntity obj = state.getCurrentObject();
		
		if(Objects.isNull(obj)) {
			obj = (UUIDEntity) Request.get().getSession().getAttribute(folderType);
			if(Objects.nonNull(obj)) {
				state.setCurrentObject(obj);
			}
		} else if(Objects.nonNull(obj)) {
			if(!obj.getResourceKey().equals(scheme.getPathTemplate().getResourceKey())) {
				Request.get().getSession().setAttribute(folderType, obj);
				state.setCurrentObject(null);
			}
		}
		
		DropdownInput input = new DropdownInput(REQUEST_PARAM_TYPE, "createMount");
		
		
		
		Element el = input.renderInput();
		input.renderValues(values, folderType);
		
		Element content = document.selectFirst("#content");
		content.appendChild(el);
		
		
		
		content.appendChild(new Element("div")
				.attr("jad:bundle", template.getBundle())
				.attr("jad:id", "objectRenderer")
				.attr("jad:handler", "setup")
				.attr("jad:disableViews", "true")
				.attr("jad:ignores", "appendUsername")
				.attr("jad:resourceKey", Objects.isNull(scheme) ? defaultScheme.getPathTemplate().getResourceKey() : scheme.getPathTemplate().getResourceKey()));
		

		state.setParameter(REQUEST_PARAM_TYPE, folderType);

	}


	@Override
	public void processReview(Document document, WizardState state) {

		try {
			Element content = document.selectFirst("#wizardContent");
			VirtualFolderPath path = ObjectUtils.assertObject(state.getObject(this), VirtualFolderPath.class);
			String folderType = (String) state.getParameter(REQUEST_PARAM_TYPE);
			FileScheme scheme = fileService.getFileScheme(folderType);
			@SuppressWarnings("unused")
			Element el;
			
			content.appendChild(new Element("div")
					.addClass("col-12 w-100 my-3")
					.appendChild(new Element("h4")
						.attr("jad:i18n", "review.homeMount.header")
						.attr("jad:bundle", "createMount"))
					.appendChild(new Element("p")
							.attr("jad:bundle", "createMount")
							.attr("jad:i18n", "review.homeMount.desc"))
					.appendChild(new Element("div")
						.addClass("row")
						.appendChild(el = new Element("div")
								.addClass("col-3")
								.appendChild(new Element("span")
										.attr("jad:bundle", "createMount")
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
										.attr("jad:bundle", "createMount")
										.attr("jad:i18n", "path.name")))
						.appendChild(new Element("div")
									.addClass("col-9")
									.appendChild(new Element("span")
											.appendChild(new Element("strong")
													.text(path.generatePath()))))));

		} catch (IOException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
		
	}
}
