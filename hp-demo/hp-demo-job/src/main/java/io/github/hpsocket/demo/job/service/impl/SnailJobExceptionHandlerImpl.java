package io.github.hpsocket.demo.job.service.impl;

import org.springframework.stereotype.Service;

import io.github.hpsocket.soa.starter.job.snailjob.exception.SnailJobExceptionHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SnailJobExceptionHandlerImpl implements SnailJobExceptionHandler
{
    @Override
    public void handleException(String jobName, Long jobId, String method, String param, long timestamp, Exception e)
    {
        log.info("handle snail-job exception : (jobName: {}, jobId: {}, method: {}, param: {}, exception: {})", jobName, jobId, method, param, e);
    }

}
