package com.jadaptive.plugins.ssh.vsftp.ui.wizards;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import com.jadaptive.api.ui.wizards.WizardState;
import com.jadaptive.utils.ObjectUtils;
import com.sshtools.common.util.FileUtils;

@Component
public class UploadFormStep1 extends UploadFormSection {

	public UploadFormStep1() {
		super(UploadFormWizard.RESOURCE_KEY, "publicUploadStep1", "PublicUploadStep1.html", 1000);
	}
	
	@Override
	public void processReview(Document document, WizardState state) {

		Element content = document.selectFirst("#wizardContent");
		UploadFormName name =  ObjectUtils.assertObject(state.getObject(this), UploadFormName.class);

		content.appendChild(new Element("div")
				.addClass("col-12 w-100 my-3")
				.appendChild(new Element("h4")
					.attr("jad:i18n", "review.name.header")
					.attr("jad:bundle", UploadFormWizard.RESOURCE_KEY))
				.appendChild(new Element("p")
						.attr("jad:bundle", UploadFormWizard.RESOURCE_KEY)
						.attr("jad:i18n", "review.name.desc"))
				.appendChild(new Element("div")
					.addClass("row")
					.appendChild(new Element("div")
							.addClass("col-3")
							.appendChild(new Element("span")
									.attr("jad:bundle", UploadFormWizard.RESOURCE_KEY)
									.attr("jad:i18n", "name.name")))
					.appendChild(new Element("div")
								.addClass("col-9")
								.appendChild(new Element("span")
										.appendChild(new Element("strong")
												.text(name.getName()))))
					.addClass("row")
					.appendChild(new Element("div")
							.addClass("col-3")
							.appendChild(new Element("span")
									.attr("jad:bundle", UploadFormWizard.RESOURCE_KEY)
									.attr("jad:i18n", "virtualPath.name")))
					.appendChild(new Element("div")
								.addClass("col-9")
								.appendChild(new Element("span")
										.appendChild(new Element("strong")
												.text(FileUtils.checkEndsWithSlash(name.getVirtualPath()) + name.getName()))))));
	
	}
	
}
