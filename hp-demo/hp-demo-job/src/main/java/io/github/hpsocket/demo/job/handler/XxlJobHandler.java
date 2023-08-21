package io.github.hpsocket.demo.job.handler;

import org.springframework.stereotype.Component;

import io.github.hpsocket.soa.framework.web.support.WebServerHelper;
import com.xxl.job.core.handler.annotation.XxlJob;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class XxlJobHandler
{
	private int i;
	
	@XxlJob("xxlJobHandler1")
	public void xxlJobHandler1()
	{
		if((++i) % 5 == 0)
			throw new RuntimeException("test thow exceptions");
		
		log.info("traceId: {}", WebServerHelper.getTraceId());
	}
}
