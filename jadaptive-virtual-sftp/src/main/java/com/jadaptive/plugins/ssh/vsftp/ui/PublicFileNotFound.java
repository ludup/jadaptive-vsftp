package com.jadaptive.plugins.ssh.vsftp.ui;

import java.io.IOException;

import org.jsoup.nodes.Document;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;

import com.jadaptive.api.entity.ObjectNotFoundException;
import com.jadaptive.api.servlet.Request;
import com.jadaptive.api.ui.HtmlPage;
import com.jadaptive.api.ui.PageCache;
import com.jadaptive.api.ui.PageDependencies;
import com.jadaptive.api.ui.PageProcessors;
import com.jadaptive.api.ui.PageRedirect;
import com.jadaptive.api.ui.RequestPage;
import com.jadaptive.api.user.UserService;
import com.jadaptive.plugins.ssh.vsftp.AnonymousUserDatabaseImpl;
import com.jadaptive.plugins.ssh.vsftp.VirtualFileService;
import com.jadaptive.plugins.ssh.vsftp.links.PublicDownload;
import com.jadaptive.plugins.ssh.vsftp.links.PublicDownloadService;
import com.jadaptive.plugins.sshd.SSHDService;
import com.sshtools.common.files.AbstractFile;
import com.sshtools.common.files.AbstractFileFactory;
import com.sshtools.common.permissions.PermissionDeniedException;

@Extension
@RequestPage(path = "file-not-found")
@PageDependencies(extensions = { "jquery", "bootstrap", "fontawesome", "bootstrap-tree", "bootstrapTable", "jadaptive-utils", "i18n"} )
@PageProcessors(extensions = { "i18n"} )
public class PublicFileNotFound extends HtmlPage {
	
	@Override
	protected void generateContent(Document document) throws IOException {
		super.generateContent(document);
		document.select(".ipAddress").html(Request.get().getRemoteAddr());	
	}

	String shortCode;
	
	@Override
	public String getUri() {
		return "file-not-found";
	}

}
