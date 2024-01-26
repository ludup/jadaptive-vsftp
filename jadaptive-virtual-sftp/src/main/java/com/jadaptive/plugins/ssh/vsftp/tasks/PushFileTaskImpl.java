package com.jadaptive.plugins.ssh.vsftp.tasks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.pf4j.Extension;

import com.jadaptive.api.app.ApplicationServiceImpl;
import com.jadaptive.api.tasks.TaskResult;
import com.jadaptive.plugins.ssh.vsftp.events.TransferResult;
import com.jadaptive.utils.Utils;
import com.sshtools.client.SshClient;
import com.sshtools.client.SshClient.SshClientBuilder;
import com.sshtools.client.tasks.PushTask.PushTaskBuilder;
import com.sshtools.common.files.AbstractFile;
import com.sshtools.common.permissions.PermissionDeniedException;
import com.sshtools.common.publickey.InvalidPassphraseException;
import com.sshtools.common.publickey.SshKeyUtils;
import com.sshtools.common.ssh.SshException;
import com.sshtools.common.ssh.components.SshKeyPair;
import com.sshtools.common.util.FileUtils;

@Extension
public class PushFileTaskImpl extends AbstractFileTaskImpl<PushFileTask> {

	@Override
	public String getResourceKey() {
		return PushFileTask.RESOURCE_KEY;
	}
	
	public String getIcon() {
		return "fa-bars-progress";
	}

	@Override
	public TaskResult doTask(PushFileTask task, String executionId) {
		
		Date started = Utils.now();
		Long length = 0L;
		
		Collection<TaskResult> results = new ArrayList<>();
		
		for(String path : task.getSource().getPaths()) {
			
			AbstractFile from = null;
			String absolutePath = null;
			try {
				from = resolveFile(task.getSource().getLocation(), path);
				length = from.length();
				absolutePath = from.getAbsolutePath();
			
			} catch(PermissionDeniedException | IOException e) {
				return new FileLocationResult(task.getSource().getLocation(), path, e);
			}
			
			try {	
				
				feedbackService.info(executionId, AbstractFileTargetTask.BUNDLE,
						"creatingSshClient.text",
						task.getConnection().getHostname());
				
				SshClientBuilder builder = SshClientBuilder.create()
						.withHostname(task.getConnection().getHostname())
						.withPort(task.getConnection().getPort())
						.withUsername(task.getConnection().getUsername());
				
				if(StringUtils.isNotBlank(task.getConnection().getPassword())) {
					builder.withPassword(task.getConnection().getPassword());
				}
				
				SshKeyPair pair = buildIdentity(task.getConnection());
				if(Objects.nonNull(pair)) {
					builder.withIdentities(pair);
				}
				
				try(SshClient ssh = builder.build()) {
				
					feedbackService.info(executionId, AbstractFileTargetTask.BUNDLE, 
							"pushingFile.text", 
							StringUtils.abbreviateMiddle(from.getName(), "...", 30));
					
					ssh.runTask(PushTaskBuilder.create()
							.withClient(ssh)
							.withAbstractFiles(from)
							.withRemoteFolder(task.getRemoteDirectory())
							.withChunks(task.getChunks())
							.withProgress(ApplicationServiceImpl.getInstance().autowire( 
									new FeedbackFileTransferProgress(executionId)))
							.build());
				} 
				
				results.add(new FileTransferResult(new TransferResult(
						FileUtils.getFilename(absolutePath),
						FileUtils.getParentPath(absolutePath),
						length,
						started,
						Utils.now()), false));
				
			} catch (IOException | SshException | InvalidPassphraseException e) {
				results.add(new FileTransferResult(new TransferResult(
						FileUtils.getFilename(absolutePath),
						FileUtils.getParentPath(absolutePath),
						length,
						started,
						Utils.now()), false, e));
			}
		}
		
		return new MultipleTaskResults(results);
	}

	private SshKeyPair buildIdentity(SshConnectionProperties connection) throws IOException, InvalidPassphraseException {
		
		if(StringUtils.isNotBlank(connection.getPrivateKey())) {
			return SshKeyUtils.getPrivateKey(connection.getPrivateKey(), connection.getPassphrase());
		}
		return null;
	}

}
