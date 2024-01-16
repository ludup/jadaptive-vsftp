package com.jadaptive.plugins.debrep;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs2.AllFileSelector;
import org.apache.commons.vfs2.FileContent;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.FileType;
import org.apache.commons.vfs2.VFS;

import com.sshtools.common.files.AbstractFile;
import com.sshtools.common.files.direct.NioFileFactory;
import com.sshtools.common.files.direct.NioFileFactory.NioFileFactoryBuilder;
import com.sshtools.common.permissions.PermissionDeniedException;
import com.sshtools.common.sftp.SftpFileAttributes;
import com.sshtools.common.sftp.SftpFileAttributes.SftpFileAttributesBuilder;
import com.sshtools.common.util.UnsignedInteger64;

public class Sync {
	final static Log LOG = LogFactory.getLog(Sync.class);

	public enum Result {
		SKIP, UPDATE, ABORT
	}
	
	public interface Completor {
		void complete(AbstractFile sourceRoot, AbstractFile destination);
		
		default void completeRoot(AbstractFile sourceRoot, AbstractFile destinationRoot) {
			complete(sourceRoot, destinationRoot);
		}
	}

	public interface Checker {
		Result check(AbstractFile incoming, AbstractFile existing) throws IOException, PermissionDeniedException;

		void tag(Result result, AbstractFile file, AbstractFile existing) throws IOException, PermissionDeniedException;
	}
	
	public static class AlwaysCopyChecker implements Checker {
		@Override
		public Result check(AbstractFile incoming, AbstractFile existing) throws IOException, PermissionDeniedException {
			return Result.UPDATE;
		}

		@Override
		public void tag(Result result, AbstractFile file, AbstractFile existing)  throws IOException, PermissionDeniedException{
		}
		
	}

	public static class LastModifiedChecker implements Checker {
		@Override
		public Result check(AbstractFile incoming, AbstractFile existing) throws IOException, PermissionDeniedException {
			if (!existing.exists()) {
				LOG.info(String.format("%s doesn't exist, so updating from incoming %s", existing, incoming));
				return Result.UPDATE;
			}
			else {
				if(!incoming.exists()) {
					LOG.warn(String.format("%s doesn't exist at all, so skipping %s", incoming, existing));
					return Result.SKIP;
				}
				long m1 = 0;
				try {
					m1 = incoming.exists() ? incoming.lastModified() : 0;
				}
				catch(Exception e) {
					LOG.warn("Exception checking local file. Assuming doesn't exist.", e);
				}
				long m2 = existing.lastModified();
				if(m1 > m2) {
					LOG.info(String.format("The incoming %s is newer than the existing %s, updating", incoming, existing));
					return Result.UPDATE;
				}
				else {
					LOG.info(String.format("The existing %s is newer or identical to %s, skipping", existing, incoming));
					return Result.SKIP;
				}
			}
		}

		@Override
		public void tag(Result result, AbstractFile incoming, AbstractFile existing) throws FileNotFoundException, IOException, PermissionDeniedException {
			long tm = incoming.lastModified();
			LOG.info(String.format("Setting last modified of %s to %s from %s", existing, tm, incoming));
			
			existing.setAttributes(SftpFileAttributesBuilder.create()
					.withFileAttributes(existing.getAttributes())
					.withLastModifiedTime(incoming.lastModified()).build());
		}
	}

	private List<AbstractFile> sources = new ArrayList<>();
	private AbstractFile destination;
	private boolean recursive = true;
	private boolean preserveAttributes = true;
	private boolean deleteRemoved = true;
	private Checker checker = new LastModifiedChecker();
	private Completor completor;

	public Sync() {
	}

	public Sync(AbstractFile destination, AbstractFile... sources) {
		sources(sources);
		destination(destination);
	}

	public Completor completor() {
		return completor;
	}

	public Sync completor(Completor decorator) {
		this.completor = decorator;
		return this;
	}

	public Sync sources(AbstractFile... sources) {
		this.sources.clear();
		this.sources.addAll(Arrays.asList(sources));
		return this;
	}

	public Sync addSource(AbstractFile source) {
		sources.add(source);
		return this;
	}

	public List<AbstractFile> sources() {
		return sources;
	}

	public Sync destination(AbstractFile destination) {
		this.destination = destination;
		return this;
	}

	public AbstractFile destination() {
		return destination;
	}
	
	public Sync deleteRemoved(boolean deleteRemoved) {
		this.deleteRemoved = deleteRemoved;
		return this;
	}
	
	public boolean deleteRemoved() {
		return deleteRemoved;
	}

	public Sync recursive(boolean recursive) {
		this.recursive = recursive;
		return this;
	}

	public boolean recursive() {
		return recursive;
	}

	public Sync preserveAttributes(boolean preserveAttributes) {
		this.preserveAttributes = preserveAttributes;
		return this;
	}

	public boolean preserveAttributes() {
		return preserveAttributes;
	}

	public Checker checker() {
		return checker;
	}

	public Sync checker(Checker checker) {
		this.checker = checker;
		return this;
	}

	public void sync() throws IOException, PermissionDeniedException {
		/*
		 * If the destination doesn't exist, then only allow the copy if the
		 * parent of the destination exists and we are copying a single file
		 */
		if (!destination.exists()) {
			if (destination.getParentFile() == null || !destination.getParentFile().exists()
					|| !destination.getParentFile().isDirectory() || sources.size() > 1)
				throw new FileNotFoundException("Destination doesn't exist.");
		}
		for (AbstractFile f : sources) {
			sync(f, destination, 0);
			if(Objects.nonNull(completor)) {
				completor.completeRoot(f, destination);
			}
		}
	}

	protected void sync(AbstractFile from, AbstractFile to, int depth) throws IOException, PermissionDeniedException {
		if (from.isDirectory()) {
			if (depth > 0 && !recursive)
				return;
			if (to.isDirectory()
					|| (to.getParentFile() != null && to.getParentFile().exists() && to.getParentFile().isDirectory())) {
				
				List<AbstractFile> exist = new LinkedList<>();
				if(deleteRemoved && to.exists()) {
					exist.addAll(to.getChildren());
				}
				
				if(!to.exists())
					to.createFolder();
				
				for (AbstractFile c : from.getChildren()) {
					AbstractFile target = to.resolveFile(c.getName());
					sync(c, target, depth + 1);
					exist.remove(target);
				}
				
				for(AbstractFile c : exist) {
					LOG.info(String.format("Removing %s as it was removing from the source directory %s", c, to));
					c.delete(true);
				}
			} else if (to.isFile()) {
				throw new IOException(String.format("Could not synchronize %s. Target file %s is a directory", from, to));
			} else {
				throw new IOException(String.format("Could not synchronize %s. Target file %s is imaginary", from, to));
			}
		} else if (from.isFile()) {
			if (to.isDirectory()) {
				syncFile(from, to.resolveFile(from.getName()));
			} else if (to.isFile()
					|| (to.getParentFile() != null && to.getParentFile().exists() && to.getParentFile().isDirectory())) {
				syncFile(from, to);
			} else {
				throw new IOException(String.format("Could not synchronize %s. Target file %s is imaginary", from, to));
			}
		} else
			throw new IOException(String.format("Source file %s is imaginary", from));
	}

	protected void syncFile(AbstractFile from, AbstractFile to) throws IOException, PermissionDeniedException {
		Result result = Result.UPDATE;
		if (checker != null) {
			result = checker.check(from, to);
			if (result == Result.ABORT)
				throw new IOException("Synchronize aborted.");
		}
		if (result == Result.SKIP)
			LOG.info(String.format("Skipping %s to %s", from, to));
		else {
			LOG.info(String.format("Copying %s to %s", from, to));
			to.copyFrom(from);
			if (checker != null)
				checker.tag(result, from, to);
			if (preserveAttributes) {
				
				to.setAttributes(SftpFileAttributesBuilder.create()
						.withFileAttributes(to.getAttributes())
						.withPermissions(from.getAttributes().permissions())
						.build());

			}
			if(completor != null) {
				completor.complete(from, to);
			}
			LOG.info(String.format("Copied %s to %s", from, to));
		}
	}

	public static void main(String[] args) throws Exception {
		if (args.length < 2)
			throw new IllegalArgumentException("Expects at least a source URI and a target URI.");
		NioFileFactory nio = NioFileFactoryBuilder.create().withHome(Paths.get("/Users/lee")).build();
		Sync sync = new Sync();
		int i;
		for (i = 0; i < args.length - 1; i++)
			sync.addSource(nio.getFile(args[i]));
		sync.destination(nio.getFile(args[i]));
		sync.sync();
	}
}
