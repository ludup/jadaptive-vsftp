package com.jadaptive.plugins.ssh.vsftp.tasks;

import java.io.IOException;
import java.io.InputStream;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.apache.commons.io.output.CountingOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.pf4j.Extension;
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

@Extension
public class FileTransferTaskImpl<T extends AbstractFileTransferTask> extends AbstractFileTaskImpl<T> {

	public static final String RESOURCE_KEY = "transferTask";
	
	@Autowired
	private SingletonObjectDatabase<VFSConfiguration> configuration;

	@Override
	public TaskResult doTask(T task, String executionId) {
		
		Date started = Utils.now();
		ContentHash contentHash = configuration.getObject(VFSConfiguration.class).getDefaultHash();
		
		AbstractFile target;
		
		try {
			target = resolveDestinationFile(task);
		} catch (PermissionDeniedException | IOException e) {
			return new FileLocationResult(task.getTarget().getLocation(), task.getTarget().getFilename(), e);
		}
		
		Collection<TaskResult> results = new ArrayList<>();
		
		for(String path : task.getSource().getPaths()) {
			try {
				AbstractFile from = resolveFile(task.getSource().getLocation(), path);
				AbstractFile to = target;
				if(to.isDirectory()) {
					to = to.resolveFile(from.getName());
				}
			
				feedbackService.info(executionId, AbstractFileTargetTask.BUNDLE, 
						"transferingFile.text", 
						StringUtils.abbreviateMiddle(from.getName(), "...", 30));
				
				MessageDigest digest = MessageDigest.getInstance(contentHash.getAlgorithm());
				
				long count = doTransfer(from, to, digest, task, executionId);
			
				byte[] output = digest.digest();
				String hash = VFSUtils.formatDigest(digest.getAlgorithm(), output);
				String humanHash = new HumanHashGenerator(output)
						.words(contentHash.getWords())
						.build();
				
				TransferResult result = new TransferResult(from.getName(), 
						to.getAbsolutePath(), count, started, new Date(), hash, humanHash);
				
				results.add(new FileTransferResult(result, task.getAppendContents()));
			} catch (PermissionDeniedException | IOException | NoSuchAlgorithmException e) {
				results.add(new FileLocationResult(task.getSource().getLocation(), path, e));
			}
		}
		
		return new MultipleTaskResults(results);
	}

	private long doTransfer(AbstractFile file, AbstractFile dest, MessageDigest digest, T task, String executionId) throws IOException, PermissionDeniedException {
		
		long count = 0;
		try(CountingOutputStream digestOutput = new CountingOutputStream(
				new DigestOutputStream(dest.getOutputStream(task.getAppendContents()), digest))) {
		
			try(InputStream in = file.getInputStream()) {
	
				int r;
				
				byte[] buf = new byte[task.getBlockSize()];
				
				final long progressBlock = file.length() / 100;
				long progressCount = 0;
				int percentage = 0;
				
				feedbackService.startProgress(executionId, 0, file.length());
				
				while(true) {
					r = in.read(buf);
					if(r == -1) {
						break;
					}
					if(r > 0) {
						digestOutput.write(buf, 0, r);
						progressCount+=r;
						if(progressCount >= progressBlock) {
							feedbackService.progress(executionId, ++percentage);
							progressCount = 0;
						}
						count+= r;
					}
				}

				feedbackService.endProgress(executionId);
				
			}
		}
		return count;
	}
	
	protected AbstractFile resolveDestinationFile(T task) throws PermissionDeniedException, IOException {
		return resolveFile(task.getTarget().getLocation(), task.getTarget().getFilename());
	}

	@Override
	public String getResourceKey() {
		return RESOURCE_KEY;
	}

}
