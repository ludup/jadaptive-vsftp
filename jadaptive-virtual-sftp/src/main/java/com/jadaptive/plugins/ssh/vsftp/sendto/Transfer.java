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
import org.springframework.beans.factory.annotation.Autowired;

import com.jadaptive.api.db.SingletonObjectDatabase;
import com.jadaptive.api.quotas.QuotaKey;
import com.jadaptive.api.quotas.QuotaService;
import com.jadaptive.api.quotas.QuotaThreshold;
import com.jadaptive.api.servlet.Request;
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

	@Autowired
	private QuotaService quotaService; 
	
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

	public synchronized void sendFile(String filename, InputStream in) throws IOException, NoSuchAlgorithmException, PermissionDeniedException {
		
		QuotaThreshold transferQuota = quotaService.getAssignedThreshold(quotaService.getKey(SendToServiceImpl.SEND_TO_TRANSFER_LIMIT));
		
		if(Objects.isNull(digestOutput)) {
			setupTransfer(count > 1 ? shareCode + ".zip" : filename);
		}
		
		SessionUtils.runIoWithoutSessionTimeout(Request.get(), () -> {
			if(Objects.nonNull(zip)) {
				zip.sendFile(filename, new QuotaEnforcingInputStream(in, transferQuota)); 
			} else {
				IOUtils.copy(new QuotaEnforcingInputStream(in, transferQuota), digestOutput);
			}	
		});
		
		
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
	
	class QuotaEnforcingInputStream extends InputStream {

		long block = 0;
		InputStream in;
		QuotaThreshold transferQuota;
		
		public QuotaEnforcingInputStream(InputStream in, QuotaThreshold transferQuota) {
			this.in = in;
			this.transferQuota = transferQuota;
		}
		@Override
		public int read() throws IOException {
			byte[] tmp = new byte[1];
			int r = read(tmp);
			if(r > 0) {
				return tmp[0] & 0xFF;
			}
			return -1;
		}

		@Override
		public int read(byte[] b, int off, int len) throws IOException {
			
			int r  = in.read(b, off, len);
			if(r > 0) {
				block += r;
				if(Objects.nonNull(transferQuota) && block >= 0x100000) {
					quotaService.incrementQuota(transferQuota, block, SendToConfiguration.RESOURCE_KEY, "transferQuotaExceeded.error");
					block = block - 0x100000;
				}
			}
			return r;
		}
		
		
		public void close() throws IOException {
			in.close();
		}
	}

}
