package com.jadaptive.plugins.ssh.vsftp;

import org.apache.commons.vfs2.CacheStrategy;

import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.events.AuditedObject;
import com.jadaptive.api.repository.AssignableUUIDEntity;
import com.jadaptive.api.template.ExcludeView;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.FieldView;
import com.jadaptive.api.template.ObjectDefinition;
import com.jadaptive.api.template.ObjectField;
import com.jadaptive.api.template.ObjectServiceBean;
import com.jadaptive.api.template.ObjectView;
import com.jadaptive.api.template.ObjectViewDefinition;
import com.jadaptive.api.template.ObjectViews;
import com.jadaptive.api.template.TableView;

@ObjectDefinition(resourceKey = VirtualFolder.RESOURCE_KEY, type = ObjectType.COLLECTION, defaultColumn = "name")
@ObjectServiceBean(bean = VirtualFileService.class)
@ObjectViews({ 
	@ObjectViewDefinition(value = VirtualFolder.CREDS_VIEW, bundle = VirtualFolder.RESOURCE_KEY, weight = -50),
	@ObjectViewDefinition(value = VirtualFolder.FOLDER_VIEW, bundle = VirtualFolder.RESOURCE_KEY, weight = -100),
	@ObjectViewDefinition(value = VirtualFolder.PERMISSIONS_VIEW, bundle = VirtualFolder.RESOURCE_KEY, weight = -25),
	@ObjectViewDefinition(value = VirtualFolder.ADVANCED_VIEW, bundle = VirtualFolder.RESOURCE_KEY, weight = -00)})
@TableView(defaultColumns = { "name", "mountPath"})
@AuditedObject
public abstract class VirtualFolder extends AssignableUUIDEntity {

	public static final String FOLDER_VIEW = "folderView";
	public static final String CREDS_VIEW = "credsView";
	public static final String PERMISSIONS_VIEW = "permissionsView";
	public static final String ADVANCED_VIEW = "advancedView";
	
	private static final long serialVersionUID = -3428053970013170410L;

	public static final String RESOURCE_KEY = "virtualFolder";
	
	@ObjectField(type = FieldType.TEXT, nameField = true, searchable = true)
	@ObjectView(bundle = VirtualFolder.RESOURCE_KEY, value = "")
	String name;
	
	@ObjectField(type = FieldType.TEXT, searchable = true)
	@ObjectView(bundle = VirtualFolder.RESOURCE_KEY, value = "")
	String mountPath;
	
	@ObjectField(type = FieldType.BOOL, defaultValue = "false")
	@ObjectView(bundle = VirtualFolder.RESOURCE_KEY, value = PERMISSIONS_VIEW)
	Boolean shareFiles;
	
	@ObjectField(type = FieldType.BOOL, defaultValue = "false")
	@ObjectView(bundle = VirtualFolder.RESOURCE_KEY, value = PERMISSIONS_VIEW)
	Boolean shareFolders;
	
	@ObjectField(type = FieldType.BOOL, defaultValue = "false")
	@ObjectView(bundle = VirtualFolder.RESOURCE_KEY, value = PERMISSIONS_VIEW)
	Boolean readOnly;
	
	@ObjectField(type = FieldType.ENUM, defaultValue = "ON_RESOLVE")
	@ExcludeView(values = FieldView.TABLE)
	@ObjectView(value = VirtualFolder.ADVANCED_VIEW, bundle = VirtualFolder.RESOURCE_KEY, weight = 9999)
	CacheStrategy cacheStrategy;

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

	public boolean isPublicFolder() {
		return getUsers().contains(AnonymousUserDatabaseImpl.ANONYMOUS_USER_UUID);
	}

	public Boolean getReadOnly() {
		return readOnly;
	}

	public void setReadOnly(Boolean readOnly) {
		this.readOnly = readOnly;
	}

	public CacheStrategy getCacheStrategy() {
		return cacheStrategy;
	}

	public void setCacheStrategy(CacheStrategy cacheStrategy) {
		this.cacheStrategy = cacheStrategy;
	}
	
	public Boolean getShareFiles() {
		return shareFiles;
	}

	public void setShareFiles(Boolean shareFiles) {
		this.shareFiles = shareFiles;
	}

	public Boolean getShareFolders() {
		return shareFolders;
	}

	public void setShareFolders(Boolean shareFolders) {
		this.shareFolders = shareFolders;
	}

	public String getEventGroup() {
		return RESOURCE_KEY;
	}
}
