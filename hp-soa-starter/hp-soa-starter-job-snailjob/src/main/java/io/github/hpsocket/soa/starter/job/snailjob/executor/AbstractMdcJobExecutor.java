package io.github.hpsocket.soa.starter.job.snailjob.executor;

import org.springframework.beans.factory.annotation.Autowired;

import com.aizuda.snailjob.client.job.core.dto.JobArgs;
import com.aizuda.snailjob.client.job.core.executor.AbstractJobExecutor;
import com.aizuda.snailjob.client.model.ExecuteResult;

/** <b>SnailJob 执行器基类</b> */
abstract public class AbstractMdcJobExecutor extends AbstractJobExecutor
{
    private static final String METHOD = "doJobExecute";
    
    abstract protected ExecuteResult doJobExecuteEx(JobArgs jobArgs);

    @Autowired
    private JobInvoker jobInvoker;

    @Override
    protected ExecuteResult doJobExecute(JobArgs jobArgs)
    {
        String jobName  = this.getClass().getName();

        return jobInvoker.invoke(() -> doJobExecuteEx(jobArgs), jobArgs, jobName, METHOD);
    }
    
}
