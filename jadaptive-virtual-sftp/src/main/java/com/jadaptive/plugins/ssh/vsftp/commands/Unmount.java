package com.jadaptive.plugins.ssh.vsftp.commands;

import java.io.IOException;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;

import com.jadaptive.api.entity.EntityNotFoundException;
import com.jadaptive.plugins.ssh.vsftp.VirtualFileService;
import com.jadaptive.utils.FileUtils;
import com.sshtools.common.files.vfs.VirtualFileFactory;
import com.sshtools.common.files.vfs.VirtualMount;
import com.sshtools.common.files.vfs.VirtualMountManager;
import com.sshtools.common.permissions.PermissionDeniedException;
import com.sshtools.server.vsession.CliHelper;
import com.sshtools.server.vsession.ShellCommand;
import com.sshtools.server.vsession.UsageException;
import com.sshtools.server.vsession.UsageHelper;
import com.sshtools.server.vsession.VirtualConsole;

public class Unmount extends AbstractVFSCommand {

	@Autowired
	private VirtualFileService fileService; 
	
	public Unmount() {
		super("umount", ShellCommand.SUBSYSTEM_FILESYSTEM, UsageHelper.build("umount [options] path",
				"-p, --permanent						Remove this permanent mount"), 
				"Unmount virtual folders");
	}

	@Override
	protected void doRun(String[] args, VirtualConsole console)
			throws IOException, PermissionDeniedException, UsageException {
		
		if(args.length < 2) {
			throw new UsageException("Not enough arguments provided");
		}
		
		boolean permanent = CliHelper.hasOption(args, 'p', "permanent");
		String mount = FileUtils.checkStartsWithSlash(
				FileUtils.checkEndsWithNoSlash(args[args.length-1]));
		
		VirtualFileFactory ff = (VirtualFileFactory) console.getFileFactory();
		VirtualMountManager mm = ff.getMountManager();
		VirtualMount m = mm.getMount(mount);
		
		if(Objects.isNull(m)) {
			throw new IOException(String.format("%s does not appear to be mounted", mount));
		}
		
		mm.unmount(m);
		
		if(permanent) {
			try {
				fileService.deleteVirtualFolder(fileService.getVirtualFolder(mount));
			} catch(EntityNotFoundException e) {
				throw new IOException(String.format("%s cannot be deleted because it is not permanent", mount));
			}
			
		}
	}

}
