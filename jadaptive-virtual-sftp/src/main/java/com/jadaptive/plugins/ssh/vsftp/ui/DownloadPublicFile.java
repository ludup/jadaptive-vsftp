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
import com.jadaptive.plugins.ssh.vsftp.links.PublicDownload;
import com.jadaptive.plugins.ssh.vsftp.links.PublicDownloadService;
import com.jadaptive.plugins.sshd.SSHDService;
import com.sshtools.common.files.AbstractFile;
import com.sshtools.common.files.AbstractFileFactory;
import com.sshtools.common.permissions.PermissionDeniedException;

@Extension
@RequestPage(path = "public-download/{shortCode}/{filename}")
@PageDependencies(extensions = { "jquery", "bootstrap", "fontawesome", "bootstrap-tree", "bootstrapTable", "jadaptive-utils", "i18n"} )
@PageProcessors(extensions = { "i18n"} )
public class DownloadPublicFile extends HtmlPage {

	@Autowired
	private PublicDownloadService downloadService; 
	
	@Autowired
	private SSHDService sshdService; 
	
	@Autowired
	private UserService userDatabase;
	
	@Autowired
	private PageCache pageCache;

	String shortCode;
	String filename;

	@Override
	protected void generateContent(Document document) throws IOException {
		super.generateContent(document);
		
		
		try {
			PublicDownload download = downloadService.getDownloadByShortCode(shortCode);

			AbstractFileFactory<?> factory = sshdService.getFileFactory(userDatabase.getUser(AnonymousUserDatabaseImpl.ANONYMOUS_USERNAME));
			AbstractFile fileObject  = factory.getFile(download.getVirtualPath());
			
			document.select(".ipAddress").html(Request.get().getRemoteAddr());
			
			if(!fileObject.exists()) {
				throw new PageRedirect(pageCache.resolvePage(PublicFileNotFound.class));
			}
			
			if(fileObject.isDirectory()) {
				String zipFile = fileObject.getName() + ".zip";
				document.select(".filename").html(zipFile);	
				
			} else {
				document.select(".filename").html(fileObject.getName());
			}
			
			document.selectFirst("#downloadLink").attr("href", download.getDirectLink());
			
		} catch (ObjectNotFoundException | PermissionDeniedException e) {
			throw new PageRedirect(pageCache.resolvePage(PublicFileNotFound.class));
		} 
	}

	
	
	@Override
	public String getUri() {
		return "public-download";
	}

}
