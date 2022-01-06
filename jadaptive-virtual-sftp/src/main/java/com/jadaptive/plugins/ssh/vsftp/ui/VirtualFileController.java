package com.jadaptive.plugins.ssh.vsftp.ui;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.jadaptive.api.entity.ObjectException;
import com.jadaptive.api.entity.ObjectNotFoundException;
import com.jadaptive.api.events.EventService;
import com.jadaptive.api.json.RequestStatus;
import com.jadaptive.api.json.RequestStatusImpl;
import com.jadaptive.api.json.ResourceList;
import com.jadaptive.api.json.ResourceStatus;
import com.jadaptive.api.permissions.AuthenticatedController;
import com.jadaptive.api.repository.RepositoryException;
import com.jadaptive.plugins.ssh.vsftp.FileScheme;
import com.jadaptive.plugins.ssh.vsftp.VirtualFileService;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderMount;
import com.jadaptive.plugins.ssh.vsftp.links.PublicDownload;
import com.jadaptive.plugins.ssh.vsftp.links.PublicDownloadService;
import com.jadaptive.plugins.ssh.vsftp.zip.ZipFolderInputStream;
import com.jadaptive.plugins.sshd.SSHDService;
import com.sshtools.common.files.AbstractFile;
import com.sshtools.common.files.AbstractFileFactory;
import com.sshtools.common.files.vfs.VirtualFileObject;
import com.sshtools.common.files.vfs.VirtualMount;
import com.sshtools.common.permissions.PermissionDeniedException;
import com.sshtools.common.util.FileUtils;
import com.sshtools.common.util.IOUtils;
import com.sshtools.common.util.URLUTF8Encoder;

@Extension
@Controller
public class VirtualFileController extends AuthenticatedController {

	private static final String ABSTRACT_FILE_FACTORY = "abstractFileFactory";

	static Logger log = LoggerFactory.getLogger(VirtualFileController.class);
	
	@Autowired
	private SSHDService sshdService;
	
	@Autowired
	private VirtualFileService fileService; 
	
	@Autowired
	private PublicDownloadService linkService; 
	
	@Autowired
	private EventService eventService; 
	
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
				FileScheme<?> scheme = fileService.getFileScheme(m.getType());
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
			
			AbstractFile parent = getFactory(request).getFile(path);
			
			VirtualMount parentMount = ((VirtualFileObject)parent).getParentMount();
			boolean publicFiles = false;
			if(parentMount.getTemplate() instanceof VirtualFolderMount) {
				VirtualFolderMount virtualMount = (VirtualFolderMount) parentMount.getTemplate();
				publicFiles = virtualMount.getVirtualFolder().isPublicFolder();
			}

			return new ResourceStatus<File>(new File(parent, publicFiles,
					FileUtils.isSamePath(parent.getAbsolutePath(),parentMount.getMount())));
			
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
			
			AbstractFile parent = getFactory(request).getFile(path);
			PathMatcher matcher = null;
			if(StringUtils.isNotBlank(filter)) {
				matcher = FileSystems.getDefault().getPathMatcher("glob:" + filter);
			}
			
			VirtualMount parentMount = ((VirtualFileObject)parent).getParentMount();
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
				if(file.isDirectory() && folders) {
					if((file.isHidden() && hidden) || !file.isHidden()) {
						
						
						boolean isFolderPublic = false;
						VirtualMount parentMount = ((VirtualFileObject)file).getParentMount();
						if(parentMount.getTemplate() instanceof VirtualFolderMount) {
							VirtualFolderMount virtualMount = (VirtualFolderMount) parentMount.getTemplate();
							isFolderPublic = virtualMount.getVirtualFolder().isPublicFolder();
						}
						
						
						folderResults.add(new File(file, isFolderPublic,
								FileUtils.isSamePath(file.getAbsolutePath(),parentMount.getMount())));
						--maximumFiles;
						
						if(maximumFiles > 0) {
							if(file.isDirectory() && currentDepth < maximumDepth) {
								maximumFiles = search(file, matcher, folderResults, fileResults, folders, files, hidden, maximumFiles, maximumDepth, currentDepth + 1, isFolderPublic);
							}
						} else {
							break;
						}
					}
					
				} else if(file.isFile() && files) {
					if((file.isHidden() && hidden) || !file.isHidden()) {
						fileResults.add(new File(file, publicFiles, false));
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
			AbstractFile fileObject = getFactory(request).getFile(path);
			sendFileOrZipFolder(path, fileObject, response);
		} catch (Throwable e) {
			throw new IllegalStateException(e);
		} finally {
			clearUserContext();
		}
	}
	
	private void sendFileOrZipFolder(String path, AbstractFile fileObject, HttpServletResponse response) throws IOException, PermissionDeniedException {
		
		InputStream in = null;
		OutputStream out = null;
		String filename;
		
		try {
			if(fileObject.isDirectory()) {
				in = new ZipFolderInputStream(fileObject);
				response.setHeader("Content-Disposition", "attachment; filename=\"" + (filename = fileObject.getName() + ".zip\""));
			} else {
				in = fileObject.getInputStream();
				response.setHeader("Content-Disposition", "attachment; filename=\"" + (filename = fileObject.getName()) + "\"");
				response.setContentLengthLong(fileObject.length());
			}
			
			
			String mimeType = URLConnection.guessContentTypeFromName(filename);
			if(StringUtils.isBlank(mimeType)) {
				mimeType = "application/octet-stream";
			}
			response.setContentType(mimeType);
			
			long started = System.currentTimeMillis();
			long size = 0L;
			MessageDigest digest = MessageDigest.getInstance("MD-5");
			try(OutputStream digestOutput = new DigestOutputStream(response.getOutputStream(), digest)) {
				size = IOUtils.copyWithCount(in, digestOutput);
			}

			eventService.publishEvent(new FileDownloadedEvent(
					new TransferResult(filename, FileUtils.getParentPath(path), size, started, System.currentTimeMillis(), digest.digest())));
		} catch (NoSuchAlgorithmException | IOException e) { 
			
		} finally {
			IOUtils.closeStream(in);
			IOUtils.closeStream(out);
		}
		
	}

	@RequestMapping(value="/app/vfs/downloadLink/{shortCode}/**", method = { RequestMethod.POST, RequestMethod.GET }, produces = {"application/octet-stream"})
	@ResponseStatus(value=HttpStatus.OK)
	public void downloadFile(HttpServletRequest request, HttpServletResponse response, @PathVariable String shortCode) throws RepositoryException, UnknownEntityException, ObjectException {

		setupSystemContext();
		
		try {
			
			PublicDownload download = linkService.getDownloadByShortCode(shortCode);
			AbstractFile fileOjbect = getFactory(request).getFile(download.getVirtualPath());
			sendFileOrZipFolder(download.getVirtualPath(), fileOjbect, response);
		} catch (Throwable e) {
			throw new IllegalStateException(e);
		} finally {
			clearUserContext();
		}
	}
	
	@RequestMapping(value="/app/vfs/createPublicDownload", method = { RequestMethod.POST}, produces = {"application/json"})
	@ResponseBody
	@ResponseStatus(value=HttpStatus.OK)
	public ResourceStatus<PublicDownload> createPublicDownload(HttpServletRequest request, HttpServletResponse response,
			@RequestParam String path) throws RepositoryException, UnknownEntityException, ObjectException {

		setupUserContext(request);
		
		try {
			try {
				PublicDownload link = linkService.getDownloadByPath(path);
				return new ResourceStatus<>(link);
			} catch(ObjectNotFoundException e) {
				
				AbstractFile fileObject = getFactory(request).getFile(path);
				PublicDownload link = linkService.createDownloadLink(fileObject);
				return new ResourceStatus<>(link);
			}
		} catch (Throwable e) {
			throw new IllegalStateException(e);
		} finally {
			clearUserContext();
		}
	}
	
	private AbstractFileFactory<?> getFactory(HttpServletRequest request) {
		
		AbstractFileFactory<?> factory = (AbstractFileFactory<?>) request.getSession().getAttribute(ABSTRACT_FILE_FACTORY);
		if(Objects.isNull(factory)) {
			factory = sshdService.getFileFactory(getCurrentUser());
			request.getSession().setAttribute(ABSTRACT_FILE_FACTORY, factory);
		}
		return factory;
	}

	@RequestMapping(value="/app/vfs/createFolder", method = { RequestMethod.POST }, produces = {"application/json"})
	@ResponseStatus(value=HttpStatus.OK)
	@ResponseBody
	public RequestStatus createFolder(HttpServletRequest request, HttpServletResponse response, 
			@RequestParam String name,
			@RequestParam String path) throws RepositoryException, UnknownEntityException, ObjectException {

		setupUserContext(request);
		
		try {
			
			name = URLUTF8Encoder.decode(name);
			path = URLUTF8Encoder.decode(path);
			
			AbstractFile parent = getFactory(request).getFile(path);
			
			if(!parent.isDirectory()) {
				throw new IllegalStateException("Parent path is not a folder");
			}
			
			AbstractFile newFolder = parent.resolveFile(name);
			
			if(newFolder.exists()) {
				return new RequestStatusImpl(false, "The folder already exists!");
			}
			
			if(!newFolder.createFolder()) {
				return new RequestStatusImpl(false, "The folder was not created");
			}
			
			return new RequestStatusImpl(true, "Created folder " + name + " in " + path);
		} catch (Throwable e) {
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
		
		try {
			path = URLUTF8Encoder.decode(path);
			
			AbstractFile obj = getFactory(request).getFile(path);
			
			if(!obj.exists()) {
				return new RequestStatusImpl(false, "The object does not exist!");
			}
				
			if(!obj.delete(obj.isDirectory())) {
				return new RequestStatusImpl(false, String.format("The %s was not deleted", obj.isDirectory() ? "folder" : "file"));
			}
			
			return new RequestStatusImpl(true, "Deleted " + path);
		} catch (Throwable e) {
			return new RequestStatusImpl(false, e.getMessage());
		} finally {
			clearUserContext();
		}
	}
}
