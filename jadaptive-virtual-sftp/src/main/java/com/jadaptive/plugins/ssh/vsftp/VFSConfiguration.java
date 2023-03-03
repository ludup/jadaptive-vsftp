package com.jadaptive.plugins.ssh.vsftp;

import java.util.Collection;

import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.repository.SingletonUUIDEntity;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.ObjectDefinition;
import com.jadaptive.api.template.ObjectField;
import com.jadaptive.api.template.ObjectView;
import com.jadaptive.api.template.ObjectViewDefinition;
import com.jadaptive.api.template.ObjectViews;
import com.jadaptive.api.template.ValidationType;
import com.jadaptive.api.template.Validator;
import com.jadaptive.api.template.Validators;
import com.jadaptive.plugins.email.EmailNotificationServiceImpl;

@ObjectDefinition(resourceKey = VFSConfiguration.RESOURCE_KEY, type = ObjectType.SINGLETON, aliases = { VFSConfiguration.RESOURCE_KEY + ".updated" })
@ObjectViews({
	@ObjectViewDefinition(value = VFSConfiguration.GENERAL_VIEW, bundle = VFSConfiguration.RESOURCE_KEY),
	@ObjectViewDefinition(value = VFSConfiguration.SHARING_VIEW, bundle = VFSConfiguration.RESOURCE_KEY)
})
public class VFSConfiguration extends SingletonUUIDEntity {

	private static final long serialVersionUID = 8028157806019041591L;

	public static final String GENERAL_VIEW = "general";
	public static final String SHARING_VIEW = "sharing";
	
	public static final String RESOURCE_KEY = "vfsConfiguration";
	
	@ObjectField(type = FieldType.ENUM, defaultValue = "SHA256")
	@ObjectView(value = GENERAL_VIEW)
	ContentHash defaultHash;
	
	@ObjectField(type = FieldType.TEXT)
	@ObjectView(value = SHARING_VIEW)
	@Validators({@Validator(type = ValidationType.REGEX, value = EmailNotificationServiceImpl.EMAIL_PATTERN)})
	Collection<String> notificationEmails;
	
	@Override
	public String getResourceKey() {
		return "vfsConfiguration";
	}

	public ContentHash getDefaultHash() {
		return defaultHash;
	}

	public void setDefaultHash(ContentHash defaultHash) {
		this.defaultHash = defaultHash;
	}

	public Collection<String> getNotificationEmails() {
		return notificationEmails;
	}

	public void setNotificationEmails(Collection<String> notificationEmails) {
		this.notificationEmails = notificationEmails;
	}

}
