package io.github.hpsocket.demo.job.handler;

import org.springframework.stereotype.Component;

import io.github.hpsocket.soa.framework.web.support.WebServerHelper;
import io.github.hpsocket.soa.starter.job.snailjob.executor.AbstractMdcJobExecutor;

import com.aizuda.snailjob.client.job.core.dto.JobArgs;
import com.aizuda.snailjob.client.model.ExecuteResult;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SnailInheritJobHandler extends AbstractMdcJobExecutor
{
    private int i;

    @Override
    protected ExecuteResult doJobExecuteEx(JobArgs jobArgs)
    {
        log.info("traceId: {}", WebServerHelper.getTraceId());

        if((++i) % 5 == 0)
            throw new RuntimeException("test thow exceptions");
        
        return ExecuteResult.success(jobArgs.getTaskBatchId());
    }

}
