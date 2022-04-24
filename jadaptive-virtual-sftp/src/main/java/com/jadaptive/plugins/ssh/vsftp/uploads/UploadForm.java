package com.jadaptive.plugins.ssh.vsftp.uploads;

import java.util.Collection;

import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.repository.NamedUUIDEntity;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.ObjectDefinition;
import com.jadaptive.api.template.ObjectField;
import com.jadaptive.api.template.ObjectView;
import com.jadaptive.api.template.ObjectViewDefinition;
import com.jadaptive.api.template.ObjectViews;
import com.jadaptive.api.template.TableAction;
import com.jadaptive.api.template.TableAction.Target;
import com.jadaptive.api.template.TableView;
import com.jadaptive.api.template.ValidationType;
import com.jadaptive.api.template.Validator;
import com.jadaptive.plugins.email.AssignmentNotificationPreference;
import com.jadaptive.plugins.email.EmailNotificationServiceImpl;

@ObjectDefinition(resourceKey = UploadForm.RESOURCE_KEY, bundle = UploadForm.RESOURCE_KEY, 
	type = ObjectType.COLLECTION, creatable = true, defaultColumn = "name")
@ObjectViews({ 
	@ObjectViewDefinition(bundle = UploadForm.RESOURCE_KEY, value = UploadForm.FILE_VIEW, weight = 0),
	@ObjectViewDefinition(bundle = UploadForm.RESOURCE_KEY, value = UploadForm.OPTIONS_VIEW, weight = 100),
	@ObjectViewDefinition(bundle = UploadForm.RESOURCE_KEY, value = UploadForm.NOTIFICATIONS_VIEW, weight = 200) })
@TableView(defaultColumns = { "name", "virtualPath" },
             actions = { @TableAction(bundle = UploadForm.RESOURCE_KEY, icon = "fa-link", 
             resourceKey = "copyLink", target = Target.ROW, url = "/app/ui/incoming/{shortCode}") })
public class UploadForm extends NamedUUIDEntity {

	private static final long serialVersionUID = 6440151078128444905L;

	public static final String RESOURCE_KEY = "uploadForms";
	
	static final String FILE_VIEW = "file";
	static final String OPTIONS_VIEW = "options";
	static final String NOTIFICATIONS_VIEW = "notifications";
	
	@ObjectField(type = FieldType.TEXT, searchable = true)
	@ObjectView(value = FILE_VIEW)
	String virtualPath;
	
	@ObjectField(type = FieldType.TEXT, unique = true, searchable = true)
	@ObjectView(value = FILE_VIEW)
	String shortCode;
	
	@ObjectField(type = FieldType.ENUM)
	@ObjectView(value = NOTIFICATIONS_VIEW)
	AssignmentNotificationPreference notifyAssignedUsers;
	
	@ObjectField(type = FieldType.TEXT)
	@ObjectView(value = NOTIFICATIONS_VIEW)
	@Validator(type = ValidationType.REGEX, value = EmailNotificationServiceImpl.EMAIL_PATTERN)
	Collection<String> otherEmails;
	
	
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

	public Collection<String> getOtherEmails() {
		return otherEmails;
	}

	public void setOtherEmails(Collection<String> otherEmails) {
		this.otherEmails = otherEmails;
	}

	public AssignmentNotificationPreference getNotifyAssignedUsers() {
		return notifyAssignedUsers;
	}

	public void setNotifyAssignedUsers(AssignmentNotificationPreference notifyAssignedUsers) {
		this.notifyAssignedUsers = notifyAssignedUsers;
	}
}
