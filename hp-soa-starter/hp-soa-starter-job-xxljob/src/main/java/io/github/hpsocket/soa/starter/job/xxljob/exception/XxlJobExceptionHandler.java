package io.github.hpsocket.soa.starter.job.xxljob.exception;

/** <b>XxlJob 异常处理器接口</b> */
public interface XxlJobExceptionHandler
{
    void handleException(String jobName, long jobId, String param, Exception e);
}
