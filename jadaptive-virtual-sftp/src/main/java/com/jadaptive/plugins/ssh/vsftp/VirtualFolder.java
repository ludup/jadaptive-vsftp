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
	
	@ObjectField(type = FieldType.TEXT)
	String mountPath;
	
	@ObjectField(type = FieldType.TEXT)
	String destinationUri;
	
	@ObjectField(type = FieldType.TEXT)
	String type;
	
	@ObjectField(type = FieldType.ENUM)
	CacheStrategy cacheStrategy;
	
	@ObjectField(type = FieldType.OBJECT_EMBEDDED)
	VirtualFolderCredentials credentials; 
	
	@ObjectField(type = FieldType.OBJECT_EMBEDDED)
	VirtualFolderOptions options; 
	
	@ObjectField(type = FieldType.BOOL, hidden = true)
	Boolean home;
	
	@ObjectField(type = FieldType.TEXT, 
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
