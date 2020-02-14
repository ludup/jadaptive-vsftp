package com.jadaptive.plugins.ssh.vsftp.commands;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.vfs2.CacheStrategy;
import org.springframework.beans.factory.annotation.Autowired;

import com.jadaptive.api.entity.EntityNotFoundException;
import com.jadaptive.api.entity.EntityService;
import com.jadaptive.api.role.Role;
import com.jadaptive.api.role.RoleService;
import com.jadaptive.api.template.EntityTemplate;
import com.jadaptive.api.template.FieldTemplate;
import com.jadaptive.api.user.User;
import com.jadaptive.api.user.UserService;
import com.jadaptive.plugins.ssh.vsftp.FileScheme;
import com.jadaptive.plugins.ssh.vsftp.VirtualFileService;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderCredentials;
import com.jadaptive.utils.FileUtils;
import com.sshtools.common.files.vfs.VFSFileFactory;
import com.sshtools.common.files.vfs.VirtualMountManager;
import com.sshtools.common.permissions.PermissionDeniedException;
import com.sshtools.server.vsession.CliHelper;
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
	
	public Mount() {
		super("mount", "Virtual File System",  UsageHelper.build("mount [options] path destination",
				"-p, --permanent						Store this mount for future use",
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
		CacheStrategy cacheStrategy = CacheStrategy.ON_RESOLVE;
		
		String mount = FileUtils.checkStartsWithSlash(
				FileUtils.checkEndsWithNoSlash(args[args.length - 2]));
		String path = args[args.length - 1];
		
		if(!fileService.checkSupportedMountType(type)) {
			throw new UsageException(String.format("%s is not a supported file type", type));
		}
		
		if(fileService.checkMountExists(mount, user)) {
			throw new UsageException(String.format("%s is alredy mounted", mount));
		}
		
		Set<Role> roles = new HashSet<>();
		String tmp = CliHelper.getValue(args, 'r', "roles", "");
		if(StringUtils.isNotBlank(tmp)) {
			for(String name : tmp.split(",")) {
				try {
				roles.add(roleService.getRoleByName(name));
				} catch(EntityNotFoundException e) { 
					throw new UsageException(String.format("%s is not a valid role name", name));
				} 
			}
		}
		
		Set<User> users = new HashSet<>();
		tmp = CliHelper.getValue(args, 'u', "users", "");
		if(StringUtils.isNotBlank(tmp)) {
			for(String name : tmp.split(",")) {
				try {
				users.add(userService.findUsername(name));
				} catch(EntityNotFoundException e) { 
					throw new UsageException(String.format("%s is not a valid user name", name));
				} 
			}
		}
		
		FileScheme provider = fileService.getFileScheme(type);
		
		VirtualFolderCredentials credentials = null;
		if(provider.requiresCredentials()) {
			credentials = promptForCredentials(provider);
		}
		
		try {
			URI uri = provider.generateUri(path);
			
			VirtualFolder folder = new VirtualFolder();
			folder.setMountPath(mount);
			folder.setCacheStrategy(cacheStrategy);
			folder.setDestinationUri(path);
			folder.setType(uri.getScheme());
			
			fileService.resolveMount(folder);
			
			VirtualMountManager mm = getFileFactory().getMountManager(console.getConnection());
			mm.mount(mm.createMount(mount, uri.toASCIIString(), new VFSFileFactory()));
			
			if(permanent) {
				users.add(user);
				saveMount(folder, roles, users);
			}
		} catch (URISyntaxException e) {
			throw new IOException(e.getMessage(), e);
		}
	}

	private VirtualFolderCredentials promptForCredentials(FileScheme provider) {
		
		EntityTemplate template = provider.getCredentialsTemplate();
		
		Map<String,String> obj = new HashMap<>();
		for(FieldTemplate field : template.getFields()) {
			obj.put(field.getResourceKey(), console.readLine(String.format("%s: ", field.getName())));
		}

		return null;
	}

	private void saveMount(VirtualFolder folder, Collection<Role> roles, Collection<User> users) {
		fileService.createOrUpdate(folder, users, roles);
	}
	
	

}
