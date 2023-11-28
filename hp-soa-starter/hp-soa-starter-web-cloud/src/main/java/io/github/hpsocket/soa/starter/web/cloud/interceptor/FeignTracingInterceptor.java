package io.github.hpsocket.soa.starter.web.cloud.interceptor;

import org.springframework.core.Ordered;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import io.github.hpsocket.soa.starter.web.cloud.support.TracingHelper;

/** <b>Feign 请求拦截器</b><p>
 * 注入调用链跟踪信息
 */
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
