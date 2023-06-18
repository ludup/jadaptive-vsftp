package com.jadaptive.plugins.ssh.vsftp.pgp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.NoSuchProviderException;

import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPSecretKeyRingCollection;
import org.bouncycastle.openpgp.PGPUtil;
import org.bouncycastle.openpgp.operator.jcajce.JcaKeyFingerprintCalculator;

import com.jadaptive.api.app.ApplicationServiceImpl;
import com.jadaptive.api.encrypt.EncryptionService;
import com.sshtools.common.files.AbstractFile;
import com.sshtools.common.files.AbstractFileAdapter;
import com.sshtools.common.files.AbstractFileFactory;
import com.sshtools.common.permissions.PermissionDeniedException;
import com.sshtools.common.util.IOUtils;

public class EncryptingFile extends AbstractFileAdapter {

	PGPEncryption encryption;
	PGPPublicKey publicKey;
	PGPPrivateKey secretKey;
	EncryptingFileFactory factory;
	
	public EncryptingFile(AbstractFile file, PGPEncryption encryption, EncryptingFileFactory factory) throws IOException, PGPException, NoSuchProviderException {
		super(file);
		if(!encryption.getEncrypt()) {
			throw new IllegalStateException("EncryptingFile can only work with VirtualFolder with encryption turned on!");
		}
		if(StringUtils.isAnyBlank(encryption.getPrivateKey(), encryption.getPassphrase(), encryption.getPublicKey())) {
			throw new IllegalStateException("Encrypting folder requires private, public keys and a passphrase!");
		}

		try(InputStream in = IOUtils.toInputStream(encryption.getPublicKey(), "UTF-8")) {
			publicKey = PGPUtils.readPublicKey(in);
		}
		
		try(InputStream in = IOUtils.toInputStream(encryption.getPrivateKey(), "UTF-8")) {

			PGPSecretKeyRingCollection ringCollection = 
					new PGPSecretKeyRingCollection(PGPUtil.getDecoderStream(in),
							new JcaKeyFingerprintCalculator());
			secretKey = PGPUtils.findSecretKey(ringCollection, publicKey.getKeyID(),
					ApplicationServiceImpl.getInstance()
						.getBean(EncryptionService.class).decrypt(encryption.getPassphrase()).toCharArray());
		}
		
		this.encryption = encryption;
	}

	
	
	@Override
	public InputStream getInputStream() throws IOException, PermissionDeniedException {
		try {
			return new PGPDecryptionInputStream(super.getInputStream(), secretKey);
		} catch (IOException | PGPException | PermissionDeniedException e) {
			throw new IOException(e.getMessage(), e);
		}
	}

	@Override
	public OutputStream getOutputStream() throws IOException, PermissionDeniedException {
		try {
			return new PGPEncryptionOutputStream(super.getOutputStream(), publicKey, getName(),
								encryption.getArmour(), encryption.getCompress(), encryption.getIntegrityCheck());
		} catch (IOException | PGPException | PermissionDeniedException e) {
			throw new IOException(e.getMessage(), e);
		}
	}



	@Override
	public AbstractFileFactory<? extends AbstractFile> getFileFactory() {
		return factory;
	}

}
