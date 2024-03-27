package com.jadaptive.plugins.vsftp.product;

import org.springframework.stereotype.Component;

import com.jadaptive.api.app.ApplicationVersion;
import com.jadaptive.api.product.Product;
import com.jadaptive.api.product.ProductService.ProductId;

@Component
public class SecureFileExchangeOnPrem implements Product {

	@Override
	public String getName() {
		return "Secure File Exchange";
	}

	@Override
	public String getVersion() {
		return ApplicationVersion.getVersion();
	}

	@Override
	public String getPoweredBy() {
		return "Powered by <a href=\"https://jadaptive.com\">Secure File Exchange</a> Self-Hosted SFTP\n"
				+ "   and File Sharing Server from <a href=\"https://jadaptive.com\">Jadaptive Limited</a>.";
	}
	
	@Override
	public String getProductCode() {
		return "SFXCHANGE";
	}

	@Override
	public ProductId getProductId() {
		return ProductId.SECURE_FILE_EXCHANGE_OMPREM;
	}
}