package com.jadaptive.plugins.ssh.vsftp.extensions.sharing;

import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.repository.AbstractUUIDEntity;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.ObjectDefinition;
import com.jadaptive.api.template.ObjectExtension;
import com.jadaptive.api.template.ObjectField;
import com.jadaptive.api.template.ObjectView;
import com.jadaptive.api.template.ObjectViewDefinition;
import com.jadaptive.api.template.ObjectViews;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;

@ObjectViews({ 
	@ObjectViewDefinition(value = Sharing.SHARING_VIEW, bundle = VirtualFolder.RESOURCE_KEY, weight = -00)
})
@ObjectExtension(resourceKey = Sharing.RESOURCE_KEY,  bundle = VirtualFolder.RESOURCE_KEY, extend = VirtualFolder.RESOURCE_KEY, extendingInterface = SharingExtension.class)
@ObjectDefinition(resourceKey = Sharing.RESOURCE_KEY, type = ObjectType.OBJECT, bundle = VirtualFolder.RESOURCE_KEY)
public class Sharing extends AbstractUUIDEntity {

	private static final long serialVersionUID = -7696905255893709882L;
	
	public static final String RESOURCE_KEY = "sharing";
	public static final String SHARING_VIEW = "sharingView";
	
	@ObjectField(type = FieldType.BOOL, defaultValue = "false")
	@ObjectView(bundle = VirtualFolder.RESOURCE_KEY, value = SHARING_VIEW)
	Boolean shareFiles;
	
	@ObjectField(type = FieldType.BOOL, defaultValue = "false")
	@ObjectView(bundle = VirtualFolder.RESOURCE_KEY, value = SHARING_VIEW)
	Boolean shareFolders;

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

	@Override
	public String getResourceKey() {
		return RESOURCE_KEY;
	}
}
