package com.jadaptive.plugins.ssh.vsftp.ui;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.lang.model.UnknownEntityException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.output.CountingOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.jadaptive.api.app.StartupAware;
import com.jadaptive.api.db.SingletonObjectDatabase;
import com.jadaptive.api.entity.ObjectException;
import com.jadaptive.api.entity.ObjectNotFoundException;
import com.jadaptive.api.events.EventService;
import com.jadaptive.api.json.EntityStatus;
import com.jadaptive.api.json.RequestStatus;
import com.jadaptive.api.json.RequestStatusImpl;
import com.jadaptive.api.json.ResourceList;
import com.jadaptive.api.json.ResourceStatus;
import com.jadaptive.api.permissions.AuthenticatedController;
import com.jadaptive.api.repository.RepositoryException;
import com.jadaptive.api.servlet.Request;
import com.jadaptive.api.session.Session;
import com.jadaptive.api.session.SessionStickyInputStream;
import com.jadaptive.api.session.SessionTimeoutException;
import com.jadaptive.api.session.SessionUtils;
import com.jadaptive.api.stats.UsageService;
import com.jadaptive.api.tenant.TenantService;
import com.jadaptive.api.ui.ErrorPage;
import com.jadaptive.api.ui.Feedback;
import com.jadaptive.api.ui.PageRedirect;
import com.jadaptive.plugins.ssh.vsftp.ContentHash;
import com.jadaptive.plugins.ssh.vsftp.FileScheme;
import com.jadaptive.plugins.ssh.vsftp.VFSConfiguration;
import com.jadaptive.plugins.ssh.vsftp.VirtualFileService;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderMount;
import com.jadaptive.plugins.ssh.vsftp.links.SharedFile;
import com.jadaptive.plugins.ssh.vsftp.links.SharedFileService;
import com.jadaptive.plugins.ssh.vsftp.stats.StatsService;
import com.jadaptive.plugins.ssh.vsftp.upload.FileUpload;
import com.jadaptive.plugins.ssh.vsftp.upload.IncomingFile;
import com.jadaptive.plugins.ssh.vsftp.upload.IncomingFileService;
import com.jadaptive.plugins.ssh.vsftp.zip.ZipFolderInputStream;
import com.jadaptive.plugins.ssh.vsftp.zip.ZipMultipleFilesInputStream;
import com.jadaptive.utils.Utils;
import com.sshtools.common.files.AbstractFile;
import com.sshtools.common.files.vfs.VirtualFile;
import com.sshtools.common.files.vfs.VirtualFileObject;
import com.sshtools.common.files.vfs.VirtualMount;
import com.sshtools.common.permissions.PermissionDeniedException;
import com.sshtools.common.util.FileUtils;
import com.sshtools.common.util.IOUtils;
import com.sshtools.common.util.URLUTF8Encoder;
import com.sshtools.humanhash.HumanHashGenerator;

@Extension
@Controller
public class VirtualFileController extends AuthenticatedController implements StartupAware {

	static Logger log = LoggerFactory.getLogger(VirtualFileController.class);
	
	@Autowired
	private VirtualFileService fileService; 
	
	@Autowired
	private SharedFileService linkService; 
	
	@Autowired
	private EventService eventService; 
	
	@Autowired
	private SingletonObjectDatabase<VFSConfiguration> configurationService; 
	
	@Autowired
	private IncomingFileService incomingService; 
	
	@Autowired
	private UsageService usageService; 
	
	@Autowired
	private TenantService tenantService; 
	
	@Autowired
	private SessionUtils sessionUtils;
	
	@Override
	public void onApplicationStartup() {

		eventService.any(VirtualFolder.class, (evt) -> {
			fileService.resetFactory();
		});
		eventService.updated(Session.class, (evt) -> {
			if(evt.getObject().isClosed()) {
				fileService.resetFactory();
			}
		});

	}
	
	@RequestMapping(value="/app/vfs/mounts", method = { RequestMethod.POST, RequestMethod.GET }, produces = {"application/json"})
	@ResponseBody
	@ResponseStatus(value=HttpStatus.OK)
	public ResourceList<Mount> getFolders(HttpServletRequest request) throws RepositoryException, UnknownEntityException, ObjectException {

		setupUserContext(request);
		
		try {
			List<Mount> mounts = new ArrayList<>();
			boolean home = false;
			for(VirtualFolder m : fileService.allObjects()) {
				home |= m.isHome();
				FileScheme<?> scheme = fileService.getFileScheme(m.getResourceKey());
				mounts.add(new Mount(m, scheme.getIcon()));
			}
			if(!home) {
				mounts.add(0, new Mount("Home", "/", "far fa-home"));
			}
			return new ResourceList<Mount>(mounts);
		} catch (Throwable e) {
			return new ResourceList<>(false, e.getMessage());
		} finally {
			clearUserContext();
		}
	}
	
	@RequestMapping(value="/app/vfs/stat/**", method = { RequestMethod.POST, RequestMethod.GET }, produces = {"application/json"})
	@ResponseBody
	@ResponseStatus(value=HttpStatus.OK)
	public ResourceStatus<File> statFile(HttpServletRequest request) 
			throws RepositoryException, UnknownEntityException, ObjectException {

		setupUserContext(request);
		
		String path = URLUTF8Encoder.decode(FileUtils.checkStartsWithSlash(request.getRequestURI().substring(13)));
		
		try {
			
			log.info("Start stat {}", path);
			
			AbstractFile parent = fileService.getFactory().getFile(path);
			
			VirtualMount parentMount = ((VirtualFileObject)parent).getMount();

			VirtualFolder folder = null;
			try {
				folder = fileService.getVirtualFolder(parentMount.getMount());
			} catch(ObjectNotFoundException e) { }
			
			return new ResourceStatus<File>(new File((VirtualFile)parent, folder, null));
			
		} catch (Throwable e) {
			log.error("Stat failed", e);
			return new ResourceStatus<>(false, e.getMessage());
		} finally {
			log.info("Finished stat {}", path);
			clearUserContext();
		}
	}
	
	@RequestMapping(value="/app/vfs/listDirectory/**", method = { RequestMethod.POST, RequestMethod.GET }, produces = {"application/json"})
	@ResponseBody
	@ResponseStatus(value=HttpStatus.OK)
	public BootstrapTableResult<File> listDirectory(HttpServletRequest request,
			@RequestParam(required=false, defaultValue="") String filter,
			@RequestParam(required=false, defaultValue="1000") int maximumResults,
			@RequestParam(required=false, defaultValue="true") boolean files,
			@RequestParam(required=false, defaultValue="true") boolean folders,
			@RequestParam(required=false, defaultValue="true") boolean hidden,
			@RequestParam(required=false, defaultValue="0") int searchDepth,
			@RequestParam int offset,
			@RequestParam int limit) throws RepositoryException, UnknownEntityException, ObjectException {

		setupUserContext(request);
		
		String path = URLUTF8Encoder.decode(FileUtils.checkStartsWithSlash(request.getRequestURI().substring(22)));
		
		try {
			
			log.info("Start Listing directory {}", path);
			
			List<File> fileResults = new ArrayList<>();
			List<File> folderResults = new ArrayList<>();
			
			AbstractFile parent = fileService.getFactory().getFile(path);
			parent.refresh();
			PathMatcher matcher = null;
			if(StringUtils.isNotBlank(filter)) {
				matcher = FileSystems.getDefault().getPathMatcher("glob:" + filter);
			}
			
			VirtualMount parentMount = ((VirtualFileObject)parent).getMount();
			boolean publicFiles = false;
			if(parentMount.getTemplate() instanceof VirtualFolderMount) {
				VirtualFolderMount virtualMount = (VirtualFolderMount) parentMount.getTemplate();
				publicFiles = virtualMount.getVirtualFolder().isPublicFolder();
			}
			
			search(parent, matcher, folderResults, fileResults, 
					folders, files, hidden, maximumResults, searchDepth, 0, publicFiles);
			
			Collections.sort(folderResults, new Comparator<File>() {

				@Override
				public int compare(File o1, File o2) {
					return o1.getName().compareTo(o2.getName());
				}
				
			});
			
			Collections.sort(fileResults, new Comparator<File>() {

				@Override
				public int compare(File o1, File o2) {
					return o1.getName().compareTo(o2.getName());
				}
			});
			
			folderResults.addAll(fileResults);
			int page = Math.min(folderResults.size(), offset+limit);
			if(offset > folderResults.size()) {
				return new BootstrapTableResult<File>(Collections.emptyList(), folderResults.size());
			}
			return new BootstrapTableResult<>(folderResults.subList(offset, page), folderResults.size());
		} catch (Throwable e) {
			log.error("File listing failed", e);
			return new BootstrapTableResult<>(e.getMessage());
		} finally {
			log.info("Finished Listing directory {}", path);
			clearUserContext();
		}
	}
	
	int search(AbstractFile parent, PathMatcher matcher, 
				Collection<File> folderResults, Collection<File> fileResults, 
				boolean folders, boolean files, boolean hidden, int maximumFiles, 
					int maximumDepth, int currentDepth, boolean publicFiles) throws IOException, PermissionDeniedException {
		
		
		for(AbstractFile file : parent.getChildren()) {
			
			boolean matches = true;
			if(Objects.nonNull(matcher)) {
				if(!matcher.matches(Paths.get(file.getName()))) {
					matches = false;
				}
			}

			if(matches) {
			
				VirtualMount parentMount = ((VirtualFileObject)file).getMount();
				VirtualFolder virtualFolder = null;
				try {
					virtualFolder = fileService.getVirtualFolder(parentMount.getMount());
				} catch(ObjectNotFoundException e) { } 
				
				if(file.isDirectory() && folders) {
					if((file.isHidden() && hidden) || !file.isHidden()) {

						VirtualFolder mount = null;
						if(((VirtualFileObject)file).isMount()) {

							try {
								mount = fileService.getVirtualFolder(file.getAbsolutePath());
							} catch(ObjectNotFoundException e) { } 
						}
						folderResults.add(new File((VirtualFile)file, virtualFolder, mount));
						
						if(maximumFiles > 0) {
							if(file.isDirectory() && currentDepth < maximumDepth) {
								maximumFiles = search(file, matcher, folderResults, fileResults, folders, files, hidden, maximumFiles, maximumDepth, currentDepth + 1, virtualFolder.isPublicFolder());
							}
						} else {
							break;
						}
					}
					
				} else if(file.isFile() && files) {
					if((file.isHidden() && hidden) || !file.isHidden()) {
						fileResults.add(new File((VirtualFile)file, virtualFolder, null));
						--maximumFiles;
					}
				} 
			}
			

		}
		
		return maximumFiles;
	}
	
	@RequestMapping(value="/app/vfs/downloadFile/**", method = { RequestMethod.POST, RequestMethod.GET }, produces = {"application/octet-stream"})
	@ResponseStatus(value=HttpStatus.OK)
	public void downloadFile(HttpServletRequest request, HttpServletResponse response) throws RepositoryException, UnknownEntityException, ObjectException {

		setupUserContext(request);
		
		try {
			String path = URLUTF8Encoder.decode(FileUtils.checkStartsWithSlash(request.getRequestURI().substring(22)));
			AbstractFile fileObject = fileService.getFile(path);
			sendFileOrZipFolder(path, fileObject, response);
		} catch(PageRedirect e) {
			throw e;
		} catch (Throwable e) {
			throw new IllegalStateException(e);
		} finally {
			clearUserContext();
		}
	}
	
	private void sendZippedFiles(String folder, String filename, List<AbstractFile> files, HttpServletResponse response) {
		
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
				
				tenantService.executeAs(getCurrentTenant(), ()-> {
					usageService.log(digestOutput.getByteCount(), StatsService.HTTPS_DOWNLOAD, 
							getCurrentUser().getUuid(),
							vf.getUuid());
				});
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
	
	private void sendFileOrZipFolder(String path, AbstractFile fileObject, HttpServletResponse response) throws IOException, PermissionDeniedException {
		
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
				tenantService.executeAs(getCurrentTenant(), ()-> {
					usageService.log(digestOutput.getByteCount(), StatsService.HTTPS_DOWNLOAD, 
							getCurrentUser().getUuid(),
							folder.getUuid());
				});
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

	private String formatDigest(String algorithm, byte[] output) {
		
		StringBuffer tmp = new StringBuffer();
		tmp.append(algorithm);
		tmp.append(":");
		tmp.append(com.sshtools.common.util.Utils.bytesToHex(output, output.length, false, false));
		return tmp.toString();
	}

	@RequestMapping(value="/app/vfs/downloadLink/{shortCode}/**", method = { RequestMethod.POST, RequestMethod.GET }, produces = {"application/octet-stream"})
	@ResponseStatus(value=HttpStatus.OK)
	public void downloadFile(HttpServletRequest request, HttpServletResponse response, @PathVariable String shortCode) throws RepositoryException, UnknownEntityException, ObjectException {

		setupSystemContext();
		
		Date started = Utils.now();
		try {
			
			SharedFile download = linkService.getDownloadByShortCode(shortCode);
			if(download.getPasswordProtected()) {
				if(!DownloadPublicFile.hasPassword(request, download)) {
					response.sendRedirect(String.format("/app/ui/password-protected/%s/%s", shortCode, download.getFilename()));
					return;
				}
			}
			
			if(download.getAcceptTerms()) {
				if(!DownloadPublicFile.hasAcceptedTerms(request, download)) {
					response.sendRedirect(String.format("/app/ui/download-share/%s/%s", shortCode, download.getFilename()));
					return;
				}
			}
			
			AbstractFile fileOjbect = fileService.getFactory(download.getSharedBy()).getFile(download.getVirtualPath());
			
			sendFileOrZipFolder(download.getVirtualPath(), fileOjbect, response);
			linkService.notifyShareAccess(download, started, fileOjbect);
		} catch (Throwable e) {
			throw new IllegalStateException(e);
		} finally {
			clearUserContext();
		}
	}
	
	@RequestMapping(value="/app/vfs/incoming/zip/{uuid}", method = { RequestMethod.POST, RequestMethod.GET }, produces = {"application/octet-stream"})
	@ResponseStatus(value=HttpStatus.OK)
	public void downloadZip(HttpServletRequest request, HttpServletResponse response, @PathVariable String uuid) throws RepositoryException, UnknownEntityException, ObjectException {

		setupUserContext(request);
		
		try {
			
			IncomingFile download = incomingService.getIncomingFile(uuid);
			List<AbstractFile> files = new ArrayList<>();
			for(FileUpload file : download.getUploadPaths()) {
				files.add(fileService.getFactory().getFile(file.getVirtualPath()));
			}
			sendZippedFiles(download.getReference(), download.getReference() + ".zip", files, response);
		} catch (Throwable e) {
			throw new IllegalStateException(e);
		} finally {
			clearUserContext();
		}
	}
	
	@RequestMapping(value="/app/vfs/incoming/delete/{uuid}", method = { RequestMethod.DELETE }, produces = {"application/json"})
	@ResponseStatus(value=HttpStatus.OK)
	@ResponseBody
	public RequestStatus deleteIncomingFile(HttpServletRequest request, HttpServletResponse response, @PathVariable String uuid) throws RepositoryException, UnknownEntityException, ObjectException {

		setupUserContext(request);
		
		try {
			
			IncomingFile download = incomingService.getIncomingFile(uuid);
			
			for(FileUpload fileUpload : download.getUploadPaths()) {
				AbstractFile file = fileService.getFactory().getFile(fileUpload.getVirtualPath());
				file.delete(false);
			}
			
			incomingService.delete(download);
			Feedback.success(VirtualFolder.RESOURCE_KEY, "incomingFile.delete.success", download.getReference());
			return new RequestStatusImpl(true);
		} catch (Throwable e) {
			Feedback.error(VirtualFolder.RESOURCE_KEY, "incomingFile.delete.error", e.getMessage());
			return new RequestStatusImpl(false, e.getMessage());
		} finally {
			clearUserContext();
		}
	}

	@RequestMapping(value="/app/vfs/share/public/**", method = { RequestMethod.GET}, produces = {"application/json"})
	@ResponseBody
	@ResponseStatus(value=HttpStatus.OK)
	public ResourceStatus<SharedFile> createPublicDownload(HttpServletRequest request, HttpServletResponse response) throws RepositoryException, UnknownEntityException, ObjectException {

		setupUserContext(request);
			
		try {
			
			String path = URLDecoder.decode(request.getRequestURI().substring(21), "UTF-8");
			
			try {
				SharedFile link = linkService.getDownloadByPath(path, getCurrentUser());
				return new ResourceStatus<>(link);
			} catch(ObjectNotFoundException e) {
				
				AbstractFile fileObject = fileService.getFactory().getFile(path);
				SharedFile link = linkService.createDownloadLink(fileObject, getCurrentUser());
				return new ResourceStatus<>(link);
			}
		} catch (Throwable e) {
			throw new IllegalStateException(e);
		} finally {
			clearUserContext();
		}
	}
	
	@RequestMapping(value="/app/vfs/share/create/**", method = { RequestMethod.GET}, produces = {"application/json"})
	@ResponseBody
	@ResponseStatus(value=HttpStatus.OK)
	public ResourceStatus<SharedFile> createShare(HttpServletRequest request, HttpServletResponse response) throws RepositoryException, UnknownEntityException, ObjectException {

		setupUserContext(request);

		try {
			
			String path = URLDecoder.decode(request.getRequestURI().substring(21), "UTF-8");
			
			SharedFile link = new SharedFile();
			link.setVirtualPath(path);
			request.getSession().setAttribute(SharedFile.RESOURCE_KEY, link);
			return new ResourceStatus<>(link);
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(e);
		} finally {
			clearUserContext();
		}
	}

	@RequestMapping(value="/app/vfs/createFolder", method = { RequestMethod.POST }, produces = {"application/json"})
	@ResponseStatus(value=HttpStatus.OK)
	@ResponseBody
	public RequestStatus createFolder(HttpServletRequest request, HttpServletResponse response, 
			@RequestParam String name,
			@RequestParam String path) throws RepositoryException, UnknownEntityException, ObjectException {

		setupUserContext(request);
		
		Date started = Utils.now();
		try {
			
			name = URLUTF8Encoder.decode(name);
			path = URLUTF8Encoder.decode(path);
			
			AbstractFile parent = fileService.getFactory().getFile(path);
			
			if(!parent.isDirectory()) {
				throw new IOException("Parent path is not a folder");
			}
			
			AbstractFile newFolder = parent.resolveFile(name);
			
			if(newFolder.exists()) {
				throw new IOException("The folder already exists!");
			}
			
			if(!newFolder.createFolder()) {
				throw new IOException("The folder was not created");
			}
			
			eventService.publishEvent(new FolderCreatedEvent(
					new FileOperation(name, 
							path, started, Utils.now())));
			
			return new RequestStatusImpl(true, "Created folder " + name + " in " + path);
		} catch (Throwable e) {
			eventService.publishEvent(new FolderCreatedEvent(
					new FileOperation(name, 
							path, started, Utils.now()), e));
			return new RequestStatusImpl(false, e.getMessage());
		} finally {
			clearUserContext();
		}
	}
	
	@RequestMapping(value="/app/vfs/delete", method = { RequestMethod.POST }, produces = {"application/json"})
	@ResponseStatus(value=HttpStatus.OK)
	@ResponseBody
	public RequestStatus delete(HttpServletRequest request, HttpServletResponse response, 
			@RequestParam String path) throws RepositoryException, UnknownEntityException, ObjectException {

		setupUserContext(request);
		
		Date started = Utils.now();
		AbstractFile obj = null;
		
		try {
			path = URLUTF8Encoder.decode(path);
			
			obj = fileService.getFactory().getFile(path);
			
			if(!obj.exists()) {
				throw new IOException("The object does not exist!");
			}
				
			if(!obj.delete(obj.isDirectory())) {
				String msg = String.format("The %s was not deleted", obj.isDirectory() ? "folder" : "file");
				throw new IOException(msg);
			}
			
			eventService.publishEvent(new FileDeletedEvent(
					new FileOperation(FileUtils.getFilename(path), 
							path, started, Utils.now())));
			
			return new RequestStatusImpl(true, "Deleted " + path);
		} catch (Throwable e) {
			eventService.publishEvent(new FileDeletedEvent(
					new FileOperation(FileUtils.getFilename(path), path, started, Utils.now()), 
					e));
			return new RequestStatusImpl(false, e.getMessage());
		} finally {
			clearUserContext();
		}
	}
	
	@RequestMapping(value="/app/vfs/report/{uuid}", method = { RequestMethod.GET }, produces = {"application/json"})
	@ResponseStatus(value=HttpStatus.OK)
	@ResponseBody
	public EntityStatus<MountReportData> generateMountReport(HttpServletRequest request, HttpServletResponse response, 
			@PathVariable String uuid) {

	try {
		VirtualFolder folder = fileService.getObjectByUUID(uuid);
		
		AbstractFile mount = fileService.getFile(folder.getMountPath());
		
		MountReportData data = new MountReportData();
		data.setName(folder.getName());
		data.setType(folder.getScheme());
		data.setTotalSize(iterateDirectory(mount, getCurrentSession()));
		
		data.setHttpDownloads(usageService.sumOr(Utils.yesterday(), Utils.today(), StatsService.HTTPS_DOWNLOAD, folder.getUuid()));
		data.setHttpUploads(usageService.sumOr(Utils.yesterday(), Utils.today(), StatsService.HTTPS_UPLOAD, folder.getUuid()));
		
		data.setScpDownload(usageService.sumOr(Utils.yesterday(), Utils.today(), StatsService.SCP_DOWNLOAD, folder.getUuid()));
		data.setScpUpload(usageService.sumOr(Utils.yesterday(), Utils.today(), StatsService.SCP_UPLOAD, folder.getUuid()));
		
		data.setSftpDownload(usageService.sumOr(Utils.yesterday(), Utils.today(), StatsService.SFTP_DOWNLOAD, folder.getUuid()));
		data.setSftpUpload(usageService.sumOr(Utils.yesterday(), Utils.today(), StatsService.SFTP_UPLOAD, folder.getUuid()));
		
		return new EntityStatus<MountReportData>(data);
		
	} catch (Throwable e) {
		return new EntityStatus<MountReportData>(false, e.getMessage());
	}
}

private long iterateDirectory(AbstractFile file, Session session) throws IOException, PermissionDeniedException, SessionTimeoutException {
	long count = 0L;
	for(AbstractFile child : file.getChildren()) {
		if(child.isDirectory()) {
			count += iterateDirectory(child, session);
		} else {
			count += child.length();		
		}
		sessionUtils.touch(session);
	}
	return count;
}
	@Override
	public Integer getStartupPosition() {
		return 0;
	}
}
