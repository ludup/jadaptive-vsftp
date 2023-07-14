package com.jadaptive.plugins.vsftp.dropbox;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxAuthFinish;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxSessionStore;
import com.dropbox.core.DbxStandardSessionStore;
import com.dropbox.core.DbxWebAuth;
import com.dropbox.core.TokenAccessType;
import com.dropbox.core.util.LangUtil;
import com.jadaptive.api.db.SystemSingletonObjectDatabase;
import com.jadaptive.api.encrypt.EncryptionService;
import com.jadaptive.api.entity.ObjectService;
import com.jadaptive.api.permissions.AuthenticatedController;
import com.jadaptive.api.servlet.PluginController;
import com.jadaptive.api.servlet.Request;
import com.jadaptive.api.tenant.Tenant;
import com.jadaptive.api.tenant.TenantService;
import com.jadaptive.api.ui.Feedback;
import com.jadaptive.api.ui.UriRedirect;
import com.jadaptive.plugins.ssh.vsftp.VirtualFileService;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.sshtools.common.util.ExpiringConcurrentHashMap;

@Extension
@Controller
public class DropboxController extends AuthenticatedController implements PluginController {

	static Logger log = LoggerFactory.getLogger(DropboxController.class);
	
	@Autowired
	private SystemSingletonObjectDatabase<DropboxConfiguration> dropboxConfig;
	
	@Autowired
	private EncryptionService encryptionService; 
	
	@Autowired
	private SystemSingletonObjectDatabase<DropboxConfiguration> configDatabase;
	
	@Autowired
	private ObjectService objectService; 
	
	@Autowired
	private VirtualFileService fileService; 
	
	@Autowired
	private TenantService tenantService; 
	
	private ExpiringConcurrentHashMap<String,DropboxFolder> folders = new ExpiringConcurrentHashMap<>(60000 * 15);
	private ExpiringConcurrentHashMap<String,String> folderTenants = new ExpiringConcurrentHashMap<>(60000 * 15);
	
	@RequestMapping(value = "/app/dropbox/start/{state}", method = { RequestMethod.GET })
	public void doStart(HttpServletRequest request, HttpServletResponse response,
			@PathVariable String state)
            throws IOException, ServletException {
        
		setupUserContext(request);
        
		try {
			folders.put(state, objectService.fromStash(DropboxFolder.RESOURCE_KEY, DropboxFolder.class));
		    folderTenants.put(state, request.getHeader(HttpHeaders.HOST));
	    
			String oauthDomain = configDatabase.getObject(DropboxConfiguration.class).getOauthDomain();
			if(StringUtils.isBlank(oauthDomain)) {
				oauthDomain = Request.get().getHeader(HttpHeaders.HOST);
			}
			throw new UriRedirect(String.format("https://%s/app/dropbox/process/%s", oauthDomain, state));
			
		} finally {
			clearUserContext();
		}
	}
	
	@RequestMapping(value = "/app/dropbox/process/{state}", method = { RequestMethod.GET })
	public void processOAuthRequest(HttpServletRequest request, HttpServletResponse response,
			@PathVariable String state)
            throws IOException, ServletException {
		
        DbxWebAuth.Request authRequest = DbxWebAuth.newRequestBuilder()
        	.withState(state)
            .withRedirectUri(getRedirectUri(request), getSessionStore(request))
            .withTokenAccessType(TokenAccessType.OFFLINE)
            .build();
        
        String authorizeUrl = getWebAuth(request).authorize(authRequest);

        response.sendRedirect(authorizeUrl);

    }

	@RequestMapping(value = "/app/dropbox/finish", method = { RequestMethod.GET })
	public void doFinish(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        DbxAuthFinish authFinish;
        try {
            authFinish = getWebAuth(request).finishFromRedirect(
                getRedirectUri(request),
                getSessionStore(request),
                request.getParameterMap()
            );
        } catch (DbxWebAuth.BadRequestException e) {
            log.info("On /dropbox-auth-finish: Bad request: " + e.getMessage());
            response.sendError(400);
            return;
        } catch (DbxWebAuth.BadStateException e) {
        	log.error("Bad state", e);
            response.sendRedirect(getUrl(request, "/app/dropbox/start"));
            return;
        } catch (DbxWebAuth.CsrfException e) {
        	log.info("On /dropbox-auth-finish: CSRF mismatch: " + e.getMessage());
            response.sendError(403);
            return;
        } catch (DbxWebAuth.NotApprovedException e) {
        	log.error("On /dropbox-auth-finish: Not Approved: " + e.getMessage());
        	response.sendError(403);
            return;
        } catch (DbxWebAuth.ProviderException e) {
        	log.info("On /dropbox-auth-finish: Auth failed: " + e.getMessage());
            response.sendError(503, "Error communicating with Dropbox.");
            return;
        } catch (DbxException e) {
        	log.info("On /dropbox-auth-finish: Error getting token: " + e);
            response.sendError(503, "Error communicating with Dropbox.");
            return;
        }

        String domain = folderTenants.get(authFinish.getUrlState());
        Tenant tenant = tenantService.getTenantByDomain(domain);
        tenantService.executeAs(tenant, ()->{
        	DropboxFolder folder = folders.remove(authFinish.getUrlState());
            
            if(Objects.isNull(folder)) {
            	throw new IllegalStateException("Missing folder object for oauth authentication");
            }
            
            folder.getCredentials().setAccessKey(authFinish.getAccessToken());
            folder.getCredentials().setRefreshKey(authFinish.getRefreshToken());
            
            fileService.saveOrUpdate(folder);
            Feedback.success(VirtualFolder.RESOURCE_KEY, "virtualFolder.saved", folder.getName());
        });
        
        response.sendRedirect(String.format("https://%s/app/ui/search/%s",
        		domain,
        		VirtualFolder.RESOURCE_KEY));
    }

    private DbxSessionStore getSessionStore(final HttpServletRequest request) {
       
    	HttpSession session = request.getSession(true);
        String sessionKey = "dropbox-auth-csrf-token";
        return new DbxStandardSessionStore(session, sessionKey);
    }

    private DbxWebAuth getWebAuth(final HttpServletRequest request) {
    	DropboxConfiguration config = configDatabase.getObject(DropboxConfiguration.class);
        return new DbxWebAuth(getRequestConfig(request), new DbxAppInfo(config.getAppKey(),
        		encryptionService.decrypt(config.getAppSecret())));
    }

    private String getRedirectUri(final HttpServletRequest request) {
    	DropboxConfiguration config = dropboxConfig.getObject(DropboxConfiguration.class);
    	String oathDomain = config.getOauthDomain();
    	if(StringUtils.isBlank(oathDomain)) {
    		return getUrl(request, "/app/dropbox/finish");
    	} else {
    		return "https://" + oathDomain + "/app/dropbox/finish";
    	}
    }
    
    public DbxRequestConfig getRequestConfig(HttpServletRequest request) {
        return DbxRequestConfig.newBuilder("secure-file-exchange-client")
            .withUserLocaleFrom(request.getLocale())
            .build();
    }
    
    public String getUrl(HttpServletRequest request, String path) {
        URL requestUrl;
        try {
            requestUrl = new URL(request.getRequestURL().toString());
            return new URL(requestUrl, path).toExternalForm();
        } catch (MalformedURLException ex) {
            throw LangUtil.mkAssert("Bad URL", ex);
        }
    }
}
