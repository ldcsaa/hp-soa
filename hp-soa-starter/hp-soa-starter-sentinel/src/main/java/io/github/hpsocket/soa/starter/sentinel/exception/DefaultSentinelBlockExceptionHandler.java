package io.github.hpsocket.soa.starter.sentinel.exception;

import java.io.IOException;
import java.io.PrintWriter;

import org.springframework.http.HttpStatus;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.BlockExceptionHandler;
import com.alibaba.csp.sentinel.slots.block.AbstractRule;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson2.JSON;

import io.github.hpsocket.soa.framework.core.exception.ServiceException;
import io.github.hpsocket.soa.framework.web.holder.AppConfigHolder;
import io.github.hpsocket.soa.framework.web.model.Response;
import io.github.hpsocket.soa.framework.web.support.WebServerHelper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import static io.github.hpsocket.soa.framework.core.exception.ServiceException.*;

/** <b>Sentinel 限流处理器</b> */
@Slf4j
public class DefaultSentinelBlockExceptionHandler implements BlockExceptionHandler
{

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, BlockException e) throws IOException
    {
        AbstractRule rule   = e.getRule();
        String msg          = String.format("接口繁忙 - %s:%s", AppConfigHolder.getAppName(), (rule != null) ? rule.getResource() : "");
        ServiceException se = wrapUnimportantException(msg, FREQUENCY_LIMIT_ERROR, e);

        logServiceException(log, se, false);

        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType("application/json; charset=utf-8");
        
        try(PrintWriter out = response.getWriter())
        {
            Response<?> resp = createBlockExceptionResponse(se);
            resp.setCostTime(WebServerHelper.calcTimestamp());
            
            out.print(JSON.toJSONString(resp));
            out.flush();
        }
    }
    
    protected Response<?> createBlockExceptionResponse(ServiceException se)
    {
        return new Response<>(se);
    }

}
