package com.jadaptive.plugins.ssh.vsftp.ui;

import java.io.IOException;

import org.apache.http.HttpHeaders;
import org.jsoup.nodes.Document;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;

import com.jadaptive.api.servlet.Request;
import com.jadaptive.api.ui.Html;
import com.jadaptive.api.ui.HtmlPage;
import com.jadaptive.api.ui.PageDependencies;
import com.jadaptive.api.ui.PageProcessors;
import com.jadaptive.plugins.ssh.vsftp.sendto.SendToService;
import com.jadaptive.utils.Utils;

@Extension
@PageDependencies(extensions = { "jquery", "bootstrap", "fontawesome", "bootstrapTable", "jadaptive-utils" } )
@PageProcessors(extensions = { "i18n"} )
public class SendToPage extends HtmlPage {

	@Autowired
	private SendToService transferService;
	
	@Override
	public String getUri() {
		return "send-to";
	}

	@Override
	protected void generateContent(Document document) throws IOException {
		
		super.generateContent(document);
		
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
