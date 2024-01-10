package com.jadaptive.plugins.ssh.vsftp.tasks;

import java.util.Collection;

import com.jadaptive.api.entity.ObjectType;
import com.jadaptive.api.tasks.TaskResult;
import com.jadaptive.api.template.FieldType;
import com.jadaptive.api.template.ObjectDefinition;
import com.jadaptive.api.template.ObjectField;

@ObjectDefinition(resourceKey = MultipleTaskResults.RESOURCE_KEY, type = ObjectType.OBJECT)
public class MultipleTaskResults extends TaskResult {

	private static final long serialVersionUID = -3554324489379628539L;

	public static final String RESOURCE_KEY = "multipleTaskResults";
	
	@ObjectField(type = FieldType.OBJECT_EMBEDDED)
	Collection<TaskResult> results;
	
	public MultipleTaskResults(Collection<TaskResult> results) {
		super(RESOURCE_KEY);
		this.results = results;
	}
	
	@Override
	public String getEventGroup() {
		return "fileTasks";
	}

	public Collection<TaskResult> getResults() {
		return results;
	}

	public void setResults(Collection<TaskResult> results) {
		this.results = results;
	}

}
