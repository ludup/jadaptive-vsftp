package com.jadaptive.plugins.ssh.vsftp.sendto;

import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jadaptive.api.app.ApplicationService;
import com.jadaptive.api.cache.CacheService;
import com.jadaptive.api.permissions.AuthenticatedService;
import com.jadaptive.api.quotas.QuotaService;
import com.jadaptive.api.tenant.Tenant;
import com.jadaptive.api.tenant.TenantAware;
import com.sshtools.common.permissions.PermissionDeniedException;

@Service
public class SendToServiceImpl extends AuthenticatedService implements SendToService, TenantAware {

	public static final String SEND_TO_SIZE_LIMIT_UUID = "38412a05-6147-463d-b434-e3a47742df54";

	public static final String SEND_TO_TRANSFER_LIMIT = "16c21c5c-20ec-4560-af68-c363054cefe0";

	@Autowired
	private QuotaService quotaService; 
	
	@Autowired
	private CacheService cacheService;
	
	@Autowired
	private ApplicationService applicationService;
	
	
	Map<String,Transfer> getCache() {
		return cacheService.getCacheOrCreate("transfers", String.class, Transfer.class);
	}
	
	public void registerTransfer(String shareCode) {
		getCache().put(shareCode, applicationService.autowire(new Transfer(shareCode)));
	}

	@Override
	public boolean isReceiverConnected(String shareCode, Integer count) {
		return getCache().get(shareCode).isReceiverConnected(count);
	}

	@Override
	public void sendFile(String shareCode, String filename, InputStream in) throws NoSuchAlgorithmException, IOException, PermissionDeniedException {
		
		getCache().get(shareCode).sendFile(filename, in);
	
	}

	@Override
	public void receiveFile(String shareCode, HttpServletResponse response) throws InterruptedException {
		
		getCache().get(shareCode).receiveFile(response);
	}

	@Override
	public void completeUpload(String shareCode) throws IOException {
		
		getCache().get(shareCode).complete();
	}

	@Override
	public boolean status(String shareCode) {
		return getCache().get(shareCode).status();
	}

	@Override
	public void initializeSystem(boolean newSchema) {
		initializeTenant(getCurrentTenant(), newSchema);
	}

	@Override
	public void initializeTenant(Tenant tenant, boolean newSchema) {
		
		if(!quotaService.hasKey(SEND_TO_TRANSFER_LIMIT)) {
			quotaService.registerKey(SEND_TO_TRANSFER_LIMIT, "SendTo Transfer Limit");
		}
	}
	
	
	
}
