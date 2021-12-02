package com.jadaptive.plugins.ssh.vsftp.links;

import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.repository.AbstractUUIDEntity;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.ObjectDefinition;
import com.jadaptive.api.template.ObjectField;
import com.jadaptive.api.template.ObjectServiceBean;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;

@ObjectDefinition(resourceKey = PublicDownload.RESOURCE_KEY, bundle = PublicDownload.RESOURCE_KEY, 
	type = ObjectType.COLLECTION, creatable = false)
@ObjectServiceBean( bean = PublicDownloadService.class)
public class PublicDownload extends AbstractUUIDEntity {

	private static final long serialVersionUID = 6440151078128444905L;

	public static final String RESOURCE_KEY = "publicDownload";
	
	@ObjectField(type = FieldType.TEXT)
	String virtualPath;
	
	@ObjectField(type = FieldType.TEXT, unique = true)
	String shortCode;
	
	@Override
	public String getResourceKey() {
		return RESOURCE_KEY;
	}

	public String getVirtualPath() {
		return virtualPath;
	}

	public void setVirtualPath(String virtualPath) {
		this.virtualPath = virtualPath;
	}

	public String getShortCode() {
		return shortCode;
	}

	public void setShortCode(String shortCode) {
		this.shortCode = shortCode;
	}
}
