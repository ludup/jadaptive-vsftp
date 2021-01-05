package com.jadaptive.plugins.ssh.vsftp;

import java.util.Objects;

import org.apache.commons.vfs2.CacheStrategy;

import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.repository.AssignableUUIDEntity;
import com.jadaptive.api.template.ExcludeView;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.FieldView;
import com.jadaptive.api.template.ObjectField;
import com.jadaptive.api.template.ObjectView;
import com.jadaptive.api.template.ObjectViewDefinition;
import com.jadaptive.api.template.ObjectViews;
import com.jadaptive.api.template.ObjectDefinition;
import com.jadaptive.utils.Utils;

@ObjectDefinition(resourceKey = VirtualFolder.RESOURCE_KEY, type = ObjectType.COLLECTION)
@ObjectViews({ 
	@ObjectViewDefinition(value = VirtualFolder.FOLDER_VIEW, bundle = VirtualFolder.RESOURCE_KEY, weight = -100),
	@ObjectViewDefinition(value = VirtualFolder.CREDS_VIEW, bundle = VirtualFolder.RESOURCE_KEY, weight = -50)})
public abstract class VirtualFolder extends AssignableUUIDEntity {

	public static final String FOLDER_VIEW = "folderView";
	public static final String CREDS_VIEW = "credsView";
	
	private static final long serialVersionUID = -3428053970013170410L;

	public static final String RESOURCE_KEY = "virtualFolder";
	
	@ObjectField(type = FieldType.TEXT, nameField = true)
	String name;
	
	@ObjectField(type = FieldType.TEXT)
	@ObjectView(VirtualFolder.FOLDER_VIEW)
	String mountPath;
	
	@ObjectField(type = FieldType.TEXT)
	@ObjectView(VirtualFolder.FOLDER_VIEW)
	String destinationUri;

	@ObjectField(type = FieldType.ENUM, defaultValue = "ON_RESOLVE")
	@ExcludeView(values = FieldView.TABLE)
	@ObjectView(VirtualFolder.FOLDER_VIEW)
	CacheStrategy cacheStrategy;
	
	@ObjectField(type = FieldType.HIDDEN, 
			searchable = true)
	String shortCode;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMountPath() {
		return mountPath;
	}

	public void setMountPath(String mountPath) {
		this.mountPath = mountPath;
	}

	public String getDestinationUri() {
		return destinationUri;
	}

	public void setDestinationUri(String destinationUri) {
		this.destinationUri = destinationUri;
	}
	
	public boolean isHome() {
		return mountPath.equals("/");
	}

	public abstract String getType();

	public CacheStrategy getCacheStrategy() {
		return cacheStrategy;
	}

	public void setCacheStrategy(CacheStrategy cacheStrategy) {
		this.cacheStrategy = cacheStrategy;
	}

//	public Boolean getHome() {
//		return home;
//	}
//
//	public void setHome(Boolean home) {
//		this.home = home;
//	}

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
