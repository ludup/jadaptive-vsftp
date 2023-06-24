package com.jadaptive.plugins.ssh.vsftp;

import org.apache.commons.lang3.StringUtils;

import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.events.GenerateEventTemplates;
import com.jadaptive.api.repository.AssignableUUIDEntity;
import com.jadaptive.api.repository.NamedDocument;
import com.jadaptive.api.template.FieldRenderer;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.ObjectDefinition;
import com.jadaptive.api.template.ObjectField;
import com.jadaptive.api.template.ObjectServiceBean;
import com.jadaptive.api.template.ObjectView;
import com.jadaptive.api.template.ObjectViewDefinition;
import com.jadaptive.api.template.TableView;
import com.jadaptive.api.user.User;

@ObjectDefinition(resourceKey = VirtualFolder.RESOURCE_KEY, type = ObjectType.COLLECTION, defaultColumn = "name")
@ObjectServiceBean(bean = VirtualFileService.class)
@ObjectViewDefinition(value = VirtualFolder.CREDS_VIEW, bundle = VirtualFolder.RESOURCE_KEY, weight = -50)
@ObjectViewDefinition(value = VirtualFolder.FOLDER_VIEW, bundle = VirtualFolder.RESOURCE_KEY, weight = -100)
@TableView(defaultColumns = { "name", "mountPath", "scheme"})
@GenerateEventTemplates(VirtualFolder.RESOURCE_KEY)
public abstract class VirtualFolder extends AssignableUUIDEntity implements NamedDocument {

	public static final String FOLDER_VIEW = "folderView";
	public static final String CREDS_VIEW = "credsView";
	public static final String PERMISSIONS_VIEW = "permissionsView";
	
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
		for(User user : getUsers()) {
			if(user.getUuid().equals(AnonymousUserDatabaseImpl.ANONYMOUS_USER_UUID)) {
				return true;
			}
		}
		return false;
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
}
