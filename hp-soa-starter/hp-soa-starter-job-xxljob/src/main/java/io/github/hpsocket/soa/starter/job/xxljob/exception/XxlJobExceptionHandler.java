package io.github.hpsocket.soa.starter.job.xxljob.exception;

public interface XxlJobExceptionHandler
{
	void handleException(String jobName, long jobId, String param, Exception e);
}
