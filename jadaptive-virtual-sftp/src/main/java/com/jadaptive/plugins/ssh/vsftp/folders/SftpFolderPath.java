package com.jadaptive.plugins.ssh.vsftp.folders;

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

@ObjectDefinition(resourceKey = SftpFolderPath.RESOURCE_KEY, type = ObjectType.OBJECT)
public class SftpFolderPath extends VirtualFolderPath {

	public static final String RESOURCE_KEY = "sftpFolderPath";
	
	private static final long serialVersionUID = 3615197803358528585L;

	@ObjectField(required = true, defaultValue = "", type = FieldType.TEXT)
	@ObjectView(value = VirtualFolder.FOLDER_VIEW, bundle = VirtualFolder.RESOURCE_KEY, weight = 0)
	String hostname;
	
	@ObjectField(required = true, defaultValue = "22", type = FieldType.INTEGER)
	@ObjectView(value = VirtualFolder.FOLDER_VIEW, bundle = VirtualFolder.RESOURCE_KEY, weight = 1)
	@Validator(type = ValidationType.RANGE, value = "1-65535", bundle = VirtualFolder.RESOURCE_KEY, i18n = "port.invalid")
	Integer port;
	
	@ObjectField(type = FieldType.TEXT)
	@ObjectView(value = VirtualFolder.FOLDER_VIEW, weight = 100, bundle = VirtualFolder.RESOURCE_KEY)
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
		return hostname + ":" + port + FileUtils.checkStartsWithSlash(remotePath);
	}

	public String getRemotePath() {
		return remotePath;
	}

	public void setRemotePath(String remotePath) {
		this.remotePath = remotePath;
	}
	
	

	
}
