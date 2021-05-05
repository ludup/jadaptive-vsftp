package com.jadaptive.plugins.ssh.vsftp.ui;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
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
import org.apache.tomcat.util.http.fileupload.IOUtils;
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

import com.jadaptive.api.entity.ObjectException;
import com.jadaptive.api.json.ResourceList;
import com.jadaptive.api.permissions.AuthenticatedController;
import com.jadaptive.api.repository.RepositoryException;
import com.jadaptive.api.servlet.PluginController;
import com.jadaptive.plugins.ssh.vsftp.FileScheme;
import com.jadaptive.plugins.ssh.vsftp.VirtualFileService;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.jadaptive.plugins.sshd.SSHDService;
import com.sshtools.common.files.AbstractFile;
import com.sshtools.common.permissions.PermissionDeniedException;
import com.sshtools.common.util.FileUtils;

@Extension
@Controller
public class VirtualFileController extends AuthenticatedController implements PluginController {

	static Logger log = LoggerFactory.getLogger(VirtualFileController.class);
	
	@Autowired
	private SSHDService sshdService;
	
	@Autowired
	private VirtualFileService fileService; 
	
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
				FileScheme scheme = fileService.getFileScheme(m.getType());
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
		
		try {
			
			List<File> fileResults = new ArrayList<>();
			List<File> folderResults = new ArrayList<>();
			String path = FileUtils.checkStartsWithSlash(request.getRequestURI().substring(22));
			AbstractFile parent = sshdService.getFileFactory(getCurrentUser()).getFile(path);
			PathMatcher matcher = null;
			if(StringUtils.isNotBlank(filter)) {
				matcher = FileSystems.getDefault().getPathMatcher("glob:" + filter);
			}
			
			search(parent, matcher, folderResults, fileResults, 
					folders, files, hidden, maximumResults, searchDepth, 0);
			
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
			clearUserContext();
		}
	}
	
	int search(AbstractFile parent, PathMatcher matcher, 
				Collection<File> folderResults, Collection<File> fileResults, 
				boolean folders, boolean files, boolean hidden, int maximumFiles, 
					int maximumDepth, int currentDepth) throws IOException, PermissionDeniedException {
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
						folderResults.add(new File(file));
						--maximumFiles;
					}
					
				} else if(file.isFile() && files) {
					if((file.isHidden() && hidden) || !file.isHidden()) {
						fileResults.add(new File(file));
						--maximumFiles;
					}
				} 
			}
			
			if(maximumFiles > 0) {
				if(file.isDirectory() && currentDepth < maximumDepth) {
					maximumFiles = search(file, matcher, folderResults, fileResults, folders, files, hidden, maximumFiles, maximumDepth, currentDepth + 1);
				}
			} else {
				break;
			}
		}
		
		return maximumFiles;
	}
	@RequestMapping(value="/app/vfs/downloadFile/**", method = { RequestMethod.POST, RequestMethod.GET }, produces = {"application/octet-stream"})
	@ResponseStatus(value=HttpStatus.OK)
	public void downloadFile(HttpServletRequest request, HttpServletResponse response) throws RepositoryException, UnknownEntityException, ObjectException {

		setupUserContext(request);
		
		try {
			String path = FileUtils.checkStartsWithSlash(request.getRequestURI().substring(22));
			AbstractFile parent = sshdService.getFileFactory(getCurrentUser()).getFile(path);
			
			if(parent.isDirectory()) {
				throw new IllegalStateException("You cannot download a directory using the downloadFile URL");
			}
			
			response.setContentType("application/octet-stream");
			response.setHeader("Content-Disposition", "attachment filename=\"" + parent.getName() + "\"");
			try(InputStream in = parent.getInputStream()) {
				IOUtils.copy(in, response.getOutputStream());
			}
		} catch (Throwable e) {
			throw new IllegalStateException(e);
		} finally {
			clearUserContext();
		}
	}
}
