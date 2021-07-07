package com.jadaptive.plugins.ssh.vsftp.commands;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.jadaptive.api.permissions.AccessDeniedException;
import com.jadaptive.plugins.ssh.vsftp.FileScheme;
import com.jadaptive.plugins.ssh.vsftp.VirtualFileService;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.jadaptive.utils.Utils;
import com.sshtools.common.permissions.PermissionDeniedException;
import com.sshtools.server.vsession.CliHelper;
import com.sshtools.server.vsession.ShellCommand;
import com.sshtools.server.vsession.UsageException;
import com.sshtools.server.vsession.UsageHelper;
import com.sshtools.server.vsession.VirtualConsole;

public class Vfs extends AbstractVFSCommand {

	@Autowired
	private VirtualFileService fileService; 
	
	public Vfs() {
		super("vfs", ShellCommand.SUBSYSTEM_FILESYSTEM, UsageHelper.build("vfs [options]",
				"-l, --list                          List all the permanent mounts",
				"-t, --types                         List all the supported mount types"), 
				"VFS information");
	}

	@Override
	protected void doRun(String[] args, VirtualConsole console)
			throws IOException, PermissionDeniedException, UsageException {
		
		if(CliHelper.hasOption(args, 't', "types")) {
			console.print(StringUtils.rightPad("Scheme", 10));
			console.print(StringUtils.rightPad("Name", 20));
			console.println("Aliases");
			console.print(StringUtils.rightPad("______", 10));
			console.print(StringUtils.rightPad("____", 20));
			console.println("_______");
			console.println();
			for(FileScheme<?> scheme : fileService.getSchemes()) {
				console.print(StringUtils.rightPad(scheme.getScheme(), 10));
				console.print(StringUtils.rightPad(scheme.getName(), 20));
				console.println(Utils.csv(scheme.types()));
			}
		} else {
			
			console.print(StringUtils.rightPad("Mount", 15));
			console.print(StringUtils.rightPad("Scheme", 10));
			console.print(StringUtils.rightPad("Short Code", 12));
			console.println("Destination");
			console.print(StringUtils.rightPad("-----", 15));
			console.print(StringUtils.rightPad("------", 10));
			console.print(StringUtils.rightPad("----------", 12));
			console.println("-----------");
			
			Iterable<VirtualFolder> folders;
			
			try {
				assertAdministrationPermission();
				folders = fileService.allObjects();
			} catch(AccessDeniedException e) {
				folders = fileService.getPersonalFolders();
			}
			
			for(VirtualFolder folder : folders) {
				console.print(StringUtils.rightPad(folder.getMountPath(), 15));
				console.print(StringUtils.rightPad(folder.getType(), 10));
				console.print(StringUtils.rightPad(folder.getShortCode(), 12));
				console.println(folder.getPath().generatePath());
			}
		}
		
		console.println();

	}

}
