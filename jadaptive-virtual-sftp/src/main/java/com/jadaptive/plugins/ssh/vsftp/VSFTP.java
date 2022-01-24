package com.jadaptive.plugins.ssh.vsftp;

import org.pf4j.Extension;

import com.jadaptive.api.product.Product;

@Extension
public class VSFTP implements Product {

	@Override
	public String getName() {
		return "VSFTP";
	}

	@Override
	public String getVersion() {
		return "DEV_VERSION";
	}

	@Override
	public boolean supportsFeature(String feature) {
		return false;
	}

}
