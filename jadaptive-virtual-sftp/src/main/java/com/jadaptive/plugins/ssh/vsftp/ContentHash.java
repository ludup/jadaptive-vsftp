package com.jadaptive.plugins.ssh.vsftp;

public enum ContentHash {

	MD5("MD5", 4),
	SHA1("SHA1", 4),
	SHA256("SHA-256", 6),
	SHA512("SHA-512", 6);
	
	String algorithm;
	int words;
	
	ContentHash(String algorithm, int words) {
		this.algorithm = algorithm;
		this.words = words;
	}
	
	public String getAlgorithm() {
		return algorithm;
	}
	
	public int getWords() {
		return words;
	}
}
