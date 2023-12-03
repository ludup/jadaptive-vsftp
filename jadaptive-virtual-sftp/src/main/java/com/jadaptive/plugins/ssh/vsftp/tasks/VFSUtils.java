package com.jadaptive.plugins.ssh.vsftp.tasks;

public class VFSUtils {

	
	public static String formatDigest(String algorithm, byte[] output) {
		
		StringBuffer tmp = new StringBuffer();
		tmp.append(algorithm);
		tmp.append(":");
		tmp.append(com.sshtools.common.util.Utils.bytesToHex(output, output.length, false, false));
		return tmp.toString();
	}
}
