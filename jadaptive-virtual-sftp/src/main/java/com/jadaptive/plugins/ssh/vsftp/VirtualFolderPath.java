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
import com.jadaptive.api.template.ObjectViewDefinition;
import com.jadaptive.api.template.ObjectViews;

@ObjectDefinition(resourceKey = "virtualFolderPath", type = ObjectType.OBJECT)
public abstract class VirtualFolderPath extends AbstractUUIDEntity {

	private static final long serialVersionUID = 6877837628940930537L;
	
	@ObjectField(type = FieldType.TEXT)
	@ObjectView(value = VirtualFolder.FOLDER_VIEW, bundle = VirtualFolder.RESOURCE_KEY, weight = 100)
	String destinationUri;

	@ObjectField(type = FieldType.ENUM, defaultValue = "ON_RESOLVE")
	@ExcludeView(values = FieldView.TABLE)
	@ObjectView(value = VirtualFolder.FOLDER_VIEW, bundle = VirtualFolder.RESOURCE_KEY, weight = 9999)
	CacheStrategy cacheStrategy;

	public String getDestinationUri() {
		return destinationUri;
	}

	public void setDestinationUri(String destinationUri) {
		this.destinationUri = destinationUri;
	}

	public CacheStrategy getCacheStrategy() {
		return cacheStrategy;
	}

	public void setCacheStrategy(CacheStrategy cacheStrategy) {
		this.cacheStrategy = cacheStrategy;
	}
	
	
}
