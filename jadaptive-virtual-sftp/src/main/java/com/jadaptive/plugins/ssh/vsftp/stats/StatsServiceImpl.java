package com.jadaptive.plugins.ssh.vsftp.stats;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jadaptive.api.app.StartupAware;
import com.jadaptive.api.stats.UsageService;
import com.jadaptive.api.tenant.Tenant;
import com.jadaptive.api.tenant.TenantService;
import com.jadaptive.api.user.User;
import com.jadaptive.plugins.sshd.SSHDService;
import com.sshtools.common.events.Event;
import com.sshtools.common.events.EventCodes;
import com.sshtools.common.events.EventListener;
import com.sshtools.common.events.EventServiceImplementation;
import com.sshtools.common.ssh.SshConnection;

@Service
public class StatsServiceImpl implements StatsService, StartupAware {

	@Autowired
	private UsageService usageService;

	@Autowired
	private TenantService tenantService;
	
	@Override
	public void onApplicationStartup() {
		
		EventServiceImplementation.getInstance().addListener(new EventListener() {

			@Override
			public void processEvent(Event evt) {
				
				SshConnection con = (SshConnection) evt.getAttribute(EventCodes.ATTRIBUTE_CONNECTION);
				if(Objects.nonNull(con)) {
				
					Tenant tenant = (Tenant) con.getProperty(SSHDService.TENANT);
					
					if(Objects.isNull(tenant)) {
						return;
					}
	
					User user = (User) con.getProperty(SSHDService.USER);
	
					if(Objects.isNull(user)) {
						return;
					}
	
					switch(evt.getId()) {
					case EventCodes.EVENT_DISCONNECTED:
					{
						tenantService.executeAs(tenant, ()-> {
							usageService.log(con.getTotalBytesIn(), "sshd_in", user.getUuid());
							usageService.log(con.getTotalBytesOut(), "sshd_out", user.getUuid());
						});
						break;
					}
					case EventCodes.EVENT_SCP_DOWNLOAD_COMPLETE:
					{
						tenantService.executeAs(tenant, ()-> {
							usageService.log((Long) evt.getAttribute(EventCodes.ATTRIBUTE_BYTES_TRANSFERED), "scp_download", user.getUuid());
						});
						break;
					}
					case EventCodes.EVENT_SFTP_FILE_DOWNLOAD_COMPLETE:
					{
						tenantService.executeAs(tenant, ()-> {
							usageService.log((Long) evt.getAttribute(EventCodes.ATTRIBUTE_BYTES_TRANSFERED), "sftp_download", user.getUuid());
						});
						break;		
					}
					case EventCodes.EVENT_SCP_UPLOAD_COMPLETE:
					{
						tenantService.executeAs(tenant, ()-> {
							usageService.log((Long) evt.getAttribute(EventCodes.ATTRIBUTE_BYTES_TRANSFERED), "scp_upload", user.getUuid());
						});
						break;		
					}
					case EventCodes.EVENT_SFTP_FILE_UPLOAD_COMPLETE:
					{
						tenantService.executeAs(tenant, ()-> {
							usageService.log((Long) evt.getAttribute(EventCodes.ATTRIBUTE_BYTES_TRANSFERED), "sftp_upload", user.getUuid());
						});
						break;		
					}
					case EventCodes.EVENT_SFTP_FILE_ACCESS:
					{
						tenantService.executeAs(tenant, ()-> {
							usageService.log((Long) evt.getAttribute(EventCodes.ATTRIBUTE_BYTES_READ), "sftp_rnd_in", user.getUuid());
							usageService.log((Long) evt.getAttribute(EventCodes.ATTRIBUTE_BYTES_READ), "sftp_rnd_out", user.getUuid());
						});
						break;		
					}
					default:
						break;
					}
				}
			}
			
		});
		
	}

}
