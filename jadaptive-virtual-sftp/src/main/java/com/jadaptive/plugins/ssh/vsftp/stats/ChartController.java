package com.jadaptive.plugins.ssh.vsftp.stats;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.lang.model.UnknownEntityException;
import javax.servlet.http.HttpServletRequest;

import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.jadaptive.api.entity.ObjectException;
import com.jadaptive.api.repository.RepositoryException;
import com.jadaptive.api.servlet.PluginController;
import com.jadaptive.api.stats.UsageService;
import com.sshtools.common.util.IOUtils;

@Controller
@Extension
public class ChartController implements PluginController {

	@Autowired
	private UsageService usageService;

	@RequestMapping(value="/app/vfs/stats/monthly", method = { RequestMethod.GET }, produces = {"application/json"})
	@ResponseBody
	@ResponseStatus(value=HttpStatus.OK)
	public MonthlyThroughputData[] getMonthly(HttpServletRequest request) 
			throws RepositoryException, UnknownEntityException, ObjectException {

		MonthlyThroughputData in = new MonthlyThroughputData();
		
		Calendar c = Calendar.getInstance();   // this takes current date
	    c.set(Calendar.DAY_OF_MONTH, 1);
	    c.set(Calendar.HOUR_OF_DAY, 0);
	    c.set(Calendar.MINUTE, 0);
	    c.set(Calendar.SECOND, 0);
	    c.set(Calendar.MILLISECOND, 0);
	    
	    Date from = c.getTime();
	    
	    c.add(Calendar.MONTH, 1);
	    Date to = c.getTime();
	    
		in.setDirection("Ingress");
		in.setSftp(generateValue(usageService.sum(StatsService.SFTP_UPLOAD, from, to)));
		in.setScp(generateValue(usageService.sum(StatsService.SCP_DOWNLOAD, from, to)));
		in.setHttps(generateValue(usageService.sum(StatsService.HTTPS_UPLOAD, from, to)));
		
		
		MonthlyThroughputData out = new MonthlyThroughputData();
		out.setDirection("Egress");
		out.setSftp(generateValue(usageService.sum(StatsService.SFTP_DOWNLOAD, from, to)));
		out.setScp(generateValue(usageService.sum(StatsService.SCP_DOWNLOAD, from, to)));
		out.setHttps(generateValue(usageService.sum(StatsService.HTTPS_DOWNLOAD, from, to)));
	
		
		return new MonthlyThroughputData[] { in, out };
	}
	
	@RequestMapping(value="/app/vfs/stats/throughput", method = { RequestMethod.GET }, produces = {"application/json"})
	@ResponseBody
	@ResponseStatus(value=HttpStatus.OK)
	public Collection<DateValue> getDaily(HttpServletRequest request) 
			throws RepositoryException, UnknownEntityException, ObjectException {

		List<DateValue> values = new ArrayList<>();
		
		Calendar c = Calendar.getInstance();   // this takes current date
	    c.set(Calendar.HOUR_OF_DAY, 0);
	    c.set(Calendar.MINUTE, 0);
	    c.set(Calendar.SECOND, 0);
	    c.set(Calendar.MILLISECOND, 0);
	    c.add(Calendar.DAY_OF_MONTH, 1);
	    
	    Date to = c.getTime();
	    
	    c.add(Calendar.DAY_OF_MONTH, -1);
	    Date from = c.getTime();

		for(int i=0;i<30;i++) {
			long value = usageService.sum(from, to, StatsService.HTTPS_DOWNLOAD,
					StatsService.HTTPS_UPLOAD,
					StatsService.SCP_DOWNLOAD,
					StatsService.SCP_UPLOAD,
					StatsService.SFTP_UPLOAD,
					StatsService.SFTP_DOWNLOAD);
			values.add(new DateValue(to.getTime(), generateValue(value)));
			to = from;
			c.add(Calendar.DAY_OF_MONTH, -1);
		    from = c.getTime();
		}
		
		return values;
	}
	
	private Double generateValue(long val) {
		return BigDecimal.valueOf(Double.valueOf(val) / IOUtils.fromByteSize("1GB"))
	    .setScale(2, RoundingMode.HALF_UP)
	    .doubleValue();
	}
}
