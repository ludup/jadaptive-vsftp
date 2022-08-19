package com.jadaptive.plugins.ssh.vsftp.ui.wizards;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import com.jadaptive.api.ui.wizards.WizardState;
import com.jadaptive.utils.ObjectUtils;
import com.jadaptive.utils.Utils;

@Component
public class PublicUploadStep4 extends PublicUploadSection {

	public PublicUploadStep4() {
		super(PublicUploadWizard.RESOURCE_KEY, "publicUploadStep4", "PublicUploadStep4.html");
	}

	@Override
	public void processReview(Document document, WizardState state) {

		Element content = document.selectFirst("#wizardContent");
		PublicUploadOptions options = ObjectUtils.assertObject(state.getObject(this), PublicUploadOptions.class);
		
		content.appendChild(new Element("div")
				.addClass("col-12 w-100 my-3")
				.appendChild(new Element("h4")
					.attr("jad:i18n", "review.options.header")
					.attr("jad:bundle", PublicUploadWizard.RESOURCE_KEY))
				.appendChild(new Element("p")
						.attr("jad:bundle", PublicUploadWizard.RESOURCE_KEY)
						.attr("jad:i18n", "review.options.desc"))
				.appendChild(new Element("div")
					.addClass("row")
					.appendChild(new Element("div")
							.addClass("col-3")
							.appendChild(new Element("span")
									.attr("jad:bundle", PublicUploadWizard.RESOURCE_KEY)
									.attr("jad:i18n", "shortCode.name")))
					.appendChild(new Element("div")
								.addClass("col-9")
								.appendChild(new Element("span")
										.appendChild(new Element("strong")
												.text(options.getShortCode()))))
					.addClass("row")
					.appendChild(new Element("div")
							.addClass("col-3")
							.appendChild(new Element("span")
									.attr("jad:bundle", PublicUploadWizard.RESOURCE_KEY)
									.attr("jad:i18n", "notifyAssignedUsers.name")))
					.appendChild(new Element("div")
								.addClass("col-9")
								.appendChild(new Element("span")
										.appendChild(new Element("strong")
												.text(options.getNotifyAssignedUsers().name().replace('_', ' ')))))
					.appendChild(new Element("div")
							.addClass("col-3")
							.appendChild(new Element("span")
									.attr("jad:bundle", PublicUploadWizard.RESOURCE_KEY)
									.attr("jad:i18n", "otherEmails.name")))
					.appendChild(new Element("div")
								.addClass("col-9")
								.appendChild(new Element("span")
										.appendChild(new Element("strong")
												.text(Utils.csv(options.getOtherEmails())))))));
	
	}
}
