package com.jadaptive.plugins.debrep;

import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.jadaptive.api.permissions.AuthenticatedContext;
import com.jadaptive.api.permissions.AuthenticatedController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Extension
@Controller
public class GPGController extends AuthenticatedController {

	static Logger LOG = LoggerFactory.getLogger(GPGController.class);
	
	@Autowired
	private GPGKeyService gpgKeyService;
	
	@RequestMapping(value = "/app/api/gpg/download/{uuid}", method = RequestMethod.GET, produces = { "text/plain" })
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	@AuthenticatedContext(system = true)
	public String getPublicKeyContent(HttpServletRequest request,
			HttpServletResponse response, @PathVariable String uuid) {
		return gpgKeyService.getPublicContent(gpgKeyService.getObjectByUUID(uuid));
	}
	
}
