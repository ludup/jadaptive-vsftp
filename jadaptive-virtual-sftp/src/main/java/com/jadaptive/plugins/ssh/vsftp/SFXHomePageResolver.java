package com.jadaptive.plugins.ssh.vsftp;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.jadaptive.api.ui.HomePageResolver;
import com.jadaptive.api.ui.Page;
import com.jadaptive.plugins.ssh.vsftp.ui.Tree;

@Component
public class SFXHomePageResolver implements HomePageResolver {

	@Override
	public int getWeight() {
		return 2000;
	}

	@Override
	public Collection<String> getPermissions() {
		return Collections.emptyList();
	}

	@Override
	public Optional<Class<? extends Page>> resolve() {
		return Optional.of(Tree.class);
	}

}
