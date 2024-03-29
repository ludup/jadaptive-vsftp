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

import org.apache.commons.lang3.time.DateUtils;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.jadaptive.api.entity.ObjectException;
import com.jadaptive.api.permissions.PermissionService;
import com.jadaptive.api.repository.RepositoryException;
import com.jadaptive.api.servlet.PluginController;
import com.jadaptive.api.session.SessionTimeoutException;
import com.jadaptive.api.session.SessionUtils;
import com.jadaptive.api.session.UnauthorizedException;
import com.jadaptive.api.stats.UsageService;
import com.jadaptive.api.user.User;
import com.jadaptive.utils.Utils;
import com.sshtools.common.util.IOUtils;

@Controller
@Extension
public class VSFTPChartController implements PluginController {

	@Autowired
	private UsageService usageService;

	@Autowired
	private PermissionService permissionService; 
	
	@Autowired
	private SessionUtils sessionUtils;
	
	@RequestMapping(value="/app/vfs/stats/monthly", method = { RequestMethod.GET }, produces = {"application/json"})
	@ResponseBody
	@ResponseStatus(value=HttpStatus.OK)
	public MonthlyThroughputData[] getMonthly(HttpServletRequest request) 
			throws RepositoryException, UnknownEntityException, ObjectException, UnauthorizedException, SessionTimeoutException {

		MonthlyThroughputData in = new MonthlyThroughputData();
		User currentUser = sessionUtils.getCurrentUser(request);
		boolean allUsers = permissionService.isAdministrator(currentUser);
		
	    Date to = Utils.tomorrow();

		Calendar c = Calendar.getInstance();   // this takes current date
	    c.set(Calendar.HOUR_OF_DAY, 0);
	    c.set(Calendar.MINUTE, 0);
	    c.set(Calendar.SECOND, 0);
	    c.set(Calendar.MILLISECOND, 0);
	    c.add(Calendar.DAY_OF_MONTH, -30);
	    Date from = c.getTime();
	    
		in.setDirection("Uploads");
		in.setSftp(generateValue(allUsers ? 
				usageService.sum(StatsService.SFTP_UPLOAD, from, to) :
				usageService.sumAnd(from,to, currentUser.getUuid(), StatsService.SFTP_UPLOAD).longValue()));
		in.setScp(generateValue(allUsers ? 
				usageService.sum(StatsService.SCP_UPLOAD, from, to) :
				usageService.sumAnd(from,to, currentUser.getUuid(), StatsService.SCP_UPLOAD).longValue()));
		in.setHttps(generateValue(allUsers ? 
				usageService.sum(StatsService.HTTPS_UPLOAD, from, to) :
				usageService.sumAnd(from,to, currentUser.getUuid(), StatsService.HTTPS_UPLOAD).longValue()));

		
		MonthlyThroughputData out = new MonthlyThroughputData();
		out.setDirection("Downloads");
		in.setSftp(generateValue(allUsers ? 
				usageService.sum(StatsService.SFTP_DOWNLOAD, from, to) :
				usageService.sumAnd(from,to, currentUser.getUuid(), StatsService.SFTP_DOWNLOAD).longValue()));
		in.setScp(generateValue(allUsers ? 
				usageService.sum(StatsService.SCP_DOWNLOAD, from, to) :
				usageService.sumAnd(from,to, currentUser.getUuid(), StatsService.SCP_DOWNLOAD).longValue()));
		in.setHttps(generateValue(allUsers ? 
				usageService.sum(StatsService.HTTPS_DOWNLOAD, from, to) :
				usageService.sumAnd(from,to, currentUser.getUuid(), StatsService.HTTPS_DOWNLOAD).longValue()));
	
		
		return new MonthlyThroughputData[] { in, out };
	}
	
	@RequestMapping(value="/app/vfs/stats/throughput", method = { RequestMethod.GET }, produces = {"application/json"})
	@ResponseBody
	@ResponseStatus(value=HttpStatus.OK)
	public Collection<DateValue> getDaily(HttpServletRequest request) 
			throws RepositoryException, UnknownEntityException, ObjectException, UnauthorizedException, SessionTimeoutException {

		List<DateValue> values = new ArrayList<>();
		User currentUser = sessionUtils.getCurrentUser(request);
		boolean allUsers = permissionService.isAdministrator(currentUser);
	    
	    Date to = Utils.tomorrow();
	    Date from = Utils.today();

	    long value;
		for(int i=0;i<30;i++) {
			if(allUsers) {
				value = usageService.sumOr(from, to, StatsService.HTTPS_DOWNLOAD,
						StatsService.HTTPS_UPLOAD,
						StatsService.SCP_DOWNLOAD,
						StatsService.SCP_UPLOAD,
						StatsService.SFTP_UPLOAD,
						StatsService.SFTP_DOWNLOAD);
			} else {
				value = usageService.sum(currentUser.getUuid(), from, to);
			}
			
			values.add(new DateValue(from.getTime(), generateValue(value)));
			to = from;
			from = DateUtils.addDays(from, -1);
		}
		
		return values;
	}
	
	private Double generateValue(long val) {
		return BigDecimal.valueOf(Double.valueOf(val) / IOUtils.fromByteSize("1GB"))
	    .setScale(2, RoundingMode.HALF_UP)
	    .doubleValue();
	}
}
