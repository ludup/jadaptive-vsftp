package com.jadaptive.plugins.ssh.vsftp.stats;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.jadaptive.api.app.StartupAware;
import com.jadaptive.api.permissions.AuthenticatedService;
import com.jadaptive.api.repository.UUIDEntity;
import com.jadaptive.api.stats.UsageService;
import com.jadaptive.api.tenant.Tenant;
import com.jadaptive.api.tenant.TenantService;
import com.jadaptive.api.user.User;
import com.jadaptive.plugins.ssh.vsftp.VirtualFileService;
import com.jadaptive.plugins.sshd.SSHDService;
import com.jadaptive.utils.Utils;
import com.sshtools.common.events.Event;
import com.sshtools.common.events.EventCodes;
import com.sshtools.common.events.EventListener;
import com.sshtools.common.events.EventServiceImplementation;
import com.sshtools.common.files.AbstractFile;
import com.sshtools.common.ssh.SshConnection;

@Service
public class StatsServiceImpl extends AuthenticatedService implements StatsService, StartupAware {

	@Autowired
	private UsageService usageService;

	@Autowired
	private TenantService tenantService;
	
	@Autowired
	private VirtualFileService fileService; 
	
	private UUIDEntity getVirtualFolder(Event evt) {
		AbstractFile file = (AbstractFile) evt.getAttribute(EventCodes.ATTRIBUTE_ABSTRACT_FILE);
		return fileService.getParentMount(file);
	}
	
	@Override
	public synchronized void registerDataTransfer(Throughput throughput, long count, String... keys) {

		tenantService.executeAs(getCurrentTenant(), ()->{
			usageService.log(count, StringUtils.addStringToArray(keys, throughput.name()));
		});
	}
	
	@Override
	public double getMonthlyThroughput(Throughput throughput) {
		return usageService.sum(throughput.name(), Utils.getMonthStart(Utils.now()), Utils.getMonthEnd(Utils.now()));
	}
	
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
					
					tenantService.setCurrentTenant(tenant);
	
					try {
						switch(evt.getId()) {
						
						case EventCodes.EVENT_SFTP_FILE_DELETED:
						{
							tenantService.executeAs(tenant, ()-> {
								usageService.incrementDailyValue(SFTP_DELETE);
							});
							break;
							
						}
						case EventCodes.EVENT_SFTP_FILE_RENAMED:
						{
							tenantService.executeAs(tenant, ()-> {
								usageService.incrementDailyValue(SFTP_RENAME);
							});
							break;
							
						}
						case EventCodes.EVENT_SFTP_FILE_TOUCHED:
						{
							tenantService.executeAs(tenant, ()-> {
								usageService.incrementDailyValue(SFTP_TOUCHED);
							});
							break;
							
						}
						case EventCodes.EVENT_SFTP_DIRECTORY_CREATED:
						{
							tenantService.executeAs(tenant, ()-> {
								usageService.incrementDailyValue(SFTP_DIR_CREATED);
							});
							break;
						}
						case EventCodes.EVENT_SFTP_DIRECTORY_DELETED:
						{
							tenantService.executeAs(tenant, ()-> {
								usageService.incrementDailyValue(SFTP_DIR_DELETED);
							});
							break;
						}
						case EventCodes.EVENT_SFTP_SET_ATTRIBUTES:
						{
							tenantService.executeAs(tenant, ()-> {
								usageService.incrementDailyValue(SFTP_SETSTAT);
							});
							break;
							
						}
						case EventCodes.EVENT_SFTP_SYMLINK_CREATED:
						{
							tenantService.executeAs(tenant, ()-> {
								usageService.incrementDailyValue(SFTP_SYMLINKED);
							});
							break;
							
						}
						case EventCodes.EVENT_SFTP_DIRECTORY_OPENED:
						{
							tenantService.executeAs(tenant, ()-> {
								usageService.incrementDailyValue(SFTP_DIR_OPEN);
							});
							break;
							
						}
						case EventCodes.EVENT_DISCONNECTED:
						{
							registerDataTransfer(Throughput.INGRESS, con.getTotalBytesIn(), SSHD_IN, user.getUuid());
							registerDataTransfer(Throughput.EGRESS,con.getTotalBytesOut(), SSHD_OUT, user.getUuid());
							break;
						}
						case EventCodes.EVENT_SFTP_DIR:
						{
							tenantService.executeAs(tenant, ()-> {
								usageService.incrementDailyValue(SFTP_DIR_CLOSED);
							});
							
							usageService.log((Long) evt.getAttribute(EventCodes.ATTRIBUTE_BYTES_TRANSFERED), 
										SFTP_DIR_LISTING, 
										user.getUuid(),
										getVirtualFolder(evt).getUuid());
							break;
						}
						case EventCodes.EVENT_SCP_DOWNLOAD_COMPLETE:
						{
							usageService.log((Long) evt.getAttribute(EventCodes.ATTRIBUTE_BYTES_TRANSFERED),
										SCP_DOWNLOAD, 
										user.getUuid(),
										getVirtualFolder(evt).getUuid());
							break;
						}
						case EventCodes.EVENT_SFTP_FILE_DOWNLOAD_COMPLETE:
						{
							usageService.log((Long) evt.getAttribute(EventCodes.ATTRIBUTE_BYTES_TRANSFERED),
										SFTP_DOWNLOAD,
										user.getUuid(),
										getVirtualFolder(evt).getUuid());
							break;		
						}
						case EventCodes.EVENT_SCP_UPLOAD_COMPLETE:
						{
							usageService.log((Long) evt.getAttribute(EventCodes.ATTRIBUTE_BYTES_TRANSFERED), 
										SCP_UPLOAD, 
										user.getUuid(),
										getVirtualFolder(evt).getUuid());
							break;		
						}
						case EventCodes.EVENT_SFTP_FILE_UPLOAD_COMPLETE:
						{
							usageService.log((Long) evt.getAttribute(EventCodes.ATTRIBUTE_BYTES_TRANSFERED), 
										SFTP_UPLOAD, 
										user.getUuid(),
										getVirtualFolder(evt).getUuid());
							break;		
						}
						case EventCodes.EVENT_SFTP_FILE_ACCESS:
						{
							usageService.log((Long) evt.getAttribute(EventCodes.ATTRIBUTE_BYTES_READ), 
										SFTP_DOWNLOAD, 
										user.getUuid(),
										getVirtualFolder(evt).getUuid());
							usageService.log((Long) evt.getAttribute(EventCodes.ATTRIBUTE_BYTES_WRITTEN), 
										SFTP_UPLOAD, 
										user.getUuid(),
										getVirtualFolder(evt).getUuid());
							
							break;		
						}
						default:
							break;
						}
					} finally {
						tenantService.clearCurrentTenant();
					}
				}
			}
		});
		
	}
}
