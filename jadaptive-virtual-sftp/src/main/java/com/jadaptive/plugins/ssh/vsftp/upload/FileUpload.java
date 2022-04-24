package com.jadaptive.plugins.ssh.vsftp.upload;

import java.util.UUID;

import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.repository.AbstractUUIDEntity;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.ObjectDefinition;
import com.jadaptive.api.template.ObjectField;
import com.jadaptive.api.template.TableAction;
import com.jadaptive.api.template.TableAction.Target;
import com.jadaptive.api.template.TableView;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.jadaptive.utils.Utils;
import com.sshtools.common.util.IOUtils;

@ObjectDefinition(resourceKey = FileUpload.RESOURCE_KEY, 
					bundle = VirtualFolder.RESOURCE_KEY, 
					type = ObjectType.OBJECT, 
					creatable = false,
					deletable = false, 
					updatable = false)
@TableView(defaultColumns = { "filename", "size"}, requiresView = false, actions = { 
	@TableAction(bundle = VirtualFolder.RESOURCE_KEY, 
					icon = "fa-download", 
					resourceKey = "downloadFile", 
					target = Target.ROW, 
					url="/app/vfs/downloadFile{virtualPath}")})
public class FileUpload extends AbstractUUIDEntity {

	private static final long serialVersionUID = -6786661863502454580L;

	public static final String RESOURCE_KEY = "fileUpload";
	
	@ObjectField(type = FieldType.TEXT, readOnly = true)
	String filename;
	
	@ObjectField(type = FieldType.LONG, readOnly = true)
	long size;
	
	@ObjectField(type = FieldType.TEXT, readOnly = true)
	String virtualPath;
	
	@ObjectField(type = FieldType.TEXT, readOnly = true)
	String contentHash;

	public FileUpload() {
		
	}
	
	public FileUpload(String filename, String virtualPath, long size, String contentHash) {
		this.filename = filename;
		this.virtualPath = virtualPath;
		this.size = size;
		this.contentHash = contentHash;
		setUuid(UUID.randomUUID().toString());
		setCreated(Utils.now());
		setLastModified(getCreated());
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public long getSize() {
		return size;
	}
	
	public String getDisplaySize() {
		return IOUtils.toByteSize(size, 1);
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getVirtualPath() {
		return virtualPath;
	}

	public void setVirtualPath(String virtualPath) {
		this.virtualPath = virtualPath;
	}

	@Override
	public String getResourceKey() {
		return RESOURCE_KEY;
	}

	public String getContentHash() {
		return contentHash;
	}

	public void setContentHash(String contentHash) {
		this.contentHash = contentHash;
	}
	
	
}
