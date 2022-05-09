package com.jadaptive.plugins.ssh.vsftp.ui.wizards;

import java.util.Collection;

import com.jadaptive.api.entity.ObjectScope;
import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.repository.UUIDEntity;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.ObjectDefinition;
import com.jadaptive.api.template.ObjectField;
import com.jadaptive.api.template.ValidationType;
import com.jadaptive.api.template.Validator;
import com.jadaptive.plugins.email.AssignmentNotificationPreference;
import com.jadaptive.plugins.email.EmailNotificationServiceImpl;
import com.jadaptive.utils.Utils;

@ObjectDefinition(resourceKey = PublicUploadOptions.RESOURCE_KEY, scope = ObjectScope.GLOBAL, type = ObjectType.OBJECT, bundle = PublicUploadWizard.RESOURCE_KEY)
public class PublicUploadOptions extends UUIDEntity {

	private static final long serialVersionUID = -8390130225179258343L;

	public static final String RESOURCE_KEY = "publicUploadOptions";
	
	@ObjectField(type = FieldType.TEXT, unique = true, searchable = true)
	String shortCode = Utils.generateRandomAlphaNumericString(8);
	
	@ObjectField(type = FieldType.ENUM)
	AssignmentNotificationPreference notifyAssignedUsers;
	
	@ObjectField(type = FieldType.TEXT)
	@Validator(type = ValidationType.REGEX, value = EmailNotificationServiceImpl.EMAIL_PATTERN)
	Collection<String> otherEmails;

	public String getShortCode() {
		return shortCode;
	}

	public void setShortCode(String shortCode) {
		this.shortCode = shortCode;
	}

	public AssignmentNotificationPreference getNotifyAssignedUsers() {
		return notifyAssignedUsers;
	}

	public void setNotifyAssignedUsers(AssignmentNotificationPreference notifyAssignedUsers) {
		this.notifyAssignedUsers = notifyAssignedUsers;
	}

	public Collection<String> getOtherEmails() {
		return otherEmails;
	}

	public void setOtherEmails(Collection<String> otherEmails) {
		this.otherEmails = otherEmails;
	}

	@Override
	public String getResourceKey() {
		return RESOURCE_KEY;
	}
}
