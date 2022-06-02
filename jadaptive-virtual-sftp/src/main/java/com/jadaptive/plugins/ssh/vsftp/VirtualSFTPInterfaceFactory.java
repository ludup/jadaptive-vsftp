package com.jadaptive.plugins.ssh.vsftp;

import java.io.File;
import java.io.IOException;
import java.nio.channels.SocketChannel;

import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.jadaptive.api.app.ApplicationProperties;
import com.jadaptive.api.permissions.PermissionService;
import com.jadaptive.api.user.User;
import com.jadaptive.api.user.UserService;
import com.jadaptive.plugins.sshd.AuthorizedKeyProvider;
import com.jadaptive.plugins.sshd.SSHDService;
import com.jadaptive.plugins.sshd.SSHInterfaceFactory;
import com.sshtools.common.auth.PasswordAuthenticationProvider;
import com.sshtools.common.files.AbstractFileFactory;
import com.sshtools.common.files.vfs.VirtualFileFactory;
import com.sshtools.common.files.vfs.VirtualMountTemplate;
import com.sshtools.common.permissions.PermissionDeniedException;
import com.sshtools.common.policy.FileFactory;
import com.sshtools.common.policy.FileSystemPolicy;
import com.sshtools.common.publickey.InvalidPassphraseException;
import com.sshtools.common.publickey.SshKeyPairGenerator;
import com.sshtools.common.ssh.SshConnection;
import com.sshtools.common.ssh.SshException;
import com.sshtools.server.SshServerContext;
import com.sshtools.synergy.nio.SshEngineContext;

@Extension
public class VirtualSFTPInterfaceFactory implements SSHInterfaceFactory<SshServerContext,VirtualSFTPInterface> {

	@Autowired
	private UserService userService; 
	
	@Autowired
	private PermissionService permissionService; 
	
	@Autowired
	@Qualifier("defaultPasswordAuthenticator")
	private PasswordAuthenticationProvider passwordAuthenticator;
	
	@Autowired
	private AuthorizedKeyProvider publicKeyAuthenticator; 
	
	@Autowired
	private VirtualFileSystemMountProvider mountProvider; 
	
	@Autowired
	private SSHDService sshdService; 
	
	@Override
	public SshServerContext createContext(SshEngineContext daemonContext, SocketChannel sc, VirtualSFTPInterface intf)
			throws IOException, SshException {
		SshServerContext ctx = new VirtualSFTPContext(daemonContext.getEngine(), intf);
		
		sshdService.applyConfiguration(ctx, passwordAuthenticator);
		
		ctx.setSoftwareVersionComments("VirtualSFTP");
		
		ctx.getAuthenticationMechanismFactory().addProvider(publicKeyAuthenticator);
		
		/**
		 * TODO host keys should be configurable from the UI
		 */
		try {
			ctx.loadOrGenerateHostKey(new File(ApplicationProperties.getConfFolder(), 
					"vsftp_host_key_rsa"), SshServerContext.PUBLIC_KEY_SSHRSA, 4098);
			ctx.loadOrGenerateHostKey(new File(ApplicationProperties.getConfFolder(), 
					"vsftp_host_key_ed25519"), SshServerContext.PUBLIC_KEY_ED25519, 0);
			ctx.loadOrGenerateHostKey(new File(ApplicationProperties.getConfFolder(), 
					"vsftp_host_key_ecdsa_256"), SshKeyPairGenerator.ECDSA, 256);
			ctx.loadOrGenerateHostKey(new File(ApplicationProperties.getConfFolder(), 
					"vsftp_host_key_ecdsa_384"), SshKeyPairGenerator.ECDSA, 384);
			ctx.loadOrGenerateHostKey(new File(ApplicationProperties.getConfFolder(), 
					"vsftp_host_key_ecdsa_521"), SshKeyPairGenerator.ECDSA, 521);
		} catch (InvalidPassphraseException e) {
			throw new IOException(e.getMessage(), e);
		}
		
		ctx.getPolicy(FileSystemPolicy.class).setFileFactory(new FileFactory() {

			@Override
			public AbstractFileFactory<?> getFileFactory(SshConnection con)
					throws IOException, PermissionDeniedException {
				
				User user = userService.getUser(con.getUsername());
				permissionService.setupUserContext(user);
				
				try {
					return new VirtualFileFactory(mountProvider.getHomeMount(user), 
							mountProvider.getAdditionalMounts().toArray(new VirtualMountTemplate[0]));
					
				} catch (IOException | PermissionDeniedException e) {
					throw new IllegalStateException(e.getMessage(), e);
				} finally {
					permissionService.clearUserContext();
				}
			}
			
		});
		
		return ctx;
	}

	

}
