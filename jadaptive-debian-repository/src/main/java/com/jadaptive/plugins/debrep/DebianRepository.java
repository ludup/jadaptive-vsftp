package com.jadaptive.plugins.debrep;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.jadaptive.api.events.GenerateEventTemplates;
import com.jadaptive.api.repository.NamedAssignableUUIDEntity;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.ObjectDefinition;
import com.jadaptive.api.template.ObjectField;
import com.jadaptive.api.template.ObjectViewDefinition;
import com.jadaptive.api.template.TableAction;
import com.jadaptive.api.template.TableAction.Target;
import com.jadaptive.api.template.TableView;
import com.jadaptive.api.ui.menu.ApplicationMenuService;
import com.jadaptive.api.ui.menu.PageMenu;
import com.jadaptive.plugins.ssh.vsftp.VirtualFolder;

@ObjectDefinition(resourceKey = DebianRepository.RESOURCE_KEY)
@GenerateEventTemplates
@TableView(defaultColumns = {"name"})
@PageMenu(bundle = DebianRepository.RESOURCE_KEY, i18n = DebianRepository.RESOURCE_KEY + ".names", icon = "fa-boxes-packing", parent = ApplicationMenuService.RESOURCE_MENU_UUID)
@TableAction(icon = "fa-upload", resourceKey = "uploadRepository", target = Target.ROW, writeAction = true, url = "/app/api/debian/upload", bundle = DebianRepository.RESOURCE_KEY)
@ObjectViewDefinition(value = DebianRepository.VIEW_BASIC, weight = 100)
@ObjectViewDefinition(value = DebianRepository.VIEW_ADVANCED, weight = 200)
public class DebianRepository extends NamedAssignableUUIDEntity {

	private static final long serialVersionUID = -3783535377059453874L;

	public static final String RESOURCE_KEY = "debianRepository";

	public static final String VIEW_BASIC = "basicRepositoryView";
	public static final String VIEW_ADVANCED = "advancedRepositoryView";
	
	@ObjectField(type = FieldType.OBJECT_REFERENCE, references = DebianRelease.RESOURCE_KEY)
	private Set<DebianRelease> releases = new HashSet<DebianRelease>();

	@ObjectField(type = FieldType.TEXT, view = VIEW_ADVANCED)
	private String architectures;
	
	@ObjectField(type = FieldType.TEXT, view = VIEW_ADVANCED)
	private String components;
	
	@ObjectField(type = FieldType.TEXT, view = VIEW_BASIC)
	private String origin;
	
	@ObjectField(type = FieldType.TEXT, view = VIEW_BASIC)
	private String suite;
	
	@ObjectField(type = FieldType.OBJECT_REFERENCE, references = VirtualFolder.RESOURCE_KEY, view = VIEW_BASIC)
	private VirtualFolder remoteRepository;
	
	@ObjectField(type = FieldType.OBJECT_REFERENCE, references = GPGKeyResource.RESOURCE_KEY, view = VIEW_BASIC)
	private GPGKeyResource signWith;
	
	@ObjectField(type = FieldType.ENUM, defaultValue = "inherit", view = VIEW_ADVANCED)
	private DebianPriority overridePriority = DebianPriority.inherit;
	
	@ObjectField(type = FieldType.TEXT, view = VIEW_ADVANCED)
	private String overrideSection;
	
	@ObjectField(type = FieldType.BOOL, view = VIEW_BASIC)
	private boolean shared;
	
	@ObjectField(type = FieldType.BOOL, defaultValue = "true", view = VIEW_BASIC)
	private boolean uploadToRemoteOnUpdate = true;

	public boolean isUploadToRemoteOnUpdate() {
		return uploadToRemoteOnUpdate;
	}

	public void setUploadToRemoteOnUpdate(boolean uploadToRemoteOnUpdate) {
		this.uploadToRemoteOnUpdate = uploadToRemoteOnUpdate;
	}

	public boolean isShared() {
		return shared;
	}

	public VirtualFolder getRemoteRepository() {
		return remoteRepository;
	}

	public void setRemoteRepository(VirtualFolder remoteRepository) {
		this.remoteRepository = remoteRepository;
	}

	public void setShared(boolean shared) {
		this.shared = shared;
	}

	public DebianPriority getOverridePriority() {
		return overridePriority;
	}

	public void setOverridePriority(DebianPriority overridePriority) {
		this.overridePriority = overridePriority;
	}

	public String getOverrideSection() {
		return overrideSection;
	}

	public void setOverrideSection(String overrideSection) {
		this.overrideSection = overrideSection;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String getSuite() {
		return suite;
	}

	public void setSuite(String suite) {
		this.suite = suite;
	}

	public DebianRelease getRelease(String name) {
		for (DebianRelease r : getReleases()) {
			if (r.getName().equals(name)) {
				return r;
			}
		}
		return null;
	}

	public Set<DebianRelease> getReleases() {
		return releases;
	}

	public List<String> getArchitecturesAsList() {
		return architectures == null || architectures.isEmpty() 
				? Collections.emptyList() 
				: Arrays.asList(architectures.split(",")).stream().map(String::trim).toList();
	}

	public String getArchitectures() {
		return architectures;
	}

	public void setArchitectures(String architectures) {
		this.architectures = architectures;
	}

	public List<String> getComponentsAsList() {
		return components == null || components.isEmpty() 
				? Collections.emptyList() 
				: Arrays.asList(components.split(",")).stream().map(String::trim).toList();
	}

	public String getComponents() {
		return components;
	}

	public void setComponents(String components) {
		this.components = components;
	}

	public GPGKeyResource getSignWith() {
		return signWith;
	}

	public void setSignWith(GPGKeyResource signWith) {
		this.signWith = signWith;
	}

	@Override
	public String toString() {
		return "DebianRepositoryResource [releases=" + releases + ", architectures=" + architectures + ", components="
				+ components + ", origin=" + origin + ", suite=" + suite + ", signWith=" + signWith
				+ ", overridePriority=" + overridePriority + ", overrideSection=" + overrideSection + ", shared="
				+ shared + "]";
	}

	@Override
	public String getResourceKey() {
		return RESOURCE_KEY;
	}
}
