package com.jadaptive.plugins.ssh.vsftp.tasks;

import java.io.IOException;
import java.io.InputStream;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import org.apache.commons.io.output.CountingOutputStream;
import org.springframework.beans.factory.annotation.Autowired;

import com.jadaptive.api.db.SingletonObjectDatabase;
import com.jadaptive.api.tasks.TaskResult;
import com.jadaptive.plugins.ssh.vsftp.ContentHash;
import com.jadaptive.plugins.ssh.vsftp.VFSConfiguration;
import com.jadaptive.plugins.ssh.vsftp.events.TransferResult;
import com.jadaptive.utils.Utils;
import com.sshtools.common.files.AbstractFile;
import com.sshtools.common.permissions.PermissionDeniedException;
import com.sshtools.humanhash.HumanHashGenerator;

public abstract class AbstractFileTransferTaskImpl<T extends AbstractFileUploadTask> extends AbstractFileTaskImpl<T> {

	@Autowired
	private SingletonObjectDatabase<VFSConfiguration> configuration;

	@Override
	public TaskResult doTask(T task, String executionId) {
		
		feedbackService.info(executionId, AbstractFileTargetTask.BUNDLE, "uploadingFile.text", task.getSource().getFilename());
		
		long count = 0;
		Date started = Utils.now();
		ContentHash contentHash = configuration.getObject(VFSConfiguration.class).getDefaultHash();
		
		try {
			AbstractFile file = resolveFile(task.getSource().getLocation(), task.getSource().getFilename());
			AbstractFile dest = resolveUploadFile(task);
		
			if(dest.isDirectory()) {
				dest = dest.resolveFile(file.getName());
			}
			
			MessageDigest digest = MessageDigest.getInstance(contentHash.getAlgorithm());
			try(CountingOutputStream digestOutput = new CountingOutputStream(
					new DigestOutputStream(dest.getOutputStream(task.getAppendContents()), digest))) {
			
				try(InputStream in = file.getInputStream()) {
		
					int r;
					
					byte[] buf = new byte[task.getBlockSize()];
					
					feedbackService.startProgress(executionId, 0, file.length());
					
					while(true) {
						r = in.read(buf);
						if(r == -1) {
							break;
						}
						if(r > 0) {
							digestOutput.write(buf, 0, r);
							feedbackService.progress(executionId, count+=r);
						}
					}
					
					feedbackService.endProgress(executionId);
					
				}
			}
		
			byte[] output = digest.digest();
			String hash = VFSUtils.formatDigest(digest.getAlgorithm(), output);
			String humanHash = new HumanHashGenerator(output)
					.words(contentHash.getWords())
					.build();
			
			TransferResult result = new TransferResult(file.getName(), 
					dest.getAbsolutePath(), count, started, new Date(), hash, humanHash);
			
			return new FileUploadResult(result, task.getAppendContents());
		} catch (PermissionDeniedException | IOException | NoSuchAlgorithmException e) {
			
			return new FileLocationResult(task.getSource().getLocation(), task.getSource().getFilename(), e);
		}
	}

	protected abstract AbstractFile resolveUploadFile(T task);

}
