package io.github.hpsocket.demo.job.handler;

import org.springframework.stereotype.Component;

import io.github.hpsocket.soa.framework.web.support.WebServerHelper;
import io.github.hpsocket.soa.starter.job.exclusive.annotation.ExclusiveJob;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ExclusiveJobHandler
{
	int i = 0;
	
	//@Scheduled(cron = "*/5 * * * * ?")
	@ExclusiveJob(jobName = "job1", cron = "*/5 * * * * ?")
	public void job1()
	{
		if((++i) % 5 == 0)
			throw new RuntimeException("test thow exceptions");
		
		log.info("traceId: {}", WebServerHelper.getTraceId());
	}
}
