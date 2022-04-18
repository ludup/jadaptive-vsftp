package com.jadaptive.plugins.ssh.vsftp.links;

import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.repository.AbstractUUIDEntity;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.ObjectDefinition;
import com.jadaptive.api.template.ObjectField;
import com.jadaptive.api.template.ObjectServiceBean;
import com.jadaptive.api.template.ObjectView;
import com.jadaptive.api.template.ObjectViewDefinition;
import com.jadaptive.api.template.ObjectViews;
import com.jadaptive.api.template.TableAction;
import com.jadaptive.api.template.TableAction.Target;
import com.jadaptive.api.template.TableView;

@ObjectDefinition(resourceKey = SharedFile.RESOURCE_KEY, bundle = SharedFile.RESOURCE_KEY, 
	type = ObjectType.COLLECTION, creatable = true, defaultColumn = "filename")
@ObjectServiceBean( bean = SharedFileService.class)
@ObjectViews({ @ObjectViewDefinition(bundle = SharedFile.RESOURCE_KEY, value = "file", weight = 0),
	@ObjectViewDefinition(bundle = SharedFile.RESOURCE_KEY, value = "options", weight = 100)})
@TableView(defaultColumns = { "filename", "passwordProtected", "acceptTerms", "virtualPath" },
             actions = { @TableAction(bundle = SharedFile.RESOURCE_KEY, icon = "fa-link", 
             resourceKey = "copyLink", target = Target.ROW, url = "/app/ui/share/{shortCode}") })
public class SharedFile extends AbstractUUIDEntity {

	private static final long serialVersionUID = 6440151078128444905L;

	public static final String RESOURCE_KEY = "sharedFiles";
	
	@ObjectField(type = FieldType.ENUM, readOnly = true)
	@ObjectView(value = "file")
	ShareType shareType;
	
	@ObjectField(type = FieldType.TEXT, searchable = true)
	@ObjectView(value = "file")
	String virtualPath;
	
	@ObjectField(type = FieldType.TEXT, unique = true, searchable = true)
	@ObjectView(value = "file")
	String shortCode;
	
	@ObjectField(type = FieldType.TEXT, hidden = true)
	@ObjectView(value = "file")
	String filename;

	@ObjectField(type = FieldType.BOOL)
	@ObjectView(value = "options")
	Boolean passwordProtected;
	
	@ObjectField(type = FieldType.PASSWORD)
	@ObjectView(value = "options")
	String password;
	
	@ObjectField(type = FieldType.BOOL)
	@ObjectView(value = "options")
	Boolean acceptTerms;
	
	@ObjectField(type = FieldType.TEXT_AREA)
	@ObjectView(value = "options")
	String terms;
	
	@Override
	public String getResourceKey() {
		return RESOURCE_KEY;
	}
	
	public ShareType getShareType() {
		return shareType;
	}

	public void setShareType(ShareType shareType) {
		this.shareType = shareType;
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
}
