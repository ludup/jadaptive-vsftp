package com.jadaptive.plugins.ssh.vsftp.tasks;

import org.springframework.beans.factory.annotation.Autowired;

import com.jadaptive.api.tasks.FeedbackService;
import com.sshtools.client.tasks.FileTransferProgress;

public class FeedbackFileTransferProgress implements FileTransferProgress {

	@Autowired
	private FeedbackService feedbackService;
	
	String executionId;
	long bytesDone;
	long percentageBlock;
	int percentageDone;
	long currentBlock;
	
	public FeedbackFileTransferProgress(String executionId) {
		this.executionId = executionId;
	}
	
	public void started(long bytesTotal, String file) {
		
		percentageBlock = bytesTotal / 100;
		percentageDone = 0;
		percentageBlock = 0;
		bytesDone = 0;
		
		feedbackService.startProgress(file, 0, 100);
	};

	public boolean isCancelled() { return false; };

	public void progressed(long bytesSoFar) {
		
		long block = bytesSoFar - bytesDone;
		bytesDone = bytesSoFar;
		
		currentBlock += block;
		if(currentBlock >= percentageBlock) {
			feedbackService.progress(executionId, ++percentageDone);
			currentBlock = currentBlock - percentageBlock;
		}
	};

	public void completed() { 
		feedbackService.endProgress(executionId);
	};

}
