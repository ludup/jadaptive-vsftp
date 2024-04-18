package com.jadaptive.plugins.ssh.vsftp.sendto;

import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;

import com.sshtools.common.permissions.PermissionDeniedException;

import jakarta.servlet.http.HttpServletResponse;

public interface SendToService {

	boolean isReceiverConnected(String shareCode, Integer count);

	void registerTransfer(String shareCode);

	void receiveFile(String shareCode, HttpServletResponse response) throws InterruptedException;

	void completeUpload(String shareCode) throws IOException;

	boolean status(String shareCode);

	void sendFile(String shareCode, String filename, InputStream in, long contentLength)
			throws NoSuchAlgorithmException, IOException, PermissionDeniedException;

}
