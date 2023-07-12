package com.jadaptive.plugins.vsftp.dropbox;

import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.repository.SingletonUUIDEntity;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.ObjectDefinition;
import com.jadaptive.api.template.ObjectField;
import com.jadaptive.api.template.ObjectView;
import com.jadaptive.api.template.ObjectViewDefinition;
import com.jadaptive.api.ui.pages.config.ConfigurationItem;

@ObjectDefinition(resourceKey = DropboxConfiguration.RESOURCE_KEY, type = ObjectType.SINGLETON, system = true)
@ObjectViewDefinition(value = DropboxConfiguration.ACCESS_VIEW, bundle = DropboxConfiguration.RESOURCE_KEY)
@ConfigurationItem(system = true, icon = "fa-dropbox", iconGroup = "fa-brands")
public class DropboxConfiguration extends SingletonUUIDEntity {

	private static final long serialVersionUID = 8146280277810715117L;

	public static final String RESOURCE_KEY = "dropboxConfiguration";
	
	public static final String ACCESS_VIEW = "accessView";

	@ObjectField(type = FieldType.BOOL)
	@ObjectView(ACCESS_VIEW)
	Boolean enableOauth;
	
	@ObjectField(type = FieldType.TEXT)
	@ObjectView(ACCESS_VIEW)
	String oauthDomain;
	
	@ObjectField(type = FieldType.TEXT, automaticEncryption = true)
	@ObjectView(ACCESS_VIEW)
	String appKey;
	
	@ObjectField(type = FieldType.PASSWORD)
	@ObjectView(ACCESS_VIEW)
	String appSecret;
	
	@Override
	public String getResourceKey() {
		return RESOURCE_KEY;
	}

	public Boolean getEnableOauth() {
		return enableOauth;
	}

	public void setEnableOauth(Boolean enableOauth) {
		this.enableOauth = enableOauth;
	}

	public String getOauthDomain() {
		return oauthDomain;
	}

	public void setOauthDomain(String oauthDomain) {
		this.oauthDomain = oauthDomain;
	}

	public String getAppKey() {
		return appKey;
	}

	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}

	public String getAppSecret() {
		return appSecret;
	}

	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
	}
}
