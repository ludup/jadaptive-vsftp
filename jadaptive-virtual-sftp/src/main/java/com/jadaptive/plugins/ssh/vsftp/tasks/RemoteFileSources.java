package com.jadaptive.plugins.ssh.vsftp.tasks;

import java.util.Collection;

import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.repository.AbstractUUIDEntity;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.ObjectDefinition;
import com.jadaptive.api.template.ObjectField;

@ObjectDefinition(resourceKey = RemoteFileSources.RESOURCE_KEY, type = ObjectType.OBJECT)
public class RemoteFileSources extends AbstractUUIDEntity {

	private static final long serialVersionUID = 4479279858955794444L;

	public static final String RESOURCE_KEY = "remoteFileSources";
	
	@ObjectField(type = FieldType.TEXT)
	Collection<String> paths;

	public Collection<String> getPaths() {
		return paths;
	}

	public void setPaths(Collection<String> paths) {
		this.paths = paths;
	}

	@Override
	public String getResourceKey() {
		return RESOURCE_KEY;
	}
	
}
