package com.jadaptive.plugins.ssh.vsftp.ui;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.jsoup.nodes.Document;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;

import com.jadaptive.api.stats.UsageService;
import com.jadaptive.api.ui.AuthenticatedPage;
import com.jadaptive.api.ui.PageDependencies;
import com.jadaptive.api.ui.PageProcessors;
import com.jadaptive.api.ui.RequestPage;
import com.jadaptive.plugins.ssh.vsftp.VirtualFileService;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.jadaptive.plugins.ssh.vsftp.stats.StatsService;
import com.jadaptive.utils.Utils;
import com.sshtools.common.files.AbstractFile;
import com.sshtools.common.permissions.PermissionDeniedException;

@Extension
@RequestPage(path = "mount-report/{uuid}")
@PageDependencies(extensions = { "jquery", "bootstrap", "fontawesome", "jadaptive-utils"} )
@PageProcessors(extensions = { "i18n"} )
public class MountReport extends AuthenticatedPage {

	@Autowired
	VirtualFileService fileService; 
	
	@Autowired
	UsageService usageService;
	
	String uuid;
	
	public MountReport() {
	}
	
	@Override
	protected void generateAuthenticatedContent(Document document) throws FileNotFoundException, IOException {
		super.generateAuthenticatedContent(document);
		document.selectFirst("#uuid").val(uuid);
	}
	
	
	@Override
	public String getUri() {
		return "mount-report";
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
}
