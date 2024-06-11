package com.jadaptive.plugins.debrep;

import org.pf4j.PluginWrapper;

import com.jadaptive.api.spring.AbstractSpringPlugin;

public class DebianRepositoryPlugin extends AbstractSpringPlugin {
	
	public final static String RESOURCE_KEY = "debrep";

	public DebianRepositoryPlugin(PluginWrapper wrapper) {
		super(wrapper);
	}

}