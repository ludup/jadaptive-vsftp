package com.jadaptive.plugins.ssh.vsftp.commands;

import java.io.IOException;
import java.text.ParseException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.vfs2.CacheStrategy;
import org.springframework.beans.factory.annotation.Autowired;

import com.jadaptive.api.entity.ObjectNotFoundException;
import com.jadaptive.api.role.Role;
import com.jadaptive.api.role.RoleService;
import com.jadaptive.api.template.TemplateService;
import com.jadaptive.api.user.User;
import com.jadaptive.api.user.UserService;
import com.jadaptive.plugins.ssh.vsftp.FileScheme;
import com.jadaptive.plugins.ssh.vsftp.VirtualFileService;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderCredentials;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderOptions;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderPath;
import com.jadaptive.plugins.sshd.ConsoleHelper;
import com.jadaptive.utils.FileUtils;
import com.sshtools.common.files.AbstractFileFactory;
import com.sshtools.common.files.vfs.VirtualFileFactory;
import com.sshtools.common.files.vfs.VirtualMountManager;
import com.sshtools.common.files.vfs.VirtualMountTemplate;
import com.sshtools.common.permissions.PermissionDeniedException;
import com.sshtools.server.vsession.CliHelper;
import com.sshtools.server.vsession.ShellCommand;
import com.sshtools.server.vsession.UsageException;
import com.sshtools.server.vsession.UsageHelper;
import com.sshtools.server.vsession.VirtualConsole;

public class Mount extends AbstractVFSCommand {

	@Autowired
	private VirtualFileService fileService; 

	@Autowired
	private RoleService roleService; 
	
	@Autowired
	private UserService userService;  
	
	@Autowired
	private TemplateService templateService; 
	
	@Autowired
	private ConsoleHelper consoleHelper;
	
	public Mount() {
		super("mount", ShellCommand.SUBSYSTEM_FILESYSTEM,  UsageHelper.build("mount [options] path destination",
				"-p, --permanent						Store this mount for future use",
				"-o, --override						    Override an existing mount",
				"-t, --type <name>						The type of file system to mount",
				"-r, --roles <names>					Comma separated list of role names to assign to",
				"-u, --users <names>					Comma separated list of user names to assign to"), 
				"Mount virtual folders");
	}

	@Override
	protected void doRun(String[] args, VirtualConsole console)
			throws IOException, PermissionDeniedException, UsageException {
		
		if(args.length < 3) {
			throw new UsageException("Not enough arguments provided");
		}
		
		boolean permanent = CliHelper.hasOption(args, 'p', "permanent");
		
		String type = CliHelper.getValue(args, 't', "type", "");
		
		if(StringUtils.isBlank(type)) {
			type = "file";
		}
		
		type = type.toLowerCase();
		@SuppressWarnings("unused")
		CacheStrategy cacheStrategy = CacheStrategy.ON_RESOLVE;
		
		String mount = FileUtils.checkStartsWithSlash(
				FileUtils.checkEndsWithNoSlash(args[args.length - 2]));
		
		fileService.assertSupportedMountType(type);
		
		Set<Role> roles = new HashSet<>();
		String tmp = CliHelper.getValue(args, 'r', "roles", "");
		if(StringUtils.isNotBlank(tmp)) {
			for(String name : tmp.split(",")) {
				try {
				roles.add(roleService.getRoleByName(name));
				} catch(ObjectNotFoundException e) { 
					throw new UsageException(String.format("%s is not a valid role name", name));
				} 
			}
		}
		
		Set<User> users = new HashSet<>();
		tmp = CliHelper.getValue(args, 'u', "users", "");
		if(StringUtils.isNotBlank(tmp)) {
			for(String name : tmp.split(",")) {
				try {
				users.add(userService.getUser(name));
				} catch(ObjectNotFoundException e) { 
					throw new UsageException(String.format("%s is not a valid user name", name));
				} 
			}
		}
		
		boolean performMount = users.contains(currentUser) || roleService.hasRole(currentUser, roles);
		boolean unmountFirst = CliHelper.hasOption(args, 'o', "override");
		
		if(!unmountFirst) {
			if(performMount) {
				if(fileService.checkMountExists(mount, currentUser)) {
					throw new UsageException(String.format("%s is alredy mounted", mount));
				}
			}
		}
		

		FileScheme provider = fileService.getFileScheme(type);
		Map<String,String> mountOptions = new HashMap<>();
		
		if(provider.hasExtendedOptions() && CliHelper.hasOption(args, 'o', "options")) {
			String options = CliHelper.getValue(args, 'o', "options");
			if(StringUtils.isNotBlank(options)) {
				for(String option : options.split(",")) {
					mountOptions.put(StringUtils.substringBefore(option, "="), 
							StringUtils.substringAfter(option, "="));
				}
			}
		}
				
		VirtualFolderCredentials credentials = null;
		if(provider.requiresCredentials()) {
			try {
				credentials = promptForCredentials(provider);
			} catch (ParseException e) {
				throw new IOException(e.getMessage(), e);
			}
		}
		
		try {
			VirtualFolder folder = provider.createFolder();
			folder.setMountPath(mount);
			
			//folder.setPath(generatePath(path, cacheStrategy));
			
			if(provider.requiresCredentials()) {
				provider.setCredentials(folder, credentials);
			}
			if(!mountOptions.isEmpty() && provider.hasExtendedOptions()) {
				provider.setOptions(folder, generateMountOptions(mountOptions, provider));
			}
			
			AbstractFileFactory<?> factory = fileService.resolveMount(folder);
			
			VirtualFileFactory ff = (VirtualFileFactory) console.getFileFactory();
			
			String uri = folder.getPath().generatePath();
			
			VirtualMountManager mm = ff.getMountManager();
			VirtualMountTemplate template = new VirtualMountTemplate(mount, 
					uri, 
					factory, 
					provider.createRoot());
					
			if(performMount) {
				mm.mount(template, unmountFirst);
			} else {
				mm.test(template);
			}
			
			if(permanent) {
				saveMount(folder, roles, users);
			}
			
		} catch (ParseException e) {
			throw new IOException(e.getMessage(), e);
		}
	}

	@SuppressWarnings("unused")
	private VirtualFolderPath generatePath(FileScheme provider) throws ParseException, IOException, PermissionDeniedException {
		
		Map<String, Object> doc = new HashMap<>();
		
		consoleHelper.promptTemplate(console, doc, 
				provider.getPathTemplate(),
				null,
				provider.getPathClass().getName());
		
		return templateService.createObject(doc, provider.getPathClass());
	}
	
	private VirtualFolderOptions generateMountOptions(Map<String, String> mountOptions, FileScheme provider) throws ParseException, IOException, PermissionDeniedException {
		
		Map<String, Object> doc = new HashMap<>();
		
		consoleHelper.promptTemplate(console, doc, 
				provider.getCredentialsTemplate(), 
				null,
				provider.getCredentialsClass().getName());
		
		return templateService.createObject(doc, provider.getOptionsClass());

	}

	private VirtualFolderCredentials promptForCredentials(FileScheme provider) throws ParseException, PermissionDeniedException, IOException {
		
		Map<String,Object> doc =  new HashMap<>();	
		consoleHelper.promptTemplate(console, doc, 
				provider.getCredentialsTemplate(), 
				null,
				provider.getCredentialsClass().getName());
		return templateService.createObject(doc, 
				provider.getCredentialsClass());
		
	}

	private void saveMount(VirtualFolder folder, Collection<Role> roles, Collection<User> users) throws IOException {
		fileService.createOrUpdate(folder, users, roles);
	}
}
