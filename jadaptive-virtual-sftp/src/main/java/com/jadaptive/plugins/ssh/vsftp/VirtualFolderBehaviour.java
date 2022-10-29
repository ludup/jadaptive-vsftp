package com.jadaptive.plugins.ssh.vsftp;

import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.repository.AbstractUUIDEntity;
import com.jadaptive.api.template.ExcludeView;
import com.jadaptive.api.template.FieldRenderer;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.FieldView;
import com.jadaptive.api.template.ObjectDefinition;
import com.jadaptive.api.template.ObjectField;
import com.jadaptive.api.template.ObjectView;
import com.jadaptive.api.template.TableView;

@ObjectDefinition(resourceKey = VirtualFolderBehaviour.RESOURCE_KEY, type = ObjectType.COLLECTION, defaultColumn = "name")
@TableView(defaultColumns = { "name"})
public abstract class VirtualFolderBehaviour extends AbstractUUIDEntity {

	private static final long serialVersionUID = -6989867944664137412L;
	public static final String RESOURCE_KEY = "virtualFolderBehaviour";

	@ObjectField(type = FieldType.TEXT)
	@ExcludeView(values = { FieldView.CREATE, FieldView.UPDATE })
	@ObjectView(value = "", renderer = FieldRenderer.I18N)
	String name = getResourceKey() + ".name";
	
	public abstract boolean supportsMultipleInstances();

	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
	}
}
