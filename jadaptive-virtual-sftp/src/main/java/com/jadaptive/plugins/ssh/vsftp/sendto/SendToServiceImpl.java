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
import com.sshtools.common.permissions.PermissionDeniedException;

@Service
public class SendToServiceImpl implements SendToService {

	
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
	public void sendFile(String shareCode, String filename, InputStream in, long contentLength) throws NoSuchAlgorithmException, IOException, PermissionDeniedException {
		
		getCache().get(shareCode).sendFile(filename, in, contentLength);
	
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
	
}
