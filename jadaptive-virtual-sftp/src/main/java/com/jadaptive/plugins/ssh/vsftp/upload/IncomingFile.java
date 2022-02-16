package com.jadaptive.plugins.ssh.vsftp.upload;

import java.util.Collection;

import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.repository.AbstractUUIDEntity;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.ObjectDefinition;
import com.jadaptive.api.template.ObjectField;
import com.jadaptive.api.template.ObjectView;
import com.jadaptive.api.template.ObjectViewDefinition;
import com.jadaptive.api.template.ObjectViews;
import com.jadaptive.api.template.TableAction;
import com.jadaptive.api.template.TableAction.Target;
import com.jadaptive.api.template.TableAction.Window;
import com.jadaptive.api.template.TableView;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;

@ObjectDefinition(
		 bundle = VirtualFolder.RESOURCE_KEY,
		 resourceKey = IncomingFile.RESOURCE_KEY, 
		 type = ObjectType.COLLECTION,
		 defaultColumn = "reference",
		 creatable = false, 
		 deletable = false,
		 updatable = false, system = true)
@TableView(defaultColumns = {  "reference", "uploadArea", "name", "email" }, actions = {
		@TableAction(target = Target.ROW, bundle = VirtualFolder.RESOURCE_KEY, window = Window.SELF,
				icon = "fa-download", resourceKey = "downloadFiles", url = "/app/vfs/incoming/zip/{uuid}"),
		@TableAction(target = Target.ROW, bundle = VirtualFolder.RESOURCE_KEY, window = Window.SELF,
		icon = "fa-trash", resourceKey = "deleteFile", url = "/app/vfs/incoming/delete/{uuid}", confirmationRequired = true)})
@ObjectViews(@ObjectViewDefinition(value = "files", bundle = VirtualFolder.RESOURCE_KEY))
public class IncomingFile extends AbstractUUIDEntity {

	private static final long serialVersionUID = -854502529745282888L;

	public static final String RESOURCE_KEY = "incomingFiles";
	
	@ObjectField(type = FieldType.TEXT, searchable = true)
	String name;
	
	@ObjectField(type = FieldType.TEXT, searchable = true)
	String email;
	
	@ObjectField(type = FieldType.TEXT, searchable = true)
	String reference;
	
	@ObjectField(type = FieldType.TEXT, searchable = true)
	String uploadArea;
	
	@ObjectField(type = FieldType.OBJECT_EMBEDDED)
	@ObjectView(value = "files")
	Collection<FileUpload> uploadPaths;

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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getUploadArea() {
		return uploadArea;
	}

	public void setUploadArea(String uploadArea) {
		this.uploadArea = uploadArea;
	}

	public Collection<FileUpload> getUploadPaths() {
		return uploadPaths;
	}

	public void setUploadPaths(Collection<FileUpload> uploadPaths) {
		this.uploadPaths = uploadPaths;
	}
}
