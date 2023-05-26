package com.jadaptive.plugins.ssh.vsftp.pgp;

import java.io.IOException;
import java.security.NoSuchProviderException;

import org.bouncycastle.openpgp.PGPException;

import com.sshtools.common.events.Event;
import com.sshtools.common.files.AbstractFileFactory;
import com.sshtools.common.permissions.PermissionDeniedException;

public class EncryptingFileFactory implements AbstractFileFactory<EncryptingFile> {

	AbstractFileFactory<?> sourceFactory;
	PGPEncryption enc;
	
	public EncryptingFileFactory(AbstractFileFactory<?> sourceFactory, PGPEncryption enc) {
		this.sourceFactory = sourceFactory;
		this.enc = enc;
	}

	@Override
	public EncryptingFile getFile(String path) throws PermissionDeniedException, IOException {
		try {
			return new EncryptingFile(sourceFactory.getFile(path), enc, this);
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
			return new EncryptingFile(sourceFactory.getDefaultPath(), enc, this);
		} catch (NoSuchProviderException | IOException | PGPException | PermissionDeniedException e) {
			throw new IOException(e.getMessage(), e);
		}
	}

}
