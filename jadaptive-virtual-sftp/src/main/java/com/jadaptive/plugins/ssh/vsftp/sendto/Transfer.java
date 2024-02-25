package com.jadaptive.plugins.ssh.vsftp.sendto;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.output.CountingOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;

import com.jadaptive.api.db.SingletonObjectDatabase;
import com.jadaptive.api.servlet.Request;
import com.jadaptive.api.session.Session;
import com.jadaptive.api.session.SessionStickyInputStream;
import com.jadaptive.api.session.SessionTimeoutException;
import com.jadaptive.api.session.SessionUtils;
import com.jadaptive.plugins.ssh.vsftp.ContentHash;
import com.jadaptive.plugins.ssh.vsftp.VFSConfiguration;
import com.sshtools.common.permissions.PermissionDeniedException;
import com.sshtools.common.util.IOUtils;

public class Transfer {

	@Autowired
	private SessionUtils sessionUtils;
	
	@Autowired
	private SingletonObjectDatabase<VFSConfiguration> configurationService; 
	
	HttpServletResponse response = null;
	Integer count;
	CountingOutputStream digestOutput = null;
	String shareCode;
	ZipTransfer zip = null;
	CountDownLatch latch = new CountDownLatch(1);
	
	public Transfer(String shareCode) {
		this.shareCode = shareCode;
	}
	
	public synchronized boolean isReceiverConnected() {
		return Objects.nonNull(response);
	}

	public boolean isReceiverConnected(Integer count) {
		this.count = count;
		return Objects.nonNull(response);
	}
	
	public void setupTransfer(String filename) throws NoSuchAlgorithmException, IOException, PermissionDeniedException {
		
		response.setHeader("Content-Disposition", "attachment; filename=\"" + filename  + "\"");
		String mimeType = URLConnection.guessContentTypeFromName(filename);
		if(StringUtils.isBlank(mimeType)) {
			mimeType = "application/octet-stream";
		}
		response.setContentType(mimeType);
		
		ContentHash contentHash = configurationService.getObject(VFSConfiguration.class).getDefaultHash();
		MessageDigest digest = MessageDigest.getInstance(contentHash.getAlgorithm());
		
		digestOutput = new CountingOutputStream(new DigestOutputStream(response.getOutputStream(), digest));
		if(count > 1) {
			zip = new ZipTransfer(digestOutput);
		}
	}

	public synchronized void sendFile(String filename, InputStream in, long contentLength) throws IOException, NoSuchAlgorithmException, PermissionDeniedException {
		
		if(Objects.isNull(digestOutput)) {
			setupTransfer(count > 1 ? shareCode + ".zip" : filename);
		}
		
		if(contentLength > 0) {
			Request.response().setContentLengthLong(contentLength);
		}
		
		Session session = sessionUtils.getActiveSession(Request.get());
		InputStream sin = new SessionStickyInputStream(in, session) {
			
			@Override
			protected void touchSession(Session session) throws IOException {
				try {
					sessionUtils.touchSession(session);
				} catch (SessionTimeoutException e) {
					throw new IOException(e.getMessage(), e);
				}
			}
		};
		
		if(Objects.nonNull(zip)) {
			zip.sendFile(filename, sin); 
		} else {
			IOUtils.copy(sin, digestOutput);
		}
		
		/**
		 * TODO event for the file itself?
		 */
	}

	public void receiveFile(HttpServletResponse response) throws InterruptedException {
		synchronized (this) {
			this.response = response;
		}
		latch.await();
	}

	public synchronized void complete() throws IOException {
		
		if(Objects.nonNull(zip)) {
			zip.close();
		}
		
		if(Objects.nonNull(digestOutput)) {
			digestOutput.close();
		}
		
		latch.countDown();
		this.response = null;
		/**
		 * TODO event for the transaction
		 */

	}

	public boolean status() {
		return latch.getCount() == 0;
	}

}
