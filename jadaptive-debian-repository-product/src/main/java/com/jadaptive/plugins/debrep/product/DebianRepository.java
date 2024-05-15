package com.jadaptive.plugins.debrep.product;

import org.springframework.stereotype.Component;

import com.jadaptive.api.app.ApplicationVersion;
import com.jadaptive.api.product.Product;
import com.jadaptive.api.product.ProductService.ProductId;

@Component
public class DebianRepository implements Product {

	@Override
	public String getName() {
		return "Debian Repository";
	}

	@Override
	public String getVersion() {
		return ApplicationVersion.getVersion();
	}

	@Override
	public String getPoweredBy() {
		return "Powered by <a href=\"https://jadaptive.com\">Debian Repository</a> \n"
				+ "   A debian repository manager from <a href=\"https://jadaptive.com\">Jadaptive Limited</a>.";
	}
	
	@Override
	public String getProductCode() {
		return "DEBREP";
	}

	@Override
	public ProductId getProductId() {
		return ProductId.DEBIAN_REPOSITORY;
	}

	@Override
	public boolean isRevenueGenerating() {
		return false;
	}
}
