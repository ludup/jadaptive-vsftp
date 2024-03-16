package com.jadaptive.plugins.ssh.vsftp.ui;

import java.io.IOException;
import java.util.Objects;

import org.apache.http.HttpHeaders;
import org.jsoup.nodes.Document;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;

import com.jadaptive.api.db.SingletonObjectDatabase;
import com.jadaptive.api.entity.ObjectException;
import com.jadaptive.api.quotas.QuotaService;
import com.jadaptive.api.quotas.QuotaThreshold;
import com.jadaptive.api.servlet.Request;
import com.jadaptive.api.session.SessionTimeoutException;
import com.jadaptive.api.session.SessionUtils;
import com.jadaptive.api.session.UnauthorizedException;
import com.jadaptive.api.ui.Html;
import com.jadaptive.api.ui.HtmlPage;
import com.jadaptive.api.ui.PageDependencies;
import com.jadaptive.api.ui.PageProcessors;
import com.jadaptive.plugins.ssh.vsftp.sendto.SendToConfiguration;
import com.jadaptive.plugins.ssh.vsftp.sendto.SendToService;
import com.jadaptive.plugins.ssh.vsftp.sendto.SendToServiceImpl;
import com.jadaptive.utils.Utils;

@Extension
@PageDependencies(extensions = { "jquery", "bootstrap", "fontawesome", "bootstrapTable", "jadaptive-utils" } )
@PageProcessors(extensions = { "i18n"} )
public class SendToPage extends HtmlPage {

	@Autowired
	private SendToService transferService;
	
	@Autowired
	private SingletonObjectDatabase<SendToConfiguration> configDatabase;
	
	@Autowired
	private SessionUtils sessionUtils;
	
	@Autowired
	private QuotaService quotaService;
	
	@Override
	public String getUri() {
		return "send-to";
	}

	@Override
	protected void generateContent(Document document) throws IOException {
	
		SendToConfiguration cfg = configDatabase.getObject(SendToConfiguration.class);
		if(!cfg.getAllowAnonymous()) {
			try {
				sessionUtils.getCurrentUser(Request.get());
			} catch (UnauthorizedException | SessionTimeoutException e) {
				throw new ObjectException(SendToConfiguration.RESOURCE_KEY, "authentication.required");
			}
		}
		super.generateContent(document);
		
		QuotaThreshold transferQuota = quotaService.getAssignedThreshold(quotaService.getKey(SendToServiceImpl.SEND_TO_TRANSFER_LIMIT));
		if(Objects.nonNull(transferQuota)) {
			long remaining = quotaService.getRemainingQuota(transferQuota);
			document.selectFirst("#quota")
				.addClass("col-12 mt-3")
				.appendChild(Html.div("alert alert-info me-2")
				.appendChild(Html.i("fa-solid", "fa-bars-progress"))
				.appendChild(Html.i18n(SendToConfiguration.RESOURCE_KEY, "quota.info", Utils.toByteSize(remaining, 0).toUpperCase(),
					transferQuota.getValue().toUpperCase(), transferQuota.getPeriodValue(), transferQuota.getPeriodUnit().name())));
		}
		
		String shareCode = Utils.generateRandomAlphaNumericString(6).toUpperCase();
		
		transferService.registerTransfer(shareCode);
		
		document.selectFirst("#shareCode").val(shareCode);
		
		String url = String.format("https://%s/recv/%s", Request.get().getHeader(HttpHeaders.HOST), shareCode);
		document.selectFirst(".copyLink").attr("href", url);
		document.selectFirst("#url").appendChild(
				Html.a(url)
					.addClass("text-decoration-none")
					.attr("target", "_blank")
					.text(url));
	}
	
	

}
