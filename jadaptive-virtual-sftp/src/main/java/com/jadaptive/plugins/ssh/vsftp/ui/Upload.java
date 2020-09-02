package com.jadaptive.plugins.ssh.vsftp.ui;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;

import com.codesmith.webbits.ClasspathResource;
import com.codesmith.webbits.FileUpload;
import com.codesmith.webbits.HTTPMethod;
import com.codesmith.webbits.In;
import com.codesmith.webbits.Out;
import com.codesmith.webbits.Page;
import com.codesmith.webbits.Redirect;
import com.codesmith.webbits.Request;
import com.codesmith.webbits.View;
import com.codesmith.webbits.bindable.FormBindable.FormField;
import com.codesmith.webbits.bindable.FormBindable.InputRestriction;
import com.codesmith.webbits.bootstrap.Bootstrap;
import com.codesmith.webbits.extensions.Widgets;
import com.codesmith.webbits.freemarker.FreeMarker;
import com.codesmith.webbits.widgets.Feedback;
import com.jadaptive.api.permissions.PermissionService;
import com.jadaptive.api.session.Session;
import com.jadaptive.api.session.SessionStickyInputStream;
import com.jadaptive.api.session.SessionUtils;
import com.jadaptive.plugins.ssh.vsftp.RunInAnonymousContext;
import com.jadaptive.plugins.ssh.vsftp.VirtualFileService;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.jadaptive.plugins.sshd.SSHDService;
import com.sshtools.common.files.AbstractFile;

@Page({ Bootstrap.class, Widgets.class, FreeMarker.class })
@View(contentType = "text/html", paths = { "/upload/{shortCode}" })
@ClasspathResource
public class Upload {

	public interface UploadForm {
		@InputRestriction(required = true)
		@FormField
		FileUpload getFile();

		@FormField
		@InputRestriction(required = true)
		String getName();

		@FormField
		@InputRestriction(required = true)
		String getEmail();

		@FormField
		boolean isReceipt();

		Feedback getFeedback();
	}

	@Autowired
	private SSHDService sshdService;

	@Autowired
	private VirtualFileService fileService;

	@Autowired
	private SessionUtils sessionUtils;

	@Autowired
	private PermissionService permissionService;

	String shortCode;

	public String getShortCode() {
		return shortCode;
	}

	@Out(wrappedBy = RunInAnonymousContext.class, methods = HTTPMethod.POST)
	public void uploadForm(UploadForm form, Request<?> request) throws Exception {
		/**
		 * Handler "short code" name should be assigned to an anonymous user
		 */
		VirtualFolder folder = fileService.getVirtualFolderByShortCode(request.requestPath());
		AbstractFile file = sshdService.getFileFactory(permissionService.getCurrentUser())
				.getFile(folder.getMountPath());

		if (!file.exists()) {
			throw new FileNotFoundException("No public area to place files");
		}

		file = file.resolveFile(form.getFile().name());
		if (!file.exists()) {
			file.createNewFile();
		}
		Session activeSession = sessionUtils.getActiveSession(request);
		
		/* Keep the session alive during big upload */ 
		IOUtils.copy(new SessionStickyInputStream(form.getFile().in(), activeSession, sessionUtils), file.getOutputStream());

		//
		throw new Redirect("/app/ui/upload/public");
	}

	public Document serviceAnonymous(@In Document contents) throws IOException {

		return contents;
	}
}
