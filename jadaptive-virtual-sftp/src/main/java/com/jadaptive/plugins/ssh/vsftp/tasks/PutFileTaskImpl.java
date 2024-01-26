package com.jadaptive.plugins.ssh.vsftp.tasks;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.pf4j.Extension;

import com.jadaptive.api.tasks.TaskResult;
import com.jadaptive.plugins.ssh.vsftp.events.TransferResult;
import com.jadaptive.utils.Utils;
import com.sshtools.client.SshClient;
import com.sshtools.client.SshClient.SshClientBuilder;
import com.sshtools.client.sftp.SftpClient;
import com.sshtools.client.sftp.SftpClient.SftpClientBuilder;
import com.sshtools.client.sftp.TransferCancelledException;
import com.sshtools.common.files.AbstractFile;
import com.sshtools.common.permissions.PermissionDeniedException;
import com.sshtools.common.publickey.InvalidPassphraseException;
import com.sshtools.common.publickey.SshKeyUtils;
import com.sshtools.common.sftp.SftpStatusException;
import com.sshtools.common.ssh.SshException;
import com.sshtools.common.ssh.components.SshKeyPair;
import com.sshtools.common.util.FileUtils;

@Extension
public class PutFileTaskImpl extends AbstractFileTaskImpl<PutFileTask> {

	@Override
	public String getResourceKey() {
		return PutFileTask.RESOURCE_KEY;
	}
	
	public String getIcon() {
		return "fa-upload";
	}

	@Override
	public TaskResult doTask(PutFileTask task, String executionId) {
		
		Date started = Utils.now();
		Long length = 0L;
		
		Collection<TaskResult> results = new ArrayList<>();
		
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
				
				try(SftpClient sftp = SftpClientBuilder.create()
						.withClient(ssh)
						.withFileFactory(getFileFactory(task.getSource().getLocation()))
						.build()) {
					
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

						feedbackService.info(executionId, AbstractFileTargetTask.BUNDLE, 
								"puttingFile.text", 
								StringUtils.abbreviateMiddle(from.getName(), "...", 30));
						
						try(InputStream in = from.getInputStream()) {
							sftp.put(in, task.getRemoteDirectory(), new FeedbackFileTransferProgress(executionId));
							
							results.add(new FileTransferResult(new TransferResult(
									FileUtils.getFilename(absolutePath),
									FileUtils.getParentPath(absolutePath),
									length,
									started,
									Utils.now()), false));
						} catch (IOException | SshException | PermissionDeniedException | SftpStatusException | TransferCancelledException e) {
							results.add(new FileTransferResult(new TransferResult(
									FileUtils.getFilename(absolutePath),
									FileUtils.getParentPath(absolutePath),
									length,
									started,
									Utils.now()), false, e));
						}
							
					}
					
				} catch (IOException | SshException | PermissionDeniedException e) {
					results.add(new FileConnectionErrorResult(task.getConnection().getHostname(), e));
				}
			} catch (IOException | SshException e) {
				results.add(new FileConnectionErrorResult(task.getConnection().getHostname(), e));
			}
		} catch(IOException | InvalidPassphraseException e) {
			results.add(new FileConnectionErrorResult(task.getConnection().getHostname(), e));
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
