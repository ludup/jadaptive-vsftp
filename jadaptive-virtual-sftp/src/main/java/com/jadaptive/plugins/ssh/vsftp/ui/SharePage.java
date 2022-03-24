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
@RequestPage(path = "share/{shortCode}")
public class SharePage extends HtmlPage {

	@Autowired
	private SharedFileService downloadService; 

	String shortCode;

	
	@Override
	protected void beforeProcess(String uri, HttpServletRequest request, HttpServletResponse response)
			throws FileNotFoundException {
		
		try {
			SharedFile file = downloadService.getDownloadByShortCode(shortCode);
			
			if(file.getShareType()==ShareType.UPLOAD) {
				throw new UriRedirect(String.format("/app/ui/incoming/%s", shortCode));
			} else {
				throw new UriRedirect(String.format("/app/ui/incoming/%s/%s", shortCode, file.getFilename()));
			}
		} catch(ObjectException e) {
			throw new FileNotFoundException();
		}
	}
	
	@Override
	public String getUri() {
		return "share";
	}

}
