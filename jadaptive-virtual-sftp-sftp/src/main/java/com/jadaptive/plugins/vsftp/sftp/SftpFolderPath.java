package com.jadaptive.plugins.vsftp.sftp;

import org.apache.commons.lang3.StringUtils;

import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.ObjectDefinition;
import com.jadaptive.api.template.ObjectField;
import com.jadaptive.api.template.ObjectView;
import com.jadaptive.api.template.ValidationType;
import com.jadaptive.api.template.Validator;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderPath;
import com.sshtools.common.util.FileUtils;

@ObjectDefinition(resourceKey = SftpFolderPath.RESOURCE_KEY, type = ObjectType.OBJECT, bundle = SftpFolder.RESOURCE_KEY)
public class SftpFolderPath extends VirtualFolderPath {

	public static final String RESOURCE_KEY = "sftpFolderPath";
	
	private static final long serialVersionUID = 3615197803358528585L;

	@ObjectField(defaultValue = "", type = FieldType.TEXT)
	@ObjectView(value = VirtualFolder.FOLDER_VIEW, weight = 0)
	@Validator(type = ValidationType.REQUIRED)
	String hostname;
	
	@ObjectField(defaultValue = "22", type = FieldType.INTEGER)
	@ObjectView(value = VirtualFolder.FOLDER_VIEW, weight = 1)
	@Validator(type = ValidationType.RANGE, value = "1-65535", bundle = SftpFolder.RESOURCE_KEY, i18n = "port.invalid")
	Integer port;
	
	@ObjectField(type = FieldType.TEXT)
	@ObjectView(value = VirtualFolder.FOLDER_VIEW, weight = 100)
	String remotePath;
	
	@Override
	public String getResourceKey() {
		return RESOURCE_KEY;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	@Override
	public String getDestinationUri() {
		if(StringUtils.isBlank(remotePath)) {
			return hostname + ":" + port;
		} else { 
			return hostname + ":" + port + FileUtils.checkStartsWithSlash(remotePath);
		}
	}
	
	public String generatePath() {
		return getRemotePath();
	}	

	public String getRemotePath() {
		return remotePath;
	}

	public void setRemotePath(String remotePath) {
		this.remotePath = remotePath;
	}
	
	

	
}
