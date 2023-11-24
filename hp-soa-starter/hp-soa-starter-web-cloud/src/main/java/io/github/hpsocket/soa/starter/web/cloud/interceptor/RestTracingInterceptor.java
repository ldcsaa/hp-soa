package io.github.hpsocket.soa.starter.web.cloud.interceptor;

import java.io.IOException;

import org.springframework.core.Ordered;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import io.github.hpsocket.soa.starter.web.cloud.support.TracingHelper;

public class RestTracingInterceptor implements ClientHttpRequestInterceptor, Ordered
{

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException
    {
        String tracingInfo = TracingHelper.createTracingInfoHeader();
        request.getHeaders().set(TracingHelper.HEADER_TRACING_INFO, tracingInfo);
        
        return execution.execute(request, body);
    }

    @Override
    public int getOrder()
    {
        return Ordered.LOWEST_PRECEDENCE;
    }

}
