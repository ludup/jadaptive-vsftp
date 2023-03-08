package com.jadaptive.plugins.ssh.vsftp.sharing;

import java.util.ArrayList;
import java.util.Collection;

import com.jadaptive.api.entity.ObjectScope;
import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.events.GenerateEventTemplates;
import com.jadaptive.api.repository.NamedDocument;
import com.jadaptive.api.repository.PersonalUUIDEntity;
import com.jadaptive.api.template.ExcludeView;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.FieldView;
import com.jadaptive.api.template.ObjectDefinition;
import com.jadaptive.api.template.ObjectField;
import com.jadaptive.api.template.ObjectServiceBean;
import com.jadaptive.api.template.ObjectView;
import com.jadaptive.api.template.ObjectViewDefinition;
import com.jadaptive.api.template.ObjectViews;
import com.jadaptive.api.template.TableAction;
import com.jadaptive.api.template.TableAction.Target;
import com.jadaptive.api.template.TableView;
import com.jadaptive.api.template.ValidationType;
import com.jadaptive.api.template.Validator;
import com.jadaptive.api.template.Validators;
import com.jadaptive.api.user.User;
import com.jadaptive.plugins.email.EmailNotificationServiceImpl;

@ObjectDefinition(resourceKey = SharedFile.RESOURCE_KEY, bundle = SharedFile.RESOURCE_KEY, 
	type = ObjectType.COLLECTION, scope = ObjectScope.PERSONAL,
	creatable = false, updatable = true, defaultColumn = "name")
@ObjectServiceBean( bean = SharedFileService.class)
@ObjectViews({ 
	@ObjectViewDefinition(bundle = SharedFile.RESOURCE_KEY, value = SharedFile.FILE_VIEW, weight = 0),
	@ObjectViewDefinition(bundle = SharedFile.RESOURCE_KEY, value = SharedFile.OPTIONS_VIEW, weight = 100),
	@ObjectViewDefinition(bundle = SharedFile.RESOURCE_KEY, value = SharedFile.NOTIFICATIONS_VIEW, weight = 200) })
@TableView(defaultColumns = {"name", "filename", "sharedBy", "passwordProtected", "acceptTerms" },
             actions = { @TableAction(bundle = SharedFile.RESOURCE_KEY, icon = "fa-link", 
             resourceKey = "copyLink", target = Target.ROW, url = "/app/ui/share/{shortCode}") })
@GenerateEventTemplates(SharedFile.RESOURCE_KEY)
public class SharedFile extends PersonalUUIDEntity implements NamedDocument {

	private static final long serialVersionUID = 6440151078128444905L;

	public static final String RESOURCE_KEY = "sharedFiles";
	
	static final String FILE_VIEW = "file";
	static final String OPTIONS_VIEW = "options";
	static final String NOTIFICATIONS_VIEW = "notifications";

	@ObjectField(searchable = true, type = FieldType.TEXT, nameField = true)
	@ObjectView(value = FILE_VIEW)
	String name;

	@ObjectField(type = FieldType.TEXT, hidden = true, searchable = true)
	@ObjectView(value = FILE_VIEW)
	String virtualPath;
	
	@ObjectField(type = FieldType.TEXT, unique = true, searchable = true)
	@ObjectView(value = FILE_VIEW)
	String shortCode;
	
	@ObjectField(type = FieldType.TEXT, nameField = true)
	@ObjectView(value = FILE_VIEW)
	@ExcludeView(values = FieldView.CREATE)
	String filename;

	@ObjectField(type = FieldType.BOOL)
	@ObjectView(value = OPTIONS_VIEW)
	Boolean passwordProtected;
	
	@ObjectField(type = FieldType.PASSWORD)
	@ObjectView(value = OPTIONS_VIEW)
	String password;
	
	@ObjectField(type = FieldType.BOOL)
	@ObjectView(value = OPTIONS_VIEW)
	Boolean acceptTerms;
	
	@ObjectField(type = FieldType.TEXT_AREA)
	@ObjectView(value = OPTIONS_VIEW)
	String terms;
	
	@ObjectField(type = FieldType.BOOL, defaultValue = "true")
	@ObjectView(value = NOTIFICATIONS_VIEW)
	Boolean notifySharer;
	
	@ObjectField(type = FieldType.TEXT)
	@ObjectView(value = NOTIFICATIONS_VIEW)
	@Validators({@Validator(type = ValidationType.REGEX, value = EmailNotificationServiceImpl.EMAIL_PATTERN)})
	Collection<String> otherEmails;
	
	@ObjectField(type = FieldType.OBJECT_REFERENCE, readOnly = true, references = User.RESOURCE_KEY, searchable = true)
	@Validators({@Validator(type = ValidationType.RESOURCE_KEY, value = User.RESOURCE_KEY)})
	@ExcludeView(values = FieldView.CREATE)
	User sharedBy;

	@ObjectField(type = FieldType.TEXT, searchable = true)
	@ObjectView(value = FILE_VIEW)
	Collection<String> virtualPaths = new ArrayList<>();
	
	@Override
	public String getResourceKey() {
		return RESOURCE_KEY;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVirtualPath() {
		return virtualPath;
	}
	
	public void setVirtualPath(String virtualPath) {
		this.virtualPath = virtualPath;
	}

	public Collection<String> getVirtualPaths() {
		return virtualPaths;
	}
	
	public void setVirtualPaths(Collection<String> virtualPaths) {
		this.virtualPaths = virtualPaths;
	}

	public String getShortCode() {
		return shortCode;
	}

	public void setShortCode(String shortCode) {
		this.shortCode = shortCode;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public Boolean getPasswordProtected() {
		return passwordProtected;
	}

	public void setPasswordProtected(Boolean passwordProtected) {
		this.passwordProtected = passwordProtected;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getTerms() {
		return terms;
	}

	public void setTerms(String terms) {
		this.terms = terms;
	}

	public Boolean getAcceptTerms() {
		return acceptTerms;
	}

	public void setAcceptTerms(Boolean acceptTerms) {
		this.acceptTerms = acceptTerms;
	}

	public Collection<String> getOtherEmails() {
		return otherEmails;
	}

	public void setOtherEmails(Collection<String> otherEmails) {
		this.otherEmails = otherEmails;
	}

	public Boolean getNotifySharer() {
		return notifySharer;
	}

	public void setNotifySharer(Boolean notifySharer) {
		this.notifySharer = notifySharer;
	}

	public User getSharedBy() {
		return sharedBy;
	}

	public void setSharedBy(User sharedBy) {
		this.sharedBy = sharedBy;
	}

	@Override
	public String getOwnerUUID() {
		return getSharedBy().getUuid();
	}

}
