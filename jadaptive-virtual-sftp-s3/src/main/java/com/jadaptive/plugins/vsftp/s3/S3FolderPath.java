package com.jadaptive.plugins.vsftp.s3;

import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.ObjectDefinition;
import com.jadaptive.api.template.ObjectField;
import com.jadaptive.api.template.ObjectView;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderPath;
import com.sshtools.common.util.FileUtils;

@ObjectDefinition(resourceKey = S3FolderPath.RESOURCE_KEY, type = ObjectType.OBJECT)
public class S3FolderPath extends VirtualFolderPath {

	private static final long serialVersionUID = 1918731617426526984L;

	public static final String RESOURCE_KEY = "s3FolderPath";
	
	@ObjectField(type = FieldType.ENUM)
	@ObjectView(value = VirtualFolder.FOLDER_VIEW, weight = 0, bundle = S3Folder.RESOURCE_KEY)
	S3Region region;
	
	@ObjectField(type = FieldType.TEXT)
	@ObjectView(value = VirtualFolder.FOLDER_VIEW, weight = 100, bundle = S3Folder.RESOURCE_KEY)
	String bucket;
	
	@ObjectField(type = FieldType.TEXT)
	@ObjectView(value = VirtualFolder.FOLDER_VIEW, weight = 200, bundle = S3Folder.RESOURCE_KEY)
	String filePath;
	
	protected String getDestinationUri() {
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

	public S3Region getRegion() {
		return region;
	}
	
	public void setRegion(S3Region region) {
		this.region = region;
	}
}
