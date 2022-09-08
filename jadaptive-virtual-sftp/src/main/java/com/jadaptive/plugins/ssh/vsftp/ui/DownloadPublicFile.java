package com.jadaptive.plugins.ssh.vsftp.ui;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;

import com.jadaptive.api.app.ApplicationService;
import com.jadaptive.api.entity.ObjectException;
import com.jadaptive.api.entity.ObjectNotFoundException;
import com.jadaptive.api.servlet.Request;
import com.jadaptive.api.ui.Html;
import com.jadaptive.api.ui.HtmlPage;
import com.jadaptive.api.ui.PageCache;
import com.jadaptive.api.ui.PageDependencies;
import com.jadaptive.api.ui.PageProcessors;
import com.jadaptive.api.ui.PageRedirect;
import com.jadaptive.api.ui.RequestPage;
import com.jadaptive.api.ui.UriRedirect;
import com.jadaptive.plugins.licensing.FeatureEnablementService;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.jadaptive.plugins.ssh.vsftp.links.SharedFile;
import com.jadaptive.plugins.ssh.vsftp.links.SharedFileService;
import com.jadaptive.plugins.sshd.SSHDService;
import com.jadaptive.utils.Utils;
import com.sshtools.common.files.AbstractFile;
import com.sshtools.common.files.AbstractFileFactory;
import com.sshtools.common.permissions.PermissionDeniedException;
import com.sshtools.common.util.FileUtils;

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
	private PageCache pageCache;

	@Autowired
	private ApplicationService applicationService; 
	
	String shortCode;
	String filename;

	
	@Override
	protected void beforeProcess(String uri, HttpServletRequest request, HttpServletResponse response)
			throws FileNotFoundException {
		
		try {
			
			applicationService.getBean(FeatureEnablementService.class).assertFeature(SharedFileService.SHARING);
			
			SharedFile file = downloadService.getDownloadByShortCode(shortCode);
			
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

			document.selectFirst("#message").attr("jad:arg0", Request.getRemoteAddress());
			
			AbstractFileFactory<?> factory = sshdService.getFileFactory(download.getSharedBy());
			
			if(download.getVirtualPaths().size()==1) {
				renderSingleFile(document, factory, download);
			} else {
				renderMultipleFiles(document, factory, download);
			}
			
		} catch (ObjectNotFoundException | PermissionDeniedException e) {
			throw new PageRedirect(pageCache.resolvePage(PublicFileNotFound.class));
		} 
	}

	
	
	private void renderMultipleFiles(Document document, AbstractFileFactory<?> factory, SharedFile download) {

		document.select(".filename").html(download.getFilename());
		document.selectFirst("#information").appendChild(Html.i18n(VirtualFolder.RESOURCE_KEY, "multipleFiles.text"));
		
		Element e = document.selectFirst("#downloadLinks");
		
		e.appendChild(new Element("p")
				).appendChild(Html.a(downloadService.getDirectLink(download))
						.addClass("btn btn-primary")
						.appendChild(Html.i("fa-regular", "fa-download", "me-1"))
						.appendChild(Html.i18n("default", "download.name"))
						.attr("id", "downloadLink"));
		
		Element div;
		e.appendChild(div = Html.div("mt-3").appendChild(
				Html.p(VirtualFolder.RESOURCE_KEY, "sharedContents.text")));
		
		for(String virtualPath : download.getVirtualPaths()) {
			div.appendChild(Html.a(downloadService.getDirectLink(download, virtualPath))
					.appendChild(Html.i("fa-regular", "fa-file-arrow-down", "me-1"))
					.appendChild(Html.span(FileUtils.getFilename(virtualPath), "me-3"))
					.appendChild(new Element("br")));
		}

	}

	private void renderSingleFile(Document document, AbstractFileFactory<?> factory, SharedFile download) throws PermissionDeniedException, IOException {
		
		AbstractFile fileObject  = factory.getFile(download.getVirtualPaths().iterator().next());
		
		if(!fileObject.exists()) {
			throw new PageRedirect(pageCache.resolvePage(PublicFileNotFound.class));
		}
		
		if(fileObject.isDirectory()) {
			String zipFile = fileObject.getName() + ".zip";
			document.select(".filename").html(zipFile);	
			document.selectFirst("#information").appendChild(Html.span(VirtualFolder.RESOURCE_KEY, "multipleFiles.text"));
		} else {
			document.select(".filename").html(fileObject.getName());
		}
		
		document.selectFirst("#downloadLinks").appendChild(new Element("p")
				).appendChild(Html.a(downloadService.getDirectLink(download))
						.addClass("btn btn-primary")
						.appendChild(Html.i("fa-regular", "fa-download", "me-1"))
						.appendChild(Html.i18n("default", "download.name"))
						.attr("id", "downloadLink"));
	
		document.selectFirst("#downloadLinks").appendChild(new Element("p")
				).appendChild(Html.a(
						Utils.getBaseURL(Request.get().getRequestURL().toString()) + downloadService.getDirectLink(download))
						.addClass("copyURL")
						.appendChild(Html.i("fa-regular", "fa-copy", "me-1"))
						.appendChild(Html.i18n("default", "copyURL.name")));
		
	}

	@Override
	public String getUri() {
		return "download";
	}

}
