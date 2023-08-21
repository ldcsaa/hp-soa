package io.github.hpsocket.demo.job.service.impl;

import org.springframework.stereotype.Service;

import io.github.hpsocket.soa.starter.job.xxljob.exception.XxlJobExceptionHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class XxlJobExceptionHandlerImpl implements XxlJobExceptionHandler
{
	@Override
	public void handleException(String jobName, long jobId, String param, Exception e)
	{
		log.info("handle xxl-job exception : (jobName: {}, jobId: {}, param: '{}', exception: {})", jobName, jobId, param, e);
	}

}
