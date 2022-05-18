package com.jadaptive.plugins.ssh.vsftp;

import org.pf4j.Extension;

import com.jadaptive.api.app.ApplicationVersion;
import com.jadaptive.api.product.Product;

@Extension
public class VSFTP implements Product {

	@Override
	public String getName() {
		return "VSFTP";
	}

	@Override
	public String getVersion() {
		return ApplicationVersion.getVersion();
	}

	@Override
	public String getLogoResource() {
		return "/app/content/images/jadaptive-logo.png";
	}

	@Override
	public String getFaviconResource() {
		return "/app/content/images/favicon.png";
	}

}
