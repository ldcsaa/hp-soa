package io.github.hpsocket.demo.mq.producer.handler;

import org.springframework.stereotype.Component;

import io.github.hpsocket.soa.framework.web.support.WebServerHelper;
import io.github.hpsocket.soa.starter.job.exclusive.annotation.ExclusiveJob;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MqSenderJobHandler
{
	int i = 0;
	
	//@ExclusiveJob(jobName = "mqSender", cron = "*/5 * * * * ?")
	public void job1()
	{

	}
}
