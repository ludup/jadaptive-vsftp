package com.jadaptive.plugins.debrep;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.jadaptive.api.events.GenerateEventTemplates;
import com.jadaptive.api.repository.NamedUUIDEntity;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.ObjectDefinition;
import com.jadaptive.api.template.ObjectField;
import com.jadaptive.api.template.TableAction;
import com.jadaptive.api.template.TableAction.Target;
import com.jadaptive.api.ui.menu.ApplicationMenuService;
import com.jadaptive.api.ui.menu.PageMenu;

@ObjectDefinition(resourceKey = DebianRelease.RESOURCE_KEY)
@GenerateEventTemplates
@PageMenu(bundle = DebianRelease.RESOURCE_KEY, i18n = DebianRelease.RESOURCE_KEY + ".names", icon = "fa-code-fork", parent = ApplicationMenuService.RESOURCE_MENU_UUID)
@TableAction(icon = "fa-asterisk", resourceKey = "promoteDebianRelease", target = Target.ROW, writeAction = true, url = "/app/api/debian/promote/{uuid}", bundle = DebianRelease.RESOURCE_KEY)
public class DebianRelease extends NamedUUIDEntity {

	private static final long serialVersionUID = -5832968884541307217L;

	public static final String RESOURCE_KEY = "debianRelease";
	
	@ObjectField(type = FieldType.OBJECT_EMBEDDED)
	Set<DebianRepository> repositories = new HashSet<DebianRepository>();

	@ObjectField(type = FieldType.OBJECT_REFERENCE)
	private GPGKeyResource signWith;

	@ObjectField(type = FieldType.TEXT)
	private String label;
	
	@ObjectField(type = FieldType.TEXT)
	private String origin;
	
	@ObjectField(type = FieldType.TEXT)
	private String suite;
	
	@ObjectField(type = FieldType.TEXT)
	private String architectures;
	
	@ObjectField(type = FieldType.TEXT)
	private String components;
	
	@ObjectField(type = FieldType.TEXT)
	private String description;
	
	@ObjectField(type = FieldType.ENUM)
	private DebianPriority overridePriority;
	
	@ObjectField(type = FieldType.TEXT)
	private String overrideSection;

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public Set<DebianRepository> getRepositories() {
		return repositories;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setRepositories(Set<DebianRepository> repositories) {
		this.repositories.clear();
		this.repositories.addAll(repositories);
	}

	public List<String> getArchitectures() {
		return architectures == null ? Collections.emptyList() : Arrays.asList(architectures.split(","));
	}

	public void setArchitectures(List<String> architectures) {
		this.architectures = architectures == null || architectures.size() == 0 ? null
				: String.join(",", architectures);
	}

	public List<String> getComponents() {
		return components == null ? Collections.emptyList() : Arrays.asList(components.split(","));
	}

	public void setComponents(List<String> components) {
		this.components = components == null || components.size() == 0 ? null : String.join(",", components);
	}

	public GPGKeyResource getSignWith() {
		return signWith;
	}

	public void setSignWith(GPGKeyResource signWith) {
		this.signWith = signWith;
	}

	public void write(PrintWriter writer, DebianRepository repository) {
		if (StringUtils.isBlank(origin)) {
			if (StringUtils.isBlank(repository.getOrigin()))
				writer.println(String.format("Origin: %s", "Hypersocket Debrep"));
			else
				writer.println(String.format("Origin: %s", repository.getOrigin()));
		} else
			writer.println(String.format("Origin: %s", origin));
		writer.println(String.format("Label: %s", label));
		if (StringUtils.isBlank(suite)) {
			if (StringUtils.isBlank(repository.getSuite()))
				writer.println(String.format("Suite: %s", "stable"));
			else
				writer.println(String.format("Suite: %s", repository.getSuite()));
		} else
			writer.println(String.format("Suite: %s", suite));
		writer.println(String.format("Codename: %s", getName()));
		writer.println(String.format("Version: 3.1"));
		if (architectures == null || architectures.isEmpty()) {
			if (repository.getArchitectures() == null || repository.getArchitectures().isEmpty())
				writer.println(String.format("Architectures: %s", "i386 amd64"));
			else
				writer.println(String.format("Architectures: %s", String.join(" ", repository.getArchitectures())));
		} else
			writer.println(String.format("Architectures: %s", String.join(" ", architectures)));
		if (components == null || components.isEmpty()) {
			if (repository.getComponents() == null || repository.getComponents().isEmpty())
				writer.println(String.format("Components: %s", "i386 amd64"));
			else
				writer.println(String.format("Components: %s", String.join(" ", repository.getComponents())));
		} else
			writer.println(String.format("Components: %s", String.join(" ", components)));
		writer.println(String.format("Description: %s", StringUtils.isBlank(description) ? label : description));
		if (signWith == null) {
			if (repository.getSignWith() != null)
				writer.println(String.format("SignWith: %s", repository.getSignWith().getFingerprint()));
		} else
			writer.println(String.format("SignWith: %s", signWith.getFingerprint()));
		writer.println(String.format("DscIndices: %s", "Sources Release . .gz .bz2"));
		writer.println(String.format("DebIndices: %s", "Packages Release . .gz .bz2"));
	}

	@Override
	public String getResourceKey() {
		return RESOURCE_KEY;
	}
}
