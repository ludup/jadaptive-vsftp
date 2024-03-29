package com.jadaptive.plugins.ssh.vsftp.setup;

import com.jadaptive.api.entity.ObjectScope;
import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.ObjectDefinition;
import com.jadaptive.api.template.ObjectField;
import com.jadaptive.api.template.ValidationType;
import com.jadaptive.api.template.Validator;
import com.jadaptive.api.ui.wizards.WizardUUIDEntity;

@ObjectDefinition(resourceKey = ChooseFilesystem.RESOURCE_KEY, scope = ObjectScope.GLOBAL, type = ObjectType.OBJECT)
public class ChooseFilesystem extends WizardUUIDEntity {

	private static final long serialVersionUID = -1985469611433553828L;
	
	public static final String RESOURCE_KEY = "chooseFilesystem";
	
	@ObjectField(defaultValue = "1", type = FieldType.INTEGER)
	@Validator(type = ValidationType.REQUIRED)
	Integer filesystemType;

	public Integer getFilesystemType() {
		return filesystemType;
	}

	public void setFilesystemType(Integer filesystemType) {
		this.filesystemType = filesystemType;
	}

	@Override
	public String getResourceKey() {
		return RESOURCE_KEY;
	}

}
