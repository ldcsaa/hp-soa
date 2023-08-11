package com.github.hpsocket.soa.starter.job.exclusive.exception;

public interface ExclusiveJobExceptionHandler
{
	void handleException(String jobPrefix, String jobName, Exception e);
}
