package com.jadaptive.plugins.ssh.vsftp.ui;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jsoup.nodes.Document;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;

import com.jadaptive.api.entity.ObjectException;
import com.jadaptive.api.entity.ObjectNotFoundException;
import com.jadaptive.api.servlet.Request;
import com.jadaptive.api.ui.HtmlPage;
import com.jadaptive.api.ui.PageCache;
import com.jadaptive.api.ui.PageDependencies;
import com.jadaptive.api.ui.PageProcessors;
import com.jadaptive.api.ui.PageRedirect;
import com.jadaptive.api.ui.RequestPage;
import com.jadaptive.api.ui.UriRedirect;
import com.jadaptive.api.user.UserService;
import com.jadaptive.plugins.ssh.vsftp.AnonymousUserDatabaseImpl;
import com.jadaptive.plugins.ssh.vsftp.links.ShareType;
import com.jadaptive.plugins.ssh.vsftp.links.SharedFile;
import com.jadaptive.plugins.ssh.vsftp.links.SharedFileService;
import com.jadaptive.plugins.sshd.SSHDService;
import com.sshtools.common.files.AbstractFile;
import com.sshtools.common.files.AbstractFileFactory;
import com.sshtools.common.permissions.PermissionDeniedException;

@Extension
@RequestPage(path = "download/{shortCode}/{filename}")
@PageDependencies(extensions = { "jquery", "bootstrap", "fontawesome", "bootstrapTable", "jadaptive-utils", "i18n"} )
@PageProcessors(extensions = { "i18n"} )
public class DownloadPublicFile extends HtmlPage {

	@Autowired
	private SharedFileService downloadService; 
	
	@Autowired
	private SSHDService sshdService; 
	
	@Autowired
	private UserService userDatabase;
	
	@Autowired
	private PageCache pageCache;

	String shortCode;
	String filename;

	
	@Override
	protected void beforeProcess(String uri, HttpServletRequest request, HttpServletResponse response)
			throws FileNotFoundException {
		
		try {
			SharedFile file = downloadService.getDownloadByShortCode(shortCode);
			
			if(file.getShareType()==ShareType.UPLOAD) {
				throw new FileNotFoundException();
			}
			
			if(!hasPassword(request, file)) {
				throw new UriRedirect(String.format("/app/ui/password-protected/%s/%s", shortCode, filename));
			} else if(!hasAcceptedTerms(request, file) && !file.getAcceptTerms()){
				throw new UriRedirect(String.format("/app/ui/download-terms/%s/%s", shortCode, file.getFilename()));
			}
		} catch(ObjectException e) {
			throw new FileNotFoundException();
		}
	}

	public static boolean hasAcceptedTerms(HttpServletRequest request, SharedFile file) {
		
		if(file.getAcceptTerms()) {
			Boolean accept = (Boolean) request.getSession().getAttribute(file.getUuid());
			return Objects.nonNull(accept) && accept.booleanValue();
		} else {
			return true;
		}
	}

	public static boolean hasPassword(HttpServletRequest request, SharedFile file) {
		
		if(file.getPasswordProtected()) {
			String sharedPassword = (String) request.getSession().getAttribute(file.getShortCode());
			if(Objects.isNull(sharedPassword)) {
				return false;
			}
			if(file.getPassword().equalsIgnoreCase(sharedPassword)) {
				return true;
			}
			return false;
		} else {
			return true;
		}
	}

	@Override
	protected void generateContent(Document document) throws IOException {
		super.generateContent(document);
		
		
		try {
			SharedFile download = downloadService.getDownloadByShortCode(shortCode);

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
			
			document.selectFirst("#downloadLink").attr("href", downloadService.getDirectLink(download));
			
		} catch (ObjectNotFoundException | PermissionDeniedException e) {
			throw new PageRedirect(pageCache.resolvePage(PublicFileNotFound.class));
		} 
	}

	
	
	@Override
	public String getUri() {
		return "download";
	}

}
