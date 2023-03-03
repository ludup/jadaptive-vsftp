package com.jadaptive.plugins.ssh.vsftp.pgp;

import java.io.IOException;
import java.io.OutputStream;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Objects;
import java.util.Stack;

import org.apache.commons.io.IOUtils;
import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.bcpg.CompressionAlgorithmTags;
import org.bouncycastle.openpgp.PGPCompressedDataGenerator;
import org.bouncycastle.openpgp.PGPEncryptedData;
import org.bouncycastle.openpgp.PGPEncryptedDataGenerator;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPLiteralData;
import org.bouncycastle.openpgp.PGPLiteralDataGenerator;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.operator.jcajce.JcePGPDataEncryptorBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcePublicKeyKeyEncryptionMethodGenerator;

public class PGPEncryptionOutputStream extends OutputStream {

	OutputStream out = null;
	Stack<OutputStream> streams = new Stack<>();
	
	public PGPEncryptionOutputStream(OutputStream destination, 
			PGPPublicKey publicKey, 
			String originalFilename,
			boolean armour,
			boolean compress,
			boolean integrityCheck) throws IOException, PGPException {

		
		streams.push(destination);
		
		if (armour) {
			destination = new ArmoredOutputStream(destination);
			streams.push(destination);
		}

		PGPEncryptedDataGenerator encryptedGenerator = new PGPEncryptedDataGenerator(
				new JcePGPDataEncryptorBuilder(PGPEncryptedData.AES_256)
						.setWithIntegrityPacket(integrityCheck)
						.setSecureRandom(new SecureRandom()).setProvider("BC"));

		encryptedGenerator.addMethod(
				new JcePublicKeyKeyEncryptionMethodGenerator(publicKey).setProvider("BC"));

		destination = encryptedGenerator.open(destination, new byte[65536]);
		streams.push(destination);
		
		if(compress) {
			PGPCompressedDataGenerator compressor = 
				new PGPCompressedDataGenerator(CompressionAlgorithmTags.ZIP);
			destination = compressor.open(destination);
			streams.push(destination);
		}
			
		PGPLiteralDataGenerator dataGenerator = new PGPLiteralDataGenerator();
		destination = dataGenerator.open(destination, PGPLiteralData.BINARY,
				originalFilename, new Date(), new byte[65536]);

		streams.push(destination);
		this.out = destination;
	}

	public void write(byte[] buf, int off, int len) throws IOException {
		out.write(buf, off, len);
	}
	
	public void write(int b) throws IOException {
		out.write(b);
	}
	
	@SuppressWarnings("deprecation")
	public void close() throws IOException {
		if(Objects.nonNull(out)) {
			out.flush();
			IOUtils.closeQuietly(out);
			out = null;
			
			while(!streams.isEmpty()) {
				try {
					OutputStream out = streams.pop();
					out.flush();
					IOUtils.closeQuietly(out);
				} catch (IOException e) {
				}
			}

		}
		
	}
}
