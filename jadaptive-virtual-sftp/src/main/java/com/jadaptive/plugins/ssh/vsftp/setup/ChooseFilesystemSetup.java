package com.jadaptive.plugins.ssh.vsftp.setup;

import java.io.IOException;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.pf4j.Extension;

import com.jadaptive.api.repository.UUIDEntity;
import com.jadaptive.api.setup.SetupSection;
import com.jadaptive.api.template.ValidationException;
import com.jadaptive.api.ui.Page;
import com.jadaptive.api.wizards.WizardState;
import com.jadaptive.plugins.web.ui.Wizard;
import com.jadaptive.utils.ObjectUtils;

@Extension
public class ChooseFilesystemSetup extends SetupSection {

	public static final String RESOURCE_KEY = "chooseFilesystem";
	
	public static final String REQUEST_PARAM_TYPE = "filesystemType";
	
	public ChooseFilesystemSetup() {
		super(RESOURCE_KEY, RESOURCE_KEY, "ChooseFilesystemSetup.html");
	}

	@Override
	public void process(Document document, Element element, Page page) throws IOException {
		
		try {
			WizardState state = Wizard.getCurrentState();
			ChooseFilesystem obj = ObjectUtils.assertObject(state.getObject(getClass()), ChooseFilesystem.class);
			
			document.selectFirst("#filesystemType").val(obj.getFilesystemType().toString());
		} catch(ValidationException t) {
		}
		super.process(document, element, page);
	}

	
	@Override
	protected void onValidate(UUIDEntity object, WizardState state) {
		
		ChooseFilesystem obj = ObjectUtils.assertObject(object, ChooseFilesystem.class);
		switch(obj.getFilesystemType()) {
		case 1:
		case 2:
			if(!state.containsPage(CreateMount.class)) {
				state.insertNextPage(new CreateMount());
			}
			break;
		default:
			state.removePage(CreateMount.class);
			break;
		}
	}

	@Override
	public void processReview(Document document, WizardState state) {
		
		Element content = document.selectFirst("#setupStep");
		ChooseFilesystem obj = ObjectUtils.assertObject(state.getObject(getClass()), ChooseFilesystem.class);
		
		content	.appendChild(new Element("div")
						.addClass("col-12 w-100 my-3")
						.appendChild(new Element("h4")
							.attr("jad:i18n", "review.chooseFilesystem.header")
							.attr("jad:bundle", getBundle()))
					.appendChild(new Element("p")
							.attr("jad:bundle", getBundle())
							.attr("jad:i18n", "review.chooseFilesystem.desc"))
					.appendChild(new Element("div")
							.addClass("row")
							.appendChild(new Element("div")
									.addClass("col-3")
									.appendChild(new Element("span")
											.attr("jad:bundle", getBundle())
											.attr("jad:i18n", "filesystemType.name")))
							.appendChild(new Element("div")
										.addClass("col-9")
										.appendChild(new Element("span")
												.appendChild(new Element("strong")
														.attr("jad:bundle", getBundle())
														.attr("jad:i18n", String.format("choice%d.review", obj.getFilesystemType())))))));
	}
	
	

}
