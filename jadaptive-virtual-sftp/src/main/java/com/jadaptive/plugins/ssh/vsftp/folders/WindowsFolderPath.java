package com.jadaptive.plugins.ssh.vsftp.folders;

import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.ObjectDefinition;
import com.jadaptive.api.template.ObjectField;
import com.jadaptive.api.template.ObjectView;
import com.jadaptive.api.template.ValidationType;
import com.jadaptive.api.template.Validator;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolderPath;

@ObjectDefinition(resourceKey = WindowsFolderPath.RESOURCE_KEY, type = ObjectType.OBJECT)
public class WindowsFolderPath extends VirtualFolderPath {

	private static final long serialVersionUID = 6251714211294534340L;

	public static final String RESOURCE_KEY = "windowsFolderPath";
	
	@ObjectField(type = FieldType.TEXT)
	@ObjectView(value = VirtualFolder.FOLDER_VIEW, weight = 100, bundle = WindowsFolder.RESOURCE_KEY)
	@Validator(bundle = WindowsFolder.RESOURCE_KEY, type = ValidationType.REGEX, value = "^(\\\\)(\\\\[\\w\\.-_]+){2,}(\\\\?)$", i18n="invalid.share")
	String share;
	
	@Override
	public String getResourceKey() {
		return RESOURCE_KEY;
	}

	@Override
	public String getDestinationUri() {
		return share.replace('\\', '/').replace("//", "/");
	}

}
