package com.jadaptive.plugins.ssh.vsftp.ui;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.jsoup.nodes.Document;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;

import com.jadaptive.api.stats.UsageService;
import com.jadaptive.api.ui.AuthenticatedPage;
import com.jadaptive.api.ui.RequestPage;
import com.jadaptive.plugins.ssh.vsftp.VirtualFileService;

@Extension
@RequestPage(path = "mount-report/{uuid}")
public class MountReport extends AuthenticatedPage {

	@Autowired
	VirtualFileService fileService; 
	
	@Autowired
	UsageService usageService;
	
	String uuid;
	
	public MountReport() {
	}

	
	@Override
	protected void generateAuthenticatedContent(Document document) throws FileNotFoundException, IOException {
		super.generateAuthenticatedContent(document);
		
		
//		try {
//			VirtualFolder folder = fileService.getObjectByUUID(uuid);
//			
//			AbstractFile mount = fileService.getFile(folder.getMountPath());
//			
//			long totalSize = iterateDirectory(mount);
//			
//			long httpDownloads = usageService.sumOr(Utils.yesterday(), Utils.today(), StatsService.HTTPS_DOWNLOAD, folder.getUuid());
//			long httpUploads = usageService.sumOr(Utils.yesterday(), Utils.today(), StatsService.HTTPS_UPLOAD, folder.getUuid());
//			
//			long scpDownload = usageService.sumOr(Utils.yesterday(), Utils.today(), StatsService.SCP_DOWNLOAD, folder.getUuid());
//			long scpUpload = usageService.sumOr(Utils.yesterday(), Utils.today(), StatsService.SCP_UPLOAD, folder.getUuid());
//			
//			long sftpDownload = usageService.sumOr(Utils.yesterday(), Utils.today(), StatsService.SFTP_DOWNLOAD, folder.getUuid());
//			long sftpUpload = usageService.sumOr(Utils.yesterday(), Utils.today(), StatsService.SFTP_UPLOAD, folder.getUuid());
//			
//			long sftpDir = usageService.sumOr(Utils.yesterday(), Utils.today(), StatsService.SFTP_DIR_LISTING, folder.getUuid());
//			
//			
//		} catch (PermissionDeniedException | IOException e) {
//			
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		
	}

//	private long iterateDirectory(AbstractFile file) throws IOException, PermissionDeniedException {
//		
//		long count = 0L;
//		for(AbstractFile child : file.getChildren()) {
//			if(child.isDirectory()) {
//				return iterateDirectory(child);
//			} else {
//				count += child.length();		
//			}
//		}
//		
//		return count;
//	}
	

	@Override
	public String getUri() {
		return "mount-report";
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
}
