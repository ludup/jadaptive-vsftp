package com.jadaptive.plugins.ssh.vsftp.ui;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
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

import org.apache.commons.lang3.StringUtils;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.jadaptive.api.app.StartupAware;
import com.jadaptive.api.entity.ObjectException;
import com.jadaptive.api.entity.ObjectNotFoundException;
import com.jadaptive.api.events.EventService;
import com.jadaptive.api.json.RequestStatus;
import com.jadaptive.api.json.RequestStatusImpl;
import com.jadaptive.api.json.ResourceList;
import com.jadaptive.api.json.ResourceStatus;
import com.jadaptive.api.repository.RepositoryException;
import com.jadaptive.api.session.Session;
import com.jadaptive.api.ui.PageRedirect;
import com.jadaptive.plugins.ssh.vsftp.FileScheme;
import com.jadaptive.plugins.ssh.vsftp.VirtualFileService;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderMount;
import com.jadaptive.plugins.ssh.vsftp.events.FileDeletedEvent;
import com.jadaptive.plugins.ssh.vsftp.events.FileOperation;
import com.jadaptive.plugins.ssh.vsftp.events.FolderCreatedEvent;
import com.jadaptive.utils.Utils;
import com.sshtools.common.files.AbstractFile;
import com.sshtools.common.files.vfs.VirtualFile;
import com.sshtools.common.files.vfs.VirtualFileObject;
import com.sshtools.common.files.vfs.VirtualMount;
import com.sshtools.common.permissions.PermissionDeniedException;
import com.sshtools.common.util.FileUtils;
import com.sshtools.common.util.URLUTF8Encoder;

@Extension
@Controller
public class VirtualFileController extends AbstractFileController implements StartupAware {

	static Logger log = LoggerFactory.getLogger(VirtualFileController.class);
	
	@Autowired
	private VirtualFileService fileService; 
	
	@Autowired
	private EventService eventService; 

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
				FileScheme scheme = fileService.getFileScheme(m.getResourceKey());
				mounts.add(new Mount(m, scheme.getIcon()));
			}
			if(!home) {
				mounts.add(0, new Mount("Home", "/", "fa-solid fa-home"));
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
	
//	@RequestMapping(value="/app/vfs/report/{uuid}", method = { RequestMethod.GET }, produces = {"application/json"})
//	@ResponseStatus(value=HttpStatus.OK)
//	@ResponseBody
//	public EntityStatus<MountReportData> generateMountReport(HttpServletRequest request, HttpServletResponse response, 
//			@PathVariable String uuid) {
//
//	try {
//		VirtualFolder folder = fileService.getObjectByUUID(uuid);
//		
//		AbstractFile mount = fileService.getFile(folder.getMountPath());
//		
//		MountReportData data = new MountReportData();
//		data.setName(folder.getName());
//		data.setType(folder.getScheme());
//		data.setTotalSize(iterateDirectory(mount, getCurrentSession()));
//		
//		data.setHttpDownloads(usageService.sumOr(Utils.yesterday(), Utils.today(), StatsService.HTTPS_DOWNLOAD, folder.getUuid()));
//		data.setHttpUploads(usageService.sumOr(Utils.yesterday(), Utils.today(), StatsService.HTTPS_UPLOAD, folder.getUuid()));
//		
//		data.setScpDownload(usageService.sumOr(Utils.yesterday(), Utils.today(), StatsService.SCP_DOWNLOAD, folder.getUuid()));
//		data.setScpUpload(usageService.sumOr(Utils.yesterday(), Utils.today(), StatsService.SCP_UPLOAD, folder.getUuid()));
//		
//		data.setSftpDownload(usageService.sumOr(Utils.yesterday(), Utils.today(), StatsService.SFTP_DOWNLOAD, folder.getUuid()));
//		data.setSftpUpload(usageService.sumOr(Utils.yesterday(), Utils.today(), StatsService.SFTP_UPLOAD, folder.getUuid()));
//		
//		return new EntityStatus<MountReportData>(data);
//		
//	} catch (Throwable e) {
//		return new EntityStatus<MountReportData>(false, e.getMessage());
//	}
//}

//private long iterateDirectory(AbstractFile file, Session session) throws IOException, PermissionDeniedException, SessionTimeoutException {
//	long count = 0L;
//	for(AbstractFile child : file.getChildren()) {
//		if(child.isDirectory()) {
//			count += iterateDirectory(child, session);
//		} else {
//			count += child.length();		
//		}
//		sessionUtils.touch(session);
//	}
//	return count;
//}
//	@Override
//	public Integer getStartupPosition() {
//		return 0;
//	}

}
