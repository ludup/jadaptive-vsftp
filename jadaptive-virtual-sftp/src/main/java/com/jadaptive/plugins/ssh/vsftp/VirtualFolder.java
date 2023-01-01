package com.jadaptive.plugins.ssh.vsftp;

import java.util.Collection;

import org.apache.commons.lang3.StringUtils;

import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.repository.AssignableUUIDEntity;
import com.jadaptive.api.template.FieldRenderer;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.ObjectDefinition;
import com.jadaptive.api.template.ObjectField;
import com.jadaptive.api.template.ObjectServiceBean;
import com.jadaptive.api.template.ObjectView;
import com.jadaptive.api.template.ObjectViewDefinition;
import com.jadaptive.api.template.ObjectViews;
import com.jadaptive.api.template.TableView;
import com.jadaptive.api.template.ValidationType;
import com.jadaptive.api.template.Validator;
import com.jadaptive.plugins.ssh.vsftp.behaviours.PGPBehaviour;

@ObjectDefinition(resourceKey = VirtualFolder.RESOURCE_KEY, bundle = VirtualFolder.RESOURCE_KEY, type = ObjectType.COLLECTION, defaultColumn = "name")
@ObjectServiceBean(bean = VirtualFileService.class)
@ObjectViews({ 
	@ObjectViewDefinition(value = VirtualFolder.CREDS_VIEW, bundle = VirtualFolder.RESOURCE_KEY, weight = -50),
	@ObjectViewDefinition(value = VirtualFolder.FOLDER_VIEW, bundle = VirtualFolder.RESOURCE_KEY, weight = -100),
	@ObjectViewDefinition(value = VirtualFolder.PERMISSIONS_VIEW, bundle = VirtualFolder.RESOURCE_KEY, weight = -25),
	@ObjectViewDefinition(value = VirtualFolder.BEHAVIOURS_VIEW, bundle = VirtualFolder.RESOURCE_KEY, weight = -25)})
@TableView(defaultColumns = { "name", "mountPath", "scheme"})
public abstract class VirtualFolder extends AssignableUUIDEntity {

	public static final String FOLDER_VIEW = "folderView";
	public static final String CREDS_VIEW = "credsView";
	public static final String PERMISSIONS_VIEW = "permissionsView";
	public static final String BEHAVIOURS_VIEW = "behavioursView";
	
	private static final long serialVersionUID = -3428053970013170410L;

	public static final String RESOURCE_KEY = "virtualFolder";
	
	@ObjectField(type = FieldType.TEXT, nameField = true, searchable = true, unique = true)
	@ObjectView(bundle = VirtualFolder.RESOURCE_KEY, value = "")
	String name;

	@ObjectField(type = FieldType.TEXT, hidden = true)
	@ObjectView(bundle = VirtualFolder.RESOURCE_KEY, value = "", renderer = FieldRenderer.I18N)
	String scheme = getType() + ".name";
	
	@ObjectField(type = FieldType.TEXT, searchable = true, unique = true)
	@ObjectView(bundle = VirtualFolder.RESOURCE_KEY, value = FOLDER_VIEW)
	String mountPath;
	
//	@ObjectField(type = FieldType.BOOL, defaultValue = "false")
//	@ObjectView(bundle = VirtualFolder.RESOURCE_KEY, value = SHARING_VIEW)
//	Boolean shareFiles;
//	
//	@ObjectField(type = FieldType.BOOL, defaultValue = "false")
//	@ObjectView(bundle = VirtualFolder.RESOURCE_KEY, value = SHARING_VIEW)
//	Boolean shareFolders;
	
	@ObjectField(type = FieldType.BOOL, defaultValue = "false")
	@ObjectView(bundle = VirtualFolder.RESOURCE_KEY, value = PERMISSIONS_VIEW)
	Boolean readOnly;

//	@ObjectField(type = FieldType.BOOL, defaultValue = "false")
//	@ObjectView(bundle = VirtualFolder.RESOURCE_KEY, value = ENCRYPTION_VIEW)
//	Boolean encrypt;
//	
//	@ObjectField(type = FieldType.BOOL, defaultValue = "false")
//	@ObjectView(bundle = VirtualFolder.RESOURCE_KEY, value = ENCRYPTION_VIEW)
//	Boolean armour;
//	
//	@ObjectField(type = FieldType.BOOL, defaultValue = "false")
//	@ObjectView(bundle = VirtualFolder.RESOURCE_KEY, value = ENCRYPTION_VIEW)
//	Boolean compress;
//	
//	@ObjectField(type = FieldType.BOOL, defaultValue = "false")
//	@ObjectView(bundle = VirtualFolder.RESOURCE_KEY, value = ENCRYPTION_VIEW)
//	Boolean integrityCheck;
	
//	@ObjectField(type = FieldType.TEXT_AREA)
//	@ObjectView(bundle = VirtualFolder.RESOURCE_KEY, value = KEYS_VIEW)
//	String privateKey;
//	
//	@ObjectField(type = FieldType.PASSWORD, manualEncryption = true)
//	@ObjectView(bundle = VirtualFolder.RESOURCE_KEY, value = KEYS_VIEW)
//	String passphrase;
//	
//	@ObjectField(type = FieldType.TEXT_AREA)
//	@ObjectView(bundle = VirtualFolder.RESOURCE_KEY, value = KEYS_VIEW)
//	String publicKey;
	
//	@ObjectField(type = FieldType.ENUM, defaultValue = "ON_RESOLVE")
//	@ExcludeView(values = FieldView.TABLE)
//	@ObjectView(value = VirtualFolder.ADVANCED_VIEW, bundle = VirtualFolder.RESOURCE_KEY, weight = 9999)
//	CacheStrategy cacheStrategy;
	
	public abstract VirtualFolderPath getPath();
	
	public abstract void setPath(VirtualFolderPath path);
	
	@ObjectField(type = FieldType.OBJECT_EMBEDDED)
	@ObjectView(VirtualFolder.BEHAVIOURS_VIEW)
	@Validator(type = ValidationType.RESOURCE_KEY, value = VirtualFolderExtension.RESOURCE_KEY)
	Collection<VirtualFolderExtension> extensions;
	
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

	

//	public Boolean getShareFiles() {
//		return shareFiles;
//	}
//
//	public void setShareFiles(Boolean shareFiles) {
//		this.shareFiles = shareFiles;
//	}
//
//	public Boolean getShareFolders() {
//		return shareFolders;
//	}
//
//	public void setShareFolders(Boolean shareFolders) {
//		this.shareFolders = shareFolders;
//	}

	public Collection<VirtualFolderExtension> getExtensions() {
		return extensions;
	}

	public void setExtensions(Collection<VirtualFolderExtension> extensions) {
		this.extensions = extensions;
	}

	public String getScheme() {
		return StringUtils.defaultString(scheme, getType() + ".name");
	}

	public void setScheme(String scheme) {
		this.scheme = getType() + ".name";
	}

	public String getEventGroup() {
		return RESOURCE_KEY;
	}

	public boolean getEncrypted() {
		for(VirtualFolderExtension b : extensions) {
			if(b instanceof PGPBehaviour) {
				return ((PGPBehaviour)b).getEncrypt().booleanValue();
			}
		}
		return false;
	}
}
