package com.jadaptive.plugins.ssh.vsftp.ui.wizards;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jadaptive.api.role.RoleService;
import com.jadaptive.api.user.UserService;
import com.jadaptive.api.wizards.WizardState;
import com.jadaptive.utils.ObjectUtils;
import com.jadaptive.utils.Utils;

@Component
public class PublicUploadStep3 extends PublicUploadSection {

	@Autowired
	private RoleService roleService; 
	
	@Autowired
	private UserService userService; 
	
	public PublicUploadStep3() {
		super("publicUploadWizard", "publicUploadStep3", "PublicUploadStep3.html");
	}
	
	@Override
	public void processReview(Document document, WizardState state) {

		Element content = document.selectFirst("#setupStep");
		PublicUploadAssignment assignments = ObjectUtils.assertObject(state.getObject(getClass()), PublicUploadAssignment.class);

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
												.text(Utils.getCommaSeparatedNames(userService.getUsersByUUID(assignments.getUsers()))))))
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
												.text(Utils.getCommaSeparatedNames(roleService.getRolesByUUID(assignments.getRoles()))))))));
		
	
	}
	
}
