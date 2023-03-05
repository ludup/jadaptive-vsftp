package com.jadaptive.plugins.ssh.vsftp.extensions;

import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.repository.AbstractUUIDEntity;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.ObjectDefinition;
import com.jadaptive.api.template.ObjectExtension;
import com.jadaptive.api.template.ObjectField;
import com.jadaptive.api.template.ObjectViewDefinition;
import com.jadaptive.api.template.ObjectViews;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;

@ObjectDefinition(resourceKey = FolderNotes.RESOURCE_KEY, type = ObjectType.OBJECT)
@ObjectExtension(extend = VirtualFolder.RESOURCE_KEY)
@ObjectViews({ 
	@ObjectViewDefinition(value = FolderNotes.NOTES_VIEW, bundle = VirtualFolder.RESOURCE_KEY, weight = Integer.MAX_VALUE)})
public class FolderNotes extends AbstractUUIDEntity {

	private static final long serialVersionUID = -8526380113453685004L;
	
	public static final String RESOURCE_KEY = "folderNotes";

	public static final String NOTES_VIEW = "notesView";
	
	@ObjectField(type = FieldType.TEXT_AREA)
	private String notes;
	
	@Override
	public String getResourceKey() {
		return RESOURCE_KEY;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}
	
	
	
}
