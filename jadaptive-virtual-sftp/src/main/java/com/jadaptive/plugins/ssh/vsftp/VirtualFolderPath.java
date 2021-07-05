package com.jadaptive.plugins.ssh.vsftp;

import org.apache.commons.vfs2.CacheStrategy;

import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.repository.AbstractUUIDEntity;
import com.jadaptive.api.template.ExcludeView;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.FieldView;
import com.jadaptive.api.template.ObjectDefinition;
import com.jadaptive.api.template.ObjectField;
import com.jadaptive.api.template.ObjectView;

@ObjectDefinition(resourceKey = "virtualFolderPath", type = ObjectType.OBJECT)
public abstract class VirtualFolderPath extends AbstractUUIDEntity {

	private static final long serialVersionUID = 6877837628940930537L;

	@ObjectField(type = FieldType.BOOL, defaultValue = "false")
	@ExcludeView(values = FieldView.TABLE)
	@ObjectView(value = VirtualFolder.FOLDER_VIEW, bundle = VirtualFolder.RESOURCE_KEY, weight = 9999)
	Boolean appendUsername = Boolean.FALSE;
	
	@ObjectField(type = FieldType.ENUM, defaultValue = "ON_RESOLVE")
	@ExcludeView(values = FieldView.TABLE)
	@ObjectView(value = VirtualFolder.FOLDER_VIEW, bundle = VirtualFolder.RESOURCE_KEY, weight = 9999)
	CacheStrategy cacheStrategy;

	public abstract String getDestinationUri();

	public CacheStrategy getCacheStrategy() {
		return cacheStrategy;
	}

	public void setCacheStrategy(CacheStrategy cacheStrategy) {
		this.cacheStrategy = cacheStrategy;
	}

	public Boolean getAppendUsername() {
		return appendUsername;
	}

	public void setAppendUsername(Boolean appendUsername) {
		this.appendUsername = appendUsername;
	}
	
}
