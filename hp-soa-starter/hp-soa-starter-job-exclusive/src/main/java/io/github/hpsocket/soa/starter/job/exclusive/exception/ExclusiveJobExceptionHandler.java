package io.github.hpsocket.soa.starter.job.exclusive.exception;

/** <b>分布式独占 Job 异常处理器接口</b> */
public interface ExclusiveJobExceptionHandler
{
    void handleException(String jobPrefix, String jobName, Exception e);
}
