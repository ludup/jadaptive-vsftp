package com.jadaptive.plugins.vsftp.azure;

import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.ObjectDefinition;
import com.jadaptive.api.template.ObjectField;
import com.jadaptive.api.template.ObjectView;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderPath;
import com.sshtools.common.util.FileUtils;

@ObjectDefinition(resourceKey = AzureFolderPath.RESOURCE_KEY, type = ObjectType.OBJECT)
public class AzureFolderPath extends VirtualFolderPath {

	private static final long serialVersionUID = 1918731617426526984L;

	public static final String RESOURCE_KEY = "azureFolderPath";
	
	@ObjectField(type = FieldType.TEXT)
	@ObjectView(value = VirtualFolder.FOLDER_VIEW, weight = 200, bundle = AzureFolder.RESOURCE_KEY)
	String filePath;
	
	protected String getDestinationUri() {
		return FileUtils.checkStartsWithSlash(filePath);
	}
	
	@Override
	public String getResourceKey() {
		return RESOURCE_KEY;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
	

}
