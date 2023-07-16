package com.jadaptive.plugins.ssh.vsftp;

import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.repository.AbstractUUIDEntity;
import com.jadaptive.api.template.ExcludeView;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.FieldView;
import com.jadaptive.api.template.ObjectDefinition;
import com.jadaptive.api.template.ObjectField;
import com.jadaptive.api.template.ObjectView;
import com.sshtools.common.util.FileUtils;

@ObjectDefinition(resourceKey = "virtualFolderPath", type = ObjectType.OBJECT)
public abstract class VirtualFolderPath extends AbstractUUIDEntity {

	private static final long serialVersionUID = 6877837628940930537L;

	@ObjectField(type = FieldType.BOOL, defaultValue = "false")
	@ExcludeView(values = FieldView.TABLE)
	@ObjectView(value = VirtualFolder.FOLDER_VIEW, bundle = VirtualFolder.RESOURCE_KEY, weight = 9998)
	Boolean appendUsername = Boolean.FALSE;
	
	@ObjectField(type = FieldType.BOOL, defaultValue = "false")
	@ObjectView(bundle = VirtualFolder.RESOURCE_KEY, value = VirtualFolder.FOLDER_VIEW, weight = 9999)
	Boolean readOnly;
	
	public abstract String getDestinationUri();

	public Boolean getAppendUsername() {
		return appendUsername;
	}

	public void setAppendUsername(Boolean appendUsername) {
		this.appendUsername = appendUsername;
	}

	public Boolean getReadOnly() {
		return readOnly;
	}

	public void setReadOnly(Boolean readOnly) {
		this.readOnly = readOnly;
	}

	public String generatePath() {
		if(getAppendUsername()) {
			return FileUtils.checkEndsWithSlash(getDestinationUri()) + "%USERNAME%";
		}
		return getDestinationUri();
	}

}
