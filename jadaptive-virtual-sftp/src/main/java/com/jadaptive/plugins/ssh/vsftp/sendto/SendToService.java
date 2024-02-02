package com.jadaptive.plugins.ssh.vsftp.sendto;

import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;

import javax.servlet.http.HttpServletResponse;

import com.sshtools.common.permissions.PermissionDeniedException;

public interface SendToService {

	boolean isReceiverConnected(String shareCode, Integer count);

	void registerTransfer(String shareCode);

	void sendFile(String shareCode, String filename, InputStream in) throws NoSuchAlgorithmException, IOException, PermissionDeniedException;

	void receiveFile(String shareCode, HttpServletResponse response) throws InterruptedException;

	void completeUpload(String shareCode) throws IOException;

	boolean status(String shareCode);

}
