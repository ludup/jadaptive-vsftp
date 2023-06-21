package com.jadaptive.plugins.ssh.vsftp.ui;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.output.CountingOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.jadaptive.api.db.SingletonObjectDatabase;
import com.jadaptive.api.events.EventService;
import com.jadaptive.api.permissions.AuthenticatedController;
import com.jadaptive.api.servlet.Request;
import com.jadaptive.api.session.Session;
import com.jadaptive.api.session.SessionStickyInputStream;
import com.jadaptive.api.session.SessionTimeoutException;
import com.jadaptive.api.session.SessionUtils;
import com.jadaptive.api.ui.ErrorPage;
import com.jadaptive.api.ui.PageRedirect;
import com.jadaptive.plugins.ssh.vsftp.ContentHash;
import com.jadaptive.plugins.ssh.vsftp.VFSConfiguration;
import com.jadaptive.plugins.ssh.vsftp.VirtualFileService;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.jadaptive.plugins.ssh.vsftp.events.FileDownloadEvent;
import com.jadaptive.plugins.ssh.vsftp.events.TransferResult;
import com.jadaptive.plugins.ssh.vsftp.stats.StatsService;
import com.jadaptive.plugins.ssh.vsftp.stats.Throughput;
import com.jadaptive.plugins.ssh.vsftp.zip.ZipFolderInputStream;
import com.jadaptive.plugins.ssh.vsftp.zip.ZipMultipleFilesInputStream;
import com.jadaptive.utils.Utils;
import com.sshtools.common.files.AbstractFile;
import com.sshtools.common.permissions.PermissionDeniedException;
import com.sshtools.common.util.FileUtils;
import com.sshtools.common.util.IOUtils;
import com.sshtools.humanhash.HumanHashGenerator;

public class AbstractFileController extends AuthenticatedController {

	final static Logger log = LoggerFactory.getLogger(AbstractFileController.class);
	
	@Autowired
	private VirtualFileService fileService; 
	
	@Autowired
	private EventService eventService; 
	
	@Autowired
	private SingletonObjectDatabase<VFSConfiguration> configurationService; 

	@Autowired
	private StatsService statsService; 
	
	@Autowired
	private SessionUtils sessionUtils;
	
	protected void sendFileOrZipFolder(String path, AbstractFile fileObject, HttpServletResponse response) throws IOException, PermissionDeniedException {
		
		InputStream in = null;
		OutputStream out = null;
		String filename = fileObject.getName();
		if(fileObject.isDirectory()) {
			filename += ".zip";
		}
		
		Date started = Utils.now();
		ContentHash contentHash = configurationService.getObject(VFSConfiguration.class).getDefaultHash();
		VirtualFolder folder = fileService.getParentMount(fileObject);
		Long size = 0L;
		try {
			
			MessageDigest digest = MessageDigest.getInstance(contentHash.getAlgorithm());
			CountingOutputStream digestOutput = new CountingOutputStream(new DigestOutputStream(response.getOutputStream(), digest));
			try {
			
				if(fileObject.isDirectory()) {
					in = new ZipFolderInputStream(fileObject);
				} else {
					in = fileObject.getInputStream();
					response.setContentLengthLong(fileObject.length());
				}
				
				response.setHeader("Content-Disposition", "attachment; filename=\"" + filename  + "\"");
				
				String mimeType = URLConnection.guessContentTypeFromName(filename);
				if(StringUtils.isBlank(mimeType)) {
					mimeType = "application/octet-stream";
				}
				response.setContentType(mimeType);
				
				Session session = sessionUtils.getActiveSession(Request.get());
				IOUtils.copy(new SessionStickyInputStream(in, session) {
					
					@Override
					protected void touchSession(Session session) throws IOException {
						try {
							sessionUtils.touchSession(session);
						} catch (SessionTimeoutException e) {
							throw new IOException(e.getMessage(), e);
						}
					}
				} , digestOutput);
			
			} finally {
				IOUtils.closeStream(digestOutput);
				size = digestOutput.getByteCount();
				statsService.registerDataTransfer(Throughput.EGRESS, 
						    digestOutput.getByteCount(), StatsService.HTTPS_DOWNLOAD, 
							getCurrentUser().getUuid(),
							folder.getUuid());
			}

			byte[] output = digest.digest();
			eventService.publishEvent(new FileDownloadEvent(
					new TransferResult(filename, FileUtils.getParentPath(path), 
							digestOutput.getByteCount(), started, Utils.now(), formatDigest(digest.getAlgorithm(), output), 
								new HumanHashGenerator(output)
									.words(contentHash.getWords())
									.build())));
			
		} catch (NoSuchAlgorithmException | IOException e) { 
			log.error("Failed to send file", e);
			eventService.publishEvent(new FileDownloadEvent(
					new TransferResult(filename, FileUtils.getParentPath(path), 
							size, started, Utils.now()), e));
			throw new PageRedirect(new ErrorPage(e, Request.get().getHeader("Referer")));
		} finally {
			IOUtils.closeStream(in);
			IOUtils.closeStream(out);
		}
		
	}
	
	protected void sendZippedFiles(String folder, String filename, List<AbstractFile> files, HttpServletResponse response) {
		
		InputStream in = null;
		OutputStream out = null;
		
		Date started = Utils.now();
		ContentHash contentHash = configurationService.getObject(VFSConfiguration.class).getDefaultHash();
		Long size = 0L;
		
		try {
			
			VirtualFolder vf = fileService.getParentMount(files.iterator().next());
			MessageDigest digest = MessageDigest.getInstance(contentHash.getAlgorithm());
			CountingOutputStream digestOutput = new CountingOutputStream(new DigestOutputStream(response.getOutputStream(), digest));
			try {
			
				in = new ZipMultipleFilesInputStream(folder, files);
				
				response.setHeader("Content-Disposition", "attachment; filename=\"" + filename  + "\"");
				
				String mimeType = URLConnection.guessContentTypeFromName(filename);
				if(StringUtils.isBlank(mimeType)) {
					mimeType = "application/octet-stream";
				}
				response.setContentType(mimeType);
				
				Session session = sessionUtils.getActiveSession(Request.get());
				IOUtils.copy(new SessionStickyInputStream(in, session) {
					
					@Override
					protected void touchSession(Session session) throws IOException {
						try {
							sessionUtils.touchSession(session);
						} catch (SessionTimeoutException e) {
							throw new IOException(e.getMessage(), e);
						}
					}
				} , digestOutput);

			} finally {
				size = digestOutput.getByteCount();
				
				statsService.registerDataTransfer(Throughput.EGRESS, digestOutput.getByteCount(), StatsService.HTTPS_DOWNLOAD, 
							getCurrentUser().getUuid(),
							vf.getUuid());
				
			}
			

			byte[] output = digest.digest();
			eventService.publishEvent(new FileDownloadEvent(
					new TransferResult(filename, "", 
							size, started, Utils.now(), formatDigest(digest.getAlgorithm(), output), 
								new HumanHashGenerator(output)
									.words(contentHash.getWords())
									.build())));
			
		} catch (NoSuchAlgorithmException | IOException | PermissionDeniedException e) { 
			log.error(e.getMessage(), e);
			eventService.publishEvent(new FileDownloadEvent(
					new TransferResult(filename, "", 
							0L, started, Utils.now()), e));
			throw new PageRedirect(new ErrorPage(e, Request.get().getHeader("Referer")));
		} finally {
			IOUtils.closeStream(in);
			IOUtils.closeStream(out);
		}
	}
	

	protected String formatDigest(String algorithm, byte[] output) {
		
		StringBuffer tmp = new StringBuffer();
		tmp.append(algorithm);
		tmp.append(":");
		tmp.append(com.sshtools.common.util.Utils.bytesToHex(output, output.length, false, false));
		return tmp.toString();
	}
}
