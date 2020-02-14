package com.jadaptive.plugins.ssh.vsftp;

import org.apache.commons.vfs2.CacheStrategy;

import com.jadaptive.api.entity.EntityType;
import com.jadaptive.api.repository.AssignableUUIDEntity;
import com.jadaptive.api.template.Entity;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.Member;

@Entity(name="Virtual Folder", resourceKey = VirtualFolder.RESOURCE_KEY, type = EntityType.COLLECTION)
public class VirtualFolder extends AssignableUUIDEntity {

	public static final String RESOURCE_KEY = "vfolder";
	@Member(name="Mount Path", description = "The path on which this folder is mounted within the virtual file system", type = FieldType.TEXT)
	String mountPath;
	
	@Member(name = "Destination URI", description = "The destination URI serving as the source for this virutal folder", type = FieldType.TEXT)
	String destinationUri;
	
	@Member(name = "Mount Type", description = "The type of mount", type = FieldType.TEXT)
	String type;
	
	@Member(name = "Cache Strategy", description = "The cache strategy to use for this mount", type = FieldType.ENUM)
	CacheStrategy cacheStrategy;
	
	@Member(name = "Credentials", description = "The credentials to use to access this folder", type = FieldType.OBJECT_EMBEDDED)
	VirtualFolderCredentials credentials; 
	
	public String getMountPath() {
		return getUuid();
	}

	public void setMountPath(String mountPath) {
		setUuid(mountPath);
	}

	public String getDestinationUri() {
		return destinationUri;
	}

	public void setDestinationUri(String destinationUri) {
		this.destinationUri = destinationUri;
	}

	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}

	public CacheStrategy getCacheStrategy() {
		return cacheStrategy;
	}

	public void setCacheStrategy(CacheStrategy cacheStrategy) {
		this.cacheStrategy = cacheStrategy;
	}
	
	public VirtualFolderCredentials getCredentials() {
		return credentials;
	}
	
	public void setCredentials(VirtualFolderCredentials credentials) {
		this.credentials = credentials;
	}
}
