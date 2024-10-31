package io.github.hpsocket.demo.job.handler;

import org.springframework.stereotype.Component;

import io.github.hpsocket.soa.framework.web.support.WebServerHelper;

import com.aizuda.snailjob.client.job.core.annotation.JobExecutor;
import com.aizuda.snailjob.client.job.core.dto.JobArgs;
import com.aizuda.snailjob.client.model.ExecuteResult;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@JobExecutor(name = "classAnnotationJob", method = "doClassAnnotationJob")
public class SnailAnnotationJobHandler
{
    private int i;
    
    public ExecuteResult doClassAnnotationJob(JobArgs jobArgs)
    {
        log.info("traceId: {}", WebServerHelper.getTraceId());

        if((++i) % 5 == 0)
            throw new RuntimeException("test thow exceptions");
        
        return ExecuteResult.success(jobArgs.getTaskBatchId());
    }
    
    @JobExecutor(name = "methodAnnotationJob")
    public ExecuteResult doMethodAnnotationJob(JobArgs jobArgs)
    {
        log.info("traceId: {}", WebServerHelper.getTraceId());

        if((++i) % 5 == 0)
            throw new RuntimeException("test thow exceptions");
        
        return ExecuteResult.success(jobArgs.getTaskBatchId());
    }
    
}
