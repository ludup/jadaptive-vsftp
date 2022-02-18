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
	public final boolean supportsFeature(String feature) {
		switch(feature) {
//		case "eventLog":
//			return true;
		default:
			return false;
		}
	}

}
