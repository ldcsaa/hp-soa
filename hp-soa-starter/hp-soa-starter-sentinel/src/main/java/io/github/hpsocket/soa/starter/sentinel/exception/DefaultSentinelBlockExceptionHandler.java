package io.github.hpsocket.soa.starter.sentinel.exception;

import java.io.PrintWriter;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.BlockExceptionHandler;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson2.JSON;

import io.github.hpsocket.soa.framework.core.exception.ServiceException;
import io.github.hpsocket.soa.framework.web.model.Response;
import io.github.hpsocket.soa.framework.web.support.WebServerHelper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import static io.github.hpsocket.soa.framework.core.exception.ServiceException.*;

@Slf4j
public class DefaultSentinelBlockExceptionHandler implements BlockExceptionHandler
{

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, BlockException e) throws Exception
    {
        ServiceException se = wrapServiceException(FREQUENCY_LIMIT_EXCEPTION, e);

        logServiceException(log, se, false);

        response.setStatus(FREQUENCY_LIMIT_ERROR);
        response.setContentType("application/json; charset=utf-8");
        
        try(PrintWriter out = response.getWriter())
        {
            Response<?> resp = new Response<>(se);
            resp.setCostTime(WebServerHelper.calcTimestamp());
            
            out.print(JSON.toJSONString(resp));
            out.flush();
        }
    }

}
