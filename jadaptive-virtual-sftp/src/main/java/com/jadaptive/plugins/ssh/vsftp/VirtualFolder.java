package com.jadaptive.plugins.ssh.vsftp;

import java.util.Objects;

import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.repository.AssignableUUIDEntity;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.ObjectDefinition;
import com.jadaptive.api.template.ObjectField;
import com.jadaptive.api.template.ObjectView;
import com.jadaptive.api.template.ObjectViewDefinition;
import com.jadaptive.api.template.ObjectViews;
import com.jadaptive.utils.Utils;

@ObjectDefinition(resourceKey = VirtualFolder.RESOURCE_KEY, type = ObjectType.COLLECTION)
@ObjectViews({ 
	@ObjectViewDefinition(value = VirtualFolder.CREDS_VIEW, bundle = VirtualFolder.RESOURCE_KEY, weight = -50),
	@ObjectViewDefinition(value = VirtualFolder.FOLDER_VIEW, bundle = VirtualFolder.RESOURCE_KEY, weight = -100)})
public abstract class VirtualFolder extends AssignableUUIDEntity {

	public static final String FOLDER_VIEW = "folderView";
	public static final String CREDS_VIEW = "credsView";
	
	private static final long serialVersionUID = -3428053970013170410L;

	public static final String RESOURCE_KEY = "virtualFolder";
	
	@ObjectField(type = FieldType.TEXT, nameField = true)
	@ObjectView(bundle = VirtualFolder.RESOURCE_KEY, value = "")
	String name;
	
	@ObjectField(type = FieldType.TEXT)
	@ObjectView(bundle = VirtualFolder.RESOURCE_KEY, value = "")
	String mountPath;
	
	@ObjectField(type = FieldType.HIDDEN, 
			searchable = true)
	String shortCode;
	
	public abstract VirtualFolderPath getPath();
	
	public abstract void setPath(VirtualFolderPath path);
	
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
	
	public boolean isHome() {
		return mountPath.equals("/");
	}

	public abstract String getType();

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
