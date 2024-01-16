package com.jadaptive.plugins.debrep;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.jadaptive.api.scheduler.TaskScope;
import com.jadaptive.api.scheduler.TenantTask;

public class PublishRepositoryJob implements TenantTask {

	private final static Logger log = LoggerFactory.getLogger(PublishRepositoryJob.class);

	@Autowired
	private DebianRepositoryService debianRepositoryResourceService;
	
	public PublishRepositoryJob(DebianRepository repo) {
		this.repo = repo;
	}
	DebianRepository repo;
	@Override
	public TaskScope getScope() {
		return TaskScope.GLOBAL;
	}

	@Override
	public void run() {
				
		try {
			
			boolean autoUpdate = Boolean.TRUE; 
			if (log.isInfoEnabled()) {
				log.info("Checking for extension updates; automatic=" + autoUpdate);
			}
			debianRepositoryResourceService.pushToRemote(repo);

		
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}
}
