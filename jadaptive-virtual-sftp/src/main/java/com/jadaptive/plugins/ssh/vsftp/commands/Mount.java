package com.jadaptive.plugins.ssh.vsftp.commands;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.vfs2.CacheStrategy;
import org.springframework.beans.factory.annotation.Autowired;

import com.jadaptive.api.app.ApplicationService;
import com.jadaptive.api.entity.EntityNotFoundException;
import com.jadaptive.api.role.Role;
import com.jadaptive.api.role.RoleService;
import com.jadaptive.api.template.EntityTemplate;
import com.jadaptive.api.template.EntityTemplateService;
import com.jadaptive.api.template.FieldTemplate;
import com.jadaptive.api.template.ValidationType;
import com.jadaptive.api.user.User;
import com.jadaptive.api.user.UserService;
import com.jadaptive.plugins.ssh.vsftp.FileScheme;
import com.jadaptive.plugins.ssh.vsftp.VirtualFileService;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderCredentials;
import com.jadaptive.utils.FileUtils;
import com.sshtools.common.files.AbstractFile;
import com.sshtools.common.files.vfs.VFSFileFactory;
import com.sshtools.common.files.vfs.VirtualMountManager;
import com.sshtools.common.files.vfs.VirtualMountTemplate;
import com.sshtools.common.permissions.PermissionDeniedException;
import com.sshtools.common.util.IOUtils;
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
	
	@Autowired
	private ApplicationService applicationService; 
	
	@Autowired
	private EntityTemplateService templateService; 
	
	
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
			try {
				credentials = promptForCredentials(provider);
			} catch (ParseException e) {
				throw new IOException(e.getMessage(), e);
			}
		}
		
		try {
			URI uri = provider.generateUri(path);
			
			VirtualFolder folder = new VirtualFolder();
			folder.setMountPath(mount);
			folder.setCacheStrategy(cacheStrategy);
			folder.setDestinationUri(path);
			folder.setType(uri.getScheme());
			folder.setCredentials(credentials);
			
			VFSFileFactory factory = fileService.resolveMount(folder);
			
			VirtualMountManager mm = getFileFactory().getMountManager(console.getConnection());
			mm.mount(new VirtualMountTemplate(mount, uri.toASCIIString(), factory));
			
			if(permanent) {
				users.add(user);
				saveMount(folder, roles, users);
			}
		} catch (URISyntaxException e) {
			throw new IOException(e.getMessage(), e);
		}
	}

	private VirtualFolderCredentials promptForCredentials(FileScheme provider) throws ParseException, PermissionDeniedException, IOException {
		
		EntityTemplate template = provider.getCredentialsTemplate();
		
		Map<String, Object> obj = new HashMap<>();
		for(FieldTemplate field : template.getFields()) {
			switch(field.getFieldType()) {
			case TEXT:
				obj.put(field.getResourceKey(), console.readLine(String.format("%s: ", field.getName())));
				break;
			case TEXT_AREA:
			{
				console.println("Enter path to ".concat(field.getName()));
				String filename = console.readLine("Path: ");
				
				AbstractFile file = getFileFactory().getFile(filename, console.getConnection());
				if(!file.exists())  {
					throw new IOException("Could not find file");
				}
				
				obj.put(field.getResourceKey(), IOUtils.readUTF8StringFromStream(file.getInputStream()));

				break;
			}
			case DECIMAL:
			{
				String val; 
				while(true) {
					val = console.readLine(String.format("%s: ", field.getName()));
					try {
						Double.parseDouble(val);
						break;
					} catch(NumberFormatException e) {
						continue;
					}
				}
				obj.put(field.getResourceKey(), val);
				break;
			}
			case BOOL:
			{
				String val; 
				Set<String> validAnswers = new HashSet<>(Arrays.asList("y", "n", "yes", "no"));
				do {
					val = console.readLine(String.format("%s (y/n): ", field.getName()));		 
				} while(!validAnswers.contains(val.toLowerCase()));
				obj.put(field.getResourceKey(), val);
				break;
			}
			case ENUM:
			{
				console.println("Select ".concat(field.getName()).concat(" from the list (type name or index number)"));
				String type = field.getValidationValue(ValidationType.OBJECT_TYPE);
				try {
					@SuppressWarnings("unchecked")
					Class<? extends Enum<?>> clz = (Class<? extends Enum<?>>) applicationService.resolveClass(type);
					
					List<String> values = new ArrayList<>();
					Enum<?>[] constants = clz.getEnumConstants();
					int maximumSize = 0;
					for(Enum<?> e : constants) {
						values.add(e.name());
						maximumSize = Math.max(maximumSize, e.name().length());
					}
					
					int columns = console.getTerminal().getSize().getColumns();
					maximumSize += 8;
					int perLine = (columns / maximumSize) - 1;
					int i = 0;
					int y = 0;
					for(String name : values) {
						if(++y > perLine) {
							y = 1;
							console.println();
						}
						console.print(StringUtils.rightPad(String.format("%02d. %s ", ++i, name), maximumSize));
					}
					console.println();
					String val;
					while(true) {
						val = console.readLine(String.format("%s: ", field.getName()));
						if(NumberUtils.isNumber(val)) {
							int idx = Integer.parseInt(val);
							if(idx > 0 && idx <= values.size()) {
								val = values.get(i-1);
								break;
							}
						} else if(values.contains(val)) {
							break;
						}
						console.println("Invalid value. Try again.");
					}
					obj.put(field.getResourceKey(), val);
				} catch (ClassNotFoundException e) {
					throw new IOException(e.getMessage(), e);
				}
				
				break;
			}
			case NUMBER:
			{
				String val; 
				while(true) {
					val = console.readLine(String.format("%s: ", field.getName()));
					try {
						Long.parseLong(val);
						break;
					} catch(NumberFormatException e) {
						continue;
					}
				}
				obj.put(field.getResourceKey(), val);
				break;
			}
			default:
				
			}
			
		}

		return templateService.createObject(obj, provider.getCredentialsClass());
	}

	private void saveMount(VirtualFolder folder, Collection<Role> roles, Collection<User> users) {
		fileService.createOrUpdate(folder, users, roles);
	}
	
	

}
