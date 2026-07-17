package io.github.hpsocket.soa.starter.task.exception;

/** <b>Scheduled Task 异常处理器接口</b> */
public interface ScheduledJobExceptionHandler
{
    void handleException(String methodName, long timestamp, Exception e);
}
