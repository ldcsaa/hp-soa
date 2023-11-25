package io.github.hpsocket.soa.starter.web.cloud.interceptor;

import org.springframework.core.Ordered;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import io.github.hpsocket.soa.starter.web.cloud.support.TracingHelper;

public class FeignTracingInterceptor implements RequestInterceptor, Ordered
{

    @Override
    public void apply(RequestTemplate template)
    {
        String tracingInfo = TracingHelper.createTracingInfoHeader();
        template.header(TracingHelper.HEADER_TRACING_INFO, tracingInfo);
    }

    @Override
    public int getOrder()
    {
        return Ordered.LOWEST_PRECEDENCE;
    }

}
