package com.jadaptive.plugins.ssh.vsftp.pgp;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Stack;

import org.apache.commons.io.IOUtils;
import org.bouncycastle.openpgp.PGPCompressedData;
import org.bouncycastle.openpgp.PGPEncryptedDataList;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPLiteralData;
import org.bouncycastle.openpgp.PGPOnePassSignatureList;
import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPPublicKeyEncryptedData;
import org.bouncycastle.openpgp.PGPUtil;
import org.bouncycastle.openpgp.jcajce.JcaPGPObjectFactory;
import org.bouncycastle.openpgp.operator.jcajce.JcePublicKeyDataDecryptorFactoryBuilder;

public class PGPDecryptionInputStream extends InputStream {

	InputStream in = null;
	PGPPublicKeyEncryptedData encryptedData;
	Stack<InputStream> streams =  new Stack<>();
	public PGPDecryptionInputStream(InputStream source, 
			PGPPrivateKey secretKey) throws IOException, PGPException {
		
		streams.push(source);
		source = PGPUtil.getDecoderStream(source);
		streams.push(source);
		
		JcaPGPObjectFactory pgpFactory = new JcaPGPObjectFactory(source);

		Object o = pgpFactory.nextObject();
		while(o!=null && !(o instanceof PGPEncryptedDataList)) {
			o = pgpFactory.nextObject();
		}
		
		if(o==null) {
			throw new IOException("Could not find PGPEncryptedDataList in stream");
		}

		PGPEncryptedDataList dataList = (PGPEncryptedDataList)o;
		encryptedData = (PGPPublicKeyEncryptedData) dataList.getEncryptedDataObjects().next();

		if(encryptedData.getKeyID()!=secretKey.getKeyID()) {
			throw new IOException("Mismatch in PGP encryption keys detected!");
		}
		
		InputStream clearData = encryptedData
				.getDataStream(new JcePublicKeyDataDecryptorFactoryBuilder()
						.setProvider("BC").build(secretKey));

		JcaPGPObjectFactory objectFactory = new JcaPGPObjectFactory(clearData);

		Object message = objectFactory.nextObject();

		if (message instanceof PGPCompressedData) {
			PGPCompressedData compressedData = (PGPCompressedData) message;
			objectFactory = new JcaPGPObjectFactory(
					compressedData.getDataStream());

			message = objectFactory.nextObject();
		}

		if (message instanceof PGPLiteralData) {
			PGPLiteralData literalData = (PGPLiteralData) message;
			source = literalData.getInputStream();
			streams.push(source);
		} else if (message instanceof PGPOnePassSignatureList) {
			throw new PGPException(
					"encrypted message contains a signed message - not literal data.");
		} else {
			throw new PGPException(
					"message is not a simple encrypted file! " + message.getClass().getSimpleName());
		}
		
		this.in = source;
		
	}
	
	@SuppressWarnings("deprecation")
	public int read(byte[] buf, int off, int len) throws IOException {
		
		try {
			
			int r = in.read(buf, off, len);
			
			if(r == -1) {
				IOUtils.closeQuietly(in);
				if (encryptedData.isIntegrityProtected()) {
					boolean passIntegrityCheck = encryptedData.verify();
					if(!passIntegrityCheck) {
						throw new IOException("File integrity check failed!");
					}
				} 
			}
			
			return r;
		} catch (PGPException e) {
			throw new IOException(e.getMessage(), e);
		}
	}

	@Override
	public int read() throws IOException {
		byte[] b = new byte[1];
		int r = read(b);
		if(r==1) {
			return b[0] & 0xFF;
		}
		return r;
	}
	
	@SuppressWarnings("deprecation")
	public void close() throws IOException {
	
		if(Objects.nonNull(in)) {

			IOUtils.closeQuietly(in);;
			in = null;
			
			while(!streams.isEmpty()) {
				InputStream in = streams.pop();
				IOUtils.closeQuietly(in);
			}
		}
	}

}
