package com.jadaptive.plugins.ssh.vsftp.pgp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Iterator;

import org.apache.commons.io.IOUtils;
import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.bcpg.CompressionAlgorithmTags;
import org.bouncycastle.bcpg.HashAlgorithmTags;
import org.bouncycastle.openpgp.PGPCompressedDataGenerator;
import org.bouncycastle.openpgp.PGPEncryptedData;
import org.bouncycastle.openpgp.PGPEncryptedDataGenerator;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPKeyPair;
import org.bouncycastle.openpgp.PGPLiteralData;
import org.bouncycastle.openpgp.PGPLiteralDataGenerator;
import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPPublicKeyRingCollection;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSecretKeyRingCollection;
import org.bouncycastle.openpgp.PGPSignature;
import org.bouncycastle.openpgp.PGPUtil;
import org.bouncycastle.openpgp.operator.PGPDigestCalculator;
import org.bouncycastle.openpgp.operator.jcajce.JcaKeyFingerprintCalculator;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPContentSignerBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPDigestCalculatorProviderBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPKeyPair;
import org.bouncycastle.openpgp.operator.jcajce.JcePBESecretKeyDecryptorBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcePBESecretKeyEncryptorBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcePGPDataEncryptorBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcePublicKeyKeyEncryptionMethodGenerator;

public class PGPUtils {

	static public PGPPublicKey readPublicKey(InputStream input)
			throws IOException, PGPException {
		PGPPublicKeyRingCollection pgpPub = new PGPPublicKeyRingCollection(
				PGPUtil.getDecoderStream(input),
				new JcaKeyFingerprintCalculator());
		Iterator<PGPPublicKeyRing> keyRingIter = pgpPub.getKeyRings();
		while (keyRingIter.hasNext()) {
			PGPPublicKeyRing keyRing = keyRingIter.next();

			Iterator<PGPPublicKey> keyIter = keyRing.getPublicKeys();
			while (keyIter.hasNext()) {
				PGPPublicKey key = keyIter.next();

				if (key.isEncryptionKey()) {
					return key;
				}
			}
		}

		throw new IllegalArgumentException(
				"Can't find encryption key in key ring.");
	}

	public static void encrypt(String filename, OutputStream encodedOutput,
			InputStream inputFile, InputStream keyFile, boolean armor,
			boolean withIntegrityCheck) throws IOException,
			NoSuchProviderException, PGPException {
		
		PGPPublicKey pub = readPublicKey(keyFile);
		try {
		encrypt(filename, encodedOutput, inputFile, pub, armor, withIntegrityCheck);
		} finally {
			IOUtils.closeQuietly(keyFile);
		}
	}
	
	public static void encrypt(String filename, OutputStream encodedOutput,
				InputStream inputFile, PGPPublicKey pubKey, boolean armor,
				boolean withIntegrityCheck) throws IOException,
				NoSuchProviderException, PGPException {
	
		OutputStream toEncrypt = null;
		try {

			if (armor) {
				encodedOutput = new ArmoredOutputStream(encodedOutput);
			}
			
			PGPEncryptedDataGenerator encryptedGenerator = new PGPEncryptedDataGenerator(
					new JcePGPDataEncryptorBuilder(PGPEncryptedData.CAST5)
							.setWithIntegrityPacket(withIntegrityCheck)
							.setSecureRandom(new SecureRandom()).setProvider("BC"));
	
			encryptedGenerator.addMethod(
					new JcePublicKeyKeyEncryptionMethodGenerator(pubKey).setProvider("BC"));
	
			toEncrypt = encryptedGenerator.open(encodedOutput, new byte[65536]);
			compress(filename, inputFile, CompressionAlgorithmTags.ZIP, toEncrypt);

		} finally {
			IOUtils.closeQuietly(toEncrypt);
			IOUtils.closeQuietly(encodedOutput);
		}
		
	}

	public static void compress(String inputFileName, InputStream clearInput,
			int algorithm, OutputStream encodedOutput) throws IOException {
		
		PGPCompressedDataGenerator compressor = 
				new PGPCompressedDataGenerator(algorithm);
		OutputStream toCompress = compressor.open(encodedOutput);
		PGPLiteralDataGenerator dataGenerator = new PGPLiteralDataGenerator();
		OutputStream out = dataGenerator.open(toCompress, PGPLiteralData.BINARY,
				inputFileName, new Date(), new byte[1024]);
		try {
			IOUtils.copyLarge(clearInput, out);
		} finally {
			IOUtils.closeQuietly(out);
			IOUtils.closeQuietly(toCompress);
		}
	}

	static public PGPPrivateKey findSecretKey(
			PGPSecretKeyRingCollection pgpSec, long keyID, char[] pass)
			throws PGPException, NoSuchProviderException {
		PGPSecretKey pgpSecKey = pgpSec.getSecretKey(keyID);

		if (pgpSecKey == null) {
			return null;
		}

		try {
			return pgpSecKey.extractPrivateKey(
					new JcePBESecretKeyDecryptorBuilder()
						.setProvider("BC").build(pass));
		} catch (PGPException pe) {
			throw new PGPException("Incorrect passphrase");
		}
	}

	static public PGPSecretKey generatePGPKey(String name, String email, int bits, char[] passphrase) throws NoSuchAlgorithmException, NoSuchProviderException, PGPException {

         KeyPairGenerator    kpg = KeyPairGenerator.getInstance("RSA", "BC");
         kpg.initialize(bits);  
         KeyPair kp = kpg.generateKeyPair();
         
         PGPDigestCalculator sha1Calc = new JcaPGPDigestCalculatorProviderBuilder().build().get(HashAlgorithmTags.SHA1);
         PGPKeyPair          keyPair = new JcaPGPKeyPair(PGPPublicKey.RSA_GENERAL, kp, new Date());
         return new PGPSecretKey(PGPSignature.DEFAULT_CERTIFICATION, keyPair, 
        		 String.format("%s <%s>", name, email), sha1Calc, null, null,
        		 new JcaPGPContentSignerBuilder(keyPair.getPublicKey().getAlgorithm(), HashAlgorithmTags.SHA1), 
        		 new JcePBESecretKeyEncryptorBuilder(PGPEncryptedData.CAST5, sha1Calc).setProvider("BC").build(passphrase));
         
	}
}
