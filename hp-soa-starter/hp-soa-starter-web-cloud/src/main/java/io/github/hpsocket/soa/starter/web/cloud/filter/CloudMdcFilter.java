    package io.github.hpsocket.soa.starter.web.cloud.filter;

import static io.github.hpsocket.soa.framework.core.mdc.MdcAttr.TRANSFER_MDC_ALL_KEYS;

import java.io.IOException;
import java.util.Arrays;

import org.slf4j.MDC;

import io.github.hpsocket.soa.framework.core.mdc.MdcAttr;
import io.github.hpsocket.soa.framework.web.filter.HttpMdcFilter;
import io.github.hpsocket.soa.starter.web.cloud.support.TraceHelper;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import lombok.extern.slf4j.Slf4j;

/** <b>Spring Cloud HTTP 请求 MDC 过滤器</b><br>
 * 主要功能：
 * <ol>
 * <li>为 HTTP 请求注入调用链跟踪信息</li>
 * <li>创建 HTTP 请求上下文 {@linkplain RequestContext}
 * </ol>
 */
@Slf4j
public class CloudMdcFilter extends HttpMdcFilter implements Filter
{
    public static final String DISPLAY_NAME = CloudMdcFilter.class.getSimpleName();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException
    {
        log.info("({}) starting up ...", DISPLAY_NAME);
    }

    @Override
    public void destroy()
    {
        log.info("({}) shutted down !", DISPLAY_NAME);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
    {
        boolean isEntry = TraceHelper.isEntry();
        
        try
        {
            if(isEntry)
                super.doFilter(request, response, chain);
            else
            {
                TraceHelper.fillMdcAttrByBaggage();
                chain.doFilter(request, response);
            }
        }
        finally
        {
            if(!isEntry)
                Arrays.stream(TRANSFER_MDC_ALL_KEYS).forEach((key) -> MDC.remove(key));
        }
    }
    
    @Override
    protected void processMdcAttr(MdcAttr mdcAttr, ServletRequest request, ServletResponse response)
    {
        TraceHelper.fillBaggageByMdcAttr();
    }
}
