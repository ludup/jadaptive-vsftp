package com.jadaptive.plugins.debrep;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jadaptive.api.app.StartupAware;
import com.jadaptive.api.entity.AbstractUUIDObjectServceImpl;
import com.jadaptive.api.events.EventService;
import com.jadaptive.api.i18n.I18nService;
import com.jadaptive.api.permissions.PermissionService;

@Service
public class DebianReleaseServiceImpl extends AbstractUUIDObjectServceImpl<DebianRelease>
		implements DebianReleaseService, StartupAware {

	public static final String RESOURCE_BUNDLE = "DebianReleaseResourceService";

	@Autowired
	private DebianRepositoryService debianRepositoryService;
	
	@Autowired
	private EventService eventService;


	public void onApplicationStartup() {

		eventService.any(DebianRelease.class, (e)->{
			for(DebianRepository dr : e.getObject().getRepositories()) {
				if(!dr.getReleases().contains(e.getObject()))
					dr.getReleases().add(e.getObject());
			}
			debianRepositoryService.rebuildConfiguration(e.getObject());
		});

	}

	protected Class<DebianRelease> getResourceClass() {
		return DebianRelease.class;
	}

}
