package io.github.hpsocket.soa.starter.web.cloud.exception;

import com.alibaba.csp.sentinel.slots.block.BlockException;

import io.github.hpsocket.soa.starter.sentinel.exception.DefaultSentinelBlockExceptionHandler;
import io.github.hpsocket.soa.starter.web.cloud.support.TracingHelper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CloudSentinelBlockExceptionHandler extends DefaultSentinelBlockExceptionHandler
{

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, BlockException e) throws Exception
    {
        if(TracingHelper.isEntry())
            super.handle(request, response, e);
        else
        {
            log.warn(e.getMessage());
            throw e;
        }
    }

}
