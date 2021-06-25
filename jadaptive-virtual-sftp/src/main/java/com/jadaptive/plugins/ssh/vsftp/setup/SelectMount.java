package com.jadaptive.plugins.ssh.vsftp.setup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;

import com.jadaptive.api.repository.UUIDEntity;
import com.jadaptive.api.servlet.Request;
import com.jadaptive.api.setup.SetupSection;
import com.jadaptive.api.template.ObjectTemplate;
import com.jadaptive.api.template.TemplateService;
import com.jadaptive.api.ui.Page;
import com.jadaptive.api.ui.renderers.DropdownInput;
import com.jadaptive.api.ui.renderers.I18nOption;
import com.jadaptive.api.wizards.WizardService;
import com.jadaptive.api.wizards.WizardState;
import com.jadaptive.plugins.ssh.vsftp.FileScheme;
import com.jadaptive.plugins.ssh.vsftp.VirtualFileService;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderCredentials;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderPath;
import com.jadaptive.plugins.ssh.vsftp.folders.LocalFolder;
import com.jadaptive.utils.ObjectUtils;

@Extension
public class SelectMount extends SetupSection {

	private static final String REQUEST_PARAM_TYPE = "type";

	@Autowired
	private TemplateService templateService; 
	
	@Autowired
	private WizardService wizardService;
	
	@Autowired
	private VirtualFileService fileService; 
	
	public static final Integer VSFTP_SECTIONS = SetupSection.END_OF_DEFAULT + 1;
	
	public SelectMount() {
		super("selectMount", "selectMount", "SelectMount.html", VSFTP_SECTIONS + 1);
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
	public void process(Document document, Element element, Page page) throws IOException {
		
		WizardState state = wizardService.getWizard("setup").getState(Request.get());
		
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
				.attr("jad:handler", "setup")
				.attr("jad:disableViews", "true")
				.attr("jad:resourceKey", scheme.getPathTemplate().getResourceKey()));
		
		state.setParameter(REQUEST_PARAM_TYPE, folderType);
		super.process(document, element, page);
	}


	@Override
	public void processReview(Document document, WizardState state, Integer sectionIndex) {

		super.processReview(document, state, sectionIndex);
		
		Element content = document.selectFirst("#setupStep");
		VirtualFolderPath path = ObjectUtils.assertObject(state.getObjectAt(sectionIndex), VirtualFolderPath.class);
		String folderType = (String) state.getParameter(REQUEST_PARAM_TYPE);
		
		content.appendChild(new Element("div")
				.addClass("col-12 w-100 my-3")
				.appendChild(new Element("h4")
					.attr("jad:i18n", "review.homeMount.header")
					.attr("jad:bundle", "selectMount"))
				.appendChild(new Element("p")
						.attr("jad:bundle", "selectMount")
						.attr("jad:i18n", "review.homeMount.desc"))
				.appendChild(new Element("div")
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
												.text(path.getDestinationUri()))))
					.appendChild(new Element("div")
							.addClass("col-3")
							.appendChild(new Element("span")
											.attr("jad:bundle", "selectMount")
											.attr("jad:i18n", "cacheStrategy.name")))
					.appendChild(new Element("div")
							.addClass("col-9")
							.appendChild(new Element("span")
									.appendChild(new Element("strong")
									.text(path.getCacheStrategy().name()))))));
	
	}
	
	class CredentialsSetupSection extends SetupSection {

		FileScheme<?> scheme;
		public CredentialsSetupSection(FileScheme<?> scheme) {
			super("setup", 
					"homeCredentials", 
					"HomeCredentials.html", 
					SelectMount.this.getPosition()+1);
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

		@Override
		public void processReview(Document document, WizardState state, Integer sectionIndex) {
			
			super.processReview(document, state, sectionIndex);
			
			Element content = document.selectFirst("#setupStep");
			VirtualFolderCredentials credentials = ObjectUtils.assertObject(state.getObjectAt(sectionIndex), VirtualFolderCredentials.class);
			String folderType = (String) state.getParameter(REQUEST_PARAM_TYPE);
			
//			content.appendChild(new Element("div")
//					.addClass("col-12 w-100 my-3")
//					.appendChild(new Element("h4")
//						.attr("jad:i18n", "review.homeMount.header")
//						.attr("jad:bundle", "selectMount"))
//					.appendChild(new Element("p")
//							.attr("jad:bundle", "selectMount")
//							.attr("jad:i18n", "review.homeMount.desc"))
//					.appendChild(new Element("div")
//						.addClass("row")
//						.appendChild(new Element("div")
//								.addClass("col-3")
//								.appendChild(new Element("span")
//										.attr("jad:bundle", "selectMount")
//										.attr("jad:i18n", "type.name")))
//						.appendChild(new Element("div")
//									.addClass("col-9")
//									.appendChild(new Element("span")
//											.appendChild(new Element("strong")
//													.attr("jad:bundle", "virtualFolder")
//													.attr("jad:i18n", folderType + ".name"))))
//						.appendChild(new Element("div")
//								.addClass("col-3")
//								.appendChild(new Element("span")
//										.attr("jad:bundle", "selectMount")
//										.attr("jad:i18n", "path.name")))
//						.appendChild(new Element("div")
//									.addClass("col-9")
//									.appendChild(new Element("span")
//											.appendChild(new Element("strong")
//													.text(path.getDestinationUri()))))
//						.appendChild(new Element("div")
//								.addClass("col-3")
//								.appendChild(new Element("span")
//												.attr("jad:bundle", "selectMount")
//												.attr("jad:i18n", "cacheStrategy.name")))
//						.appendChild(new Element("div")
//								.addClass("col-9")
//								.appendChild(new Element("span")
//										.appendChild(new Element("strong")
//										.text(path.getCacheStrategy().name()))))));
		}
		
		
	}
}
