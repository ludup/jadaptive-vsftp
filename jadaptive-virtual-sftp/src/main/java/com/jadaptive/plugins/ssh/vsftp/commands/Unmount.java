package com.jadaptive.plugins.ssh.vsftp.commands;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import org.jline.reader.Candidate;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;
import org.springframework.beans.factory.annotation.Autowired;

import com.jadaptive.api.entity.ObjectNotFoundException;
import com.jadaptive.plugins.ssh.vsftp.VirtualFileService;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.jadaptive.utils.FileUtils;
import com.sshtools.common.files.vfs.VirtualFileFactory;
import com.sshtools.common.files.vfs.VirtualMount;
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
		super("umount", ShellCommand.SUBSYSTEM_FILESYSTEM, UsageHelper.build("umount path [options]",
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
				FileUtils.checkEndsWithNoSlash(args[1]));
		
		VirtualFileFactory ff = (VirtualFileFactory) console.getFileFactory();
		VirtualMount m = ff.getMount(mount);
		
		if(Objects.isNull(m)) {
			throw new IOException(String.format("%s does not appear to be mounted", mount));
		}
		
		ff.unmount(m);
		
		if(permanent) {
			try {
				fileService.deleteVirtualFolder(fileService.getVirtualFolder(mount));
			} catch(ObjectNotFoundException e) {
				throw new IOException(String.format("%s cannot be deleted because it is not permanent", mount));
			}
			
		}
	}
	
	@Override
	public void complete(LineReader reader, ParsedLine line, List<Candidate> candidates) {
		if(line.wordIndex() == 1) {
			for(VirtualFolder mount : fileService.allObjects()) {
				candidates.add(new Candidate(mount.getMountPath()));
			}
		} 
	}

}
