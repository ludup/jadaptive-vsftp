package com.jadaptive.plugins.ssh.vsftp.pgp;

import java.io.IOException;
import java.security.NoSuchProviderException;

import org.bouncycastle.openpgp.PGPException;

import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.jadaptive.plugins.ssh.vsftp.behaviours.PGPBehaviour;
import com.sshtools.common.events.Event;
import com.sshtools.common.files.AbstractFileFactory;
import com.sshtools.common.permissions.PermissionDeniedException;

public class EncryptingFileFactory implements AbstractFileFactory<EncryptingFile> {

	AbstractFileFactory<?> sourceFactory;
	VirtualFolder folder;
	PGPBehaviour pgp;
	
	public EncryptingFileFactory(PGPBehaviour pgp, AbstractFileFactory<?> sourceFactory, VirtualFolder folder) {
		this.pgp = pgp;
		this.sourceFactory = sourceFactory;
		this.folder = folder;
	}

	@Override
	public EncryptingFile getFile(String path) throws PermissionDeniedException, IOException {
		try {
			return new EncryptingFile(pgp, sourceFactory.getFile(path), folder);
		} catch (NoSuchProviderException | IOException | PGPException | PermissionDeniedException e) {
			throw new IOException(e.getMessage(), e);
		}
	}

	@Override
	public Event populateEvent(Event evt) {
		return sourceFactory.populateEvent(evt);
	}

	@Override
	public EncryptingFile getDefaultPath() throws PermissionDeniedException, IOException {
		try {
			return new EncryptingFile(pgp, sourceFactory.getDefaultPath(), folder);
		} catch (NoSuchProviderException | IOException | PGPException | PermissionDeniedException e) {
			throw new IOException(e.getMessage(), e);
		}
	}

}
