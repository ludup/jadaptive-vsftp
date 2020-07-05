package com.jadaptive.plugins.ssh.vsftp;

import java.util.Objects;

import org.apache.commons.vfs2.CacheStrategy;

import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.repository.AssignableUUIDEntity;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.ObjectField;
import com.jadaptive.api.template.ObjectDefinition;
import com.jadaptive.utils.Utils;

@ObjectDefinition(resourceKey = VirtualFolder.RESOURCE_KEY, type = ObjectType.COLLECTION)
public class VirtualFolder extends AssignableUUIDEntity {

	private static final long serialVersionUID = -3428053970013170410L;

	public static final String RESOURCE_KEY = "vfolder";
	
	@ObjectField(name="Mount Path", description = "The path on which this folder is mounted within the virtual file system", type = FieldType.TEXT)
	String mountPath;
	
	@ObjectField(name = "Destination URI", description = "The destination URI serving as the source for this virutal folder", type = FieldType.TEXT)
	String destinationUri;
	
	@ObjectField(name = "Mount Type", description = "The type of mount", type = FieldType.TEXT)
	String type;
	
	@ObjectField(name = "Cache Strategy", description = "The cache strategy to use for this mount", type = FieldType.ENUM)
	CacheStrategy cacheStrategy;
	
	@ObjectField(name = "Credentials", description = "The credentials to use to access this folder", type = FieldType.OBJECT_EMBEDDED)
	VirtualFolderCredentials credentials; 
	
	@ObjectField(name = "Options", description = "The options available for this folder", type = FieldType.OBJECT_EMBEDDED)
	VirtualFolderOptions options; 
	
	@ObjectField(name = "Home", description = "This is the home mount for this tenant", type = FieldType.BOOL, hidden = true)
	Boolean home;
	
	@ObjectField(name = "Short Code", 
			description = "A unique code for anonymously identifying this folder",
			type = FieldType.TEXT, 
			hidden = true,
			searchable = true)
	String shortCode;
	
	public String getMountPath() {
		return getUuid();
	}

	public void setMountPath(String mountPath) {
		setUuid(mountPath);
		this.mountPath = mountPath;
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

	public VirtualFolderOptions getOptions() {
		return options;
	}

	public void setOptions(VirtualFolderOptions options) {
		this.options = options;
	}

	public Boolean getHome() {
		return home;
	}

	public void setHome(Boolean home) {
		this.home = home;
	}

	@Override
	public String getResourceKey() {
		return RESOURCE_KEY;
	}

	public String getShortCode() {
		if(Objects.isNull(shortCode)) {
			shortCode = Utils.generateRandomAlphaNumericString(8);
		}
		return shortCode;
	}

	public void setShortCode(String shortCode) {
		this.shortCode = shortCode;
	}
	
	
}
