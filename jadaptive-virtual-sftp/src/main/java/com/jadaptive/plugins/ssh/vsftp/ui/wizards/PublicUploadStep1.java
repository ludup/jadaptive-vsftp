package com.jadaptive.plugins.ssh.vsftp.ui.wizards;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import com.jadaptive.api.wizards.WizardState;
import com.jadaptive.utils.ObjectUtils;
import com.sshtools.common.util.FileUtils;

@Component
public class PublicUploadStep1 extends PublicUploadSection {

	public PublicUploadStep1() {
		super("publicUploadWizard", "publicUploadStep1", "PublicUploadStep1.html", 2000);
	}
	
	@Override
	public void finish(WizardState state, Integer sectionIndex) {
		

	}

	@Override
	public void processReview(Document document, WizardState state, Integer sectionIndex) {

		Element content = document.selectFirst("#setupStep");
		PublicUploadName name = ObjectUtils.assertObject(state.getObjectAt(sectionIndex), PublicUploadName.class);

		content.appendChild(new Element("div")
				.addClass("col-12 w-100 my-3")
				.appendChild(new Element("h4")
					.attr("jad:i18n", "review.name.header")
					.attr("jad:bundle", PublicUploadWizard.RESOURCE_KEY))
				.appendChild(new Element("p")
						.attr("jad:bundle", PublicUploadWizard.RESOURCE_KEY)
						.attr("jad:i18n", "review.name.desc"))
				.appendChild(new Element("div")
					.addClass("row")
					.appendChild(new Element("div")
							.addClass("col-3")
							.appendChild(new Element("span")
									.attr("jad:bundle", PublicUploadWizard.RESOURCE_KEY)
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
									.attr("jad:bundle", PublicUploadWizard.RESOURCE_KEY)
									.attr("jad:i18n", "virtualPath.name")))
					.appendChild(new Element("div")
								.addClass("col-9")
								.appendChild(new Element("span")
										.appendChild(new Element("strong")
												.text(FileUtils.checkEndsWithSlash(name.getVirtualPath()) + name.getName()))))));
	
	}
	
}
