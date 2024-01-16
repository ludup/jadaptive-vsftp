package com.jadaptive.plugins.debrep;

import com.jadaptive.api.events.AuditedObject;
import com.jadaptive.api.events.ObjectEvent;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.ObjectField;

@AuditedObject
public class DebianRepositoryChangeEvent extends ObjectEvent<DebianRepository> {

	private static final long serialVersionUID = -110567668546913751L;
	
	@ObjectField(type = FieldType.OBJECT_EMBEDDED, references = DebianRepository.RESOURCE_KEY)
	DebianRepository repo;
	
	@ObjectField(type = FieldType.TEXT)
	String filename;
	
	public static final String RESOURCE_KEY = "debianRepository.change";
	
	public DebianRepositoryChangeEvent() {
		super();
	}

	public DebianRepositoryChangeEvent(Throwable e, DebianRepository repo) {
		super(RESOURCE_KEY, "debian", e);
		this.repo = repo;
	}

	public DebianRepositoryChangeEvent(DebianRepository repo, String filename) {
		super(RESOURCE_KEY, "debian");
		this.repo = repo;
	}

	@Override
	public DebianRepository getObject() {
		return repo;
	}

	public DebianRepository getRepo() {
		return repo;
	}

	public void setRepo(DebianRepository repo) {
		this.repo = repo;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

}
