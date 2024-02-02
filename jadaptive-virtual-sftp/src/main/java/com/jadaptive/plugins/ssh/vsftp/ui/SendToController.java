package com.jadaptive.plugins.ssh.vsftp.ui;

import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jadaptive.api.json.RequestStatus;
import com.jadaptive.api.json.RequestStatusImpl;
import com.jadaptive.api.servlet.Request;
import com.jadaptive.api.ui.UriRedirect;
import com.jadaptive.plugins.ssh.vsftp.sendto.SendToService;

@Controller
@Extension
public class SendToController extends AbstractFileController {

	@Autowired
	private SendToService transferService; 
	
	@RequestMapping(value = "/app/api/sendTo/receiver/{shareCode}/{count}", method = { RequestMethod.GET })
	@ResponseBody
	public RequestStatus waitForReciverer(@PathVariable String shareCode, @PathVariable Integer count) {
		return new RequestStatusImpl(transferService.isReceiverConnected(shareCode, count));
	}
	
	
	@RequestMapping(value = "/app/api/sendTo/recv/{shareCode}", method = { RequestMethod.GET })
	public void receiveFile(@PathVariable String shareCode) throws InterruptedException {
		transferService.receiveFile(shareCode, Request.response());
	}
	
	@RequestMapping(value = "/app/api/sendTo/status/{shareCode}", method = { RequestMethod.GET })
	@ResponseBody
	public RequestStatus transferStatus(@PathVariable String shareCode) throws InterruptedException {
		return new RequestStatusImpl(transferService.status(shareCode));
	}
	
	@RequestMapping(value = "/recv/{shareCode}", method = { RequestMethod.GET })
	@ResponseBody
	public RequestStatus redirect(@PathVariable String shareCode) {
		throw new UriRedirect("/app/ui/recv/" + shareCode);
	}
	
	@RequestMapping(value = "/send", method = { RequestMethod.GET })
	@ResponseBody
	public RequestStatus redirect() {
		throw new UriRedirect("/app/ui/send-to");
	}
	
	
}
