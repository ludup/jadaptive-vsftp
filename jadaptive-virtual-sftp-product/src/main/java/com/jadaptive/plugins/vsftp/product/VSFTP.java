package com.jadaptive.plugins.vsftp.product;

import org.springframework.stereotype.Component;

import com.jadaptive.api.app.ApplicationVersion;
import com.jadaptive.api.product.Product;

@Component
public class VSFTP implements Product {

	@Override
	public String getName() {
		return "VSFTP";
	}

	@Override
	public String getVersion() {
		return ApplicationVersion.getVersion();
	}
}
