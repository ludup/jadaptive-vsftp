package com.jadaptive.plugins.ssh.vsftp.behaviours;

import com.jadaptive.api.entity.ObjectScope;
import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.ObjectDefinition;
import com.jadaptive.api.template.ObjectField;
import com.jadaptive.api.template.ObjectView;
import com.jadaptive.api.template.ObjectViewDefinition;
import com.jadaptive.api.template.ObjectViews;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderBehaviour;

@ObjectDefinition(resourceKey = SharingBehaviour.RESOURCE_KEY, bundle = VirtualFolder.RESOURCE_KEY, type = ObjectType.OBJECT, scope = ObjectScope.GLOBAL, defaultColumn = "name")
@ObjectViews({ 
	@ObjectViewDefinition(value = SharingBehaviour.SHARING_VIEW, bundle = VirtualFolder.RESOURCE_KEY)})
public class SharingBehaviour extends VirtualFolderBehaviour {

	private static final long serialVersionUID = 3900310246508116815L;

	public static final String RESOURCE_KEY = "sharingBehaviour";
	
	public static final String SHARING_VIEW = "sharingView";
	
	@Override
	public String getResourceKey() {
		return RESOURCE_KEY;
	}
	
	@Override
	public boolean supportsMultipleInstances() {
		return false;
	}
	
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
	
	

}
