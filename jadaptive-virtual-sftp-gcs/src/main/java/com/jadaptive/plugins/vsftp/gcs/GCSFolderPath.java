package com.jadaptive.plugins.vsftp.gcs;

import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.ObjectDefinition;
import com.jadaptive.api.template.ObjectField;
import com.jadaptive.api.template.ObjectView;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderPath;
import com.sshtools.common.util.FileUtils;

@ObjectDefinition(resourceKey = GCSFolderPath.RESOURCE_KEY, type = ObjectType.OBJECT, bundle = VirtualFolder.RESOURCE_KEY)
public class GCSFolderPath extends VirtualFolderPath {

	private static final long serialVersionUID = 1918731617426526984L;

	public static final String RESOURCE_KEY = "gcsFolderPath";
	
	@ObjectField(type = FieldType.TEXT)
	@ObjectView(value = VirtualFolder.FOLDER_VIEW, weight = 100, bundle = GCSFolder.RESOURCE_KEY)
	String bucket;
	
	@ObjectField(type = FieldType.TEXT)
	@ObjectView(value = VirtualFolder.FOLDER_VIEW, weight = 200, bundle = GCSFolder.RESOURCE_KEY)
	String filePath;
	
	public String getDestinationUri() {
		return FileUtils.checkStartsWithSlash(bucket)  + FileUtils.checkStartsWithSlash(filePath);
	}
	
	@Override
	public String getResourceKey() {
		return RESOURCE_KEY;
	}

	public String getBucket() {
		return bucket;
	}

	public void setBucket(String bucket) {
		this.bucket = bucket;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
	

}
