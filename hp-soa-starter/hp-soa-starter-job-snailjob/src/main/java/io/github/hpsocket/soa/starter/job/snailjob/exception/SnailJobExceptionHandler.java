package io.github.hpsocket.soa.starter.job.snailjob.exception;

/** <b>XxlJob 异常处理器接口</b> */
public interface SnailJobExceptionHandler
{
    void handleException(String jobName, Long jobId, String method, String param, long timestamp, Exception e);
}
