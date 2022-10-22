package com.jadaptive.plugins.ssh.vsftp.ui.wizards;

import com.jadaptive.api.entity.ObjectScope;
import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.repository.UUIDEntity;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.ObjectDefinition;
import com.jadaptive.api.template.ObjectField;
import com.jadaptive.api.template.ValidationType;
import com.jadaptive.api.template.Validator;

@ObjectDefinition(resourceKey = UploadFormName.RESOURCE_KEY, scope = ObjectScope.GLOBAL, type = ObjectType.OBJECT, bundle = UploadFormWizard.RESOURCE_KEY)
public class UploadFormName extends UUIDEntity {

	private static final long serialVersionUID = -3801741503005746668L;
	
	public static final String RESOURCE_KEY = "publicUploadName";

	@Override
	public String getResourceKey() {
		return RESOURCE_KEY;
	}
	
	@ObjectField(type = FieldType.TEXT)
	@Validator(type = ValidationType.REGEX, value = "^[a-zA-Z0-9_\\- ]{4,64}$", bundle = UploadFormWizard.RESOURCE_KEY, i18n = "name.invalid")
	String name;
	
	@ObjectField(type = FieldType.TEXT, defaultValue = "/public")
	@Validator(type = ValidationType.REGEX, value = "^\\/[a-zA-Z0-9_\\- \\/]*$", bundle = UploadFormWizard.RESOURCE_KEY, i18n = "virtualPath.invalid")
	String virtualPath;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVirtualPath() {
		return virtualPath;
	}

	public void setVirtualPath(String virtualPath) {
		this.virtualPath = virtualPath;
	}

	
}
