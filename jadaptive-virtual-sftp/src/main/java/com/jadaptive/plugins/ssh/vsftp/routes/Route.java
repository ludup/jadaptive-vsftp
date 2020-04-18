package com.jadaptive.plugins.ssh.vsftp.routes;

import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.regex.Pattern;

import com.jadaptive.api.entity.EntityType;
import com.jadaptive.api.repository.NamedUUIDEntity;
import com.jadaptive.api.template.Column;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.Template;

@Template(name = "Route", resourceKey = Route.RESOURCE_KEY, type = EntityType.COLLECTION)
public class Route extends NamedUUIDEntity {

	public static final String RESOURCE_KEY = "route";

	@Column(name = "User", 
			description = "Active this route if the username of the file transaction matches this value",
			searchable = true,
			type = FieldType.TEXT)
	String user;
	
	@Column(name = "Remote Address", 
			description = "Active this route if the remote IP address of the file transaction matches this value",
			searchable = true,
			type = FieldType.TEXT)
	String remoteAddress;
	
	@Column(name = "Path", 
			description = "Active this route if the path of the file transaction matches this value",
			searchable = true,
			type = FieldType.TEXT)
	String path;

	@Column(name = "Type", 
			description = "Does this route apply to the input or output of a file transaction",
			searchable = true,
			type = FieldType.ENUM)
	RouteType type;
	
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getRemoteAddress() {
		return remoteAddress;
	}

	public void setRemoteAddress(String remoteAddress) {
		this.remoteAddress = remoteAddress;
	}

	public boolean matches(String path) {
		
		if(doRegexMatch(path)) {
			return true;
		}
		
		if(doGlobMatch(path)) {
			return true;
		}
		
		return false;
	}

	private boolean doGlobMatch(String path) {
		PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + path);
		return matcher.matches(Paths.get(path));
	}

	private boolean doRegexMatch(String path) {
		Pattern p = Pattern.compile(path);
		return p.matcher(path).matches();
	}

	@Override
	public String getResourceKey() {
		return RESOURCE_KEY;
	}
}
