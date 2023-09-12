package io.github.hpsocket.demo.job.service.impl;

import org.springframework.stereotype.Service;

import io.github.hpsocket.soa.starter.job.exclusive.exception.ExclusiveJobExceptionHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ExclusiveJobExceptionHandlerImpl implements ExclusiveJobExceptionHandler
{
	@Override
	public void handleException(String jobPrefix, String jobName, Exception e)
	{
		log.info("handle exclusive job exception : (jobPrefix: {}, jobName: {}, exception: {})", jobPrefix, jobName, e);
	}

}
