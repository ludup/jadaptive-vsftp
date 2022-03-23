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
	
	@ObjectField(type = FieldType.TEXT)
	String filename;
	
	@ObjectField(type = FieldType.LONG)
	long size;
	
	@ObjectField(type = FieldType.TEXT)
	String virtualPath;

	public FileUpload() {
		
	}
	
	public FileUpload(String filename, String virtualPath, long size) {
		this.filename = filename;
		this.virtualPath = virtualPath;
		this.size = size;
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
	
	
}
