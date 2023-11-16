    package io.github.hpsocket.soa.starter.web.cloud.filter;

import java.io.IOException;

import org.springframework.cloud.sleuth.Tracer;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import io.github.hpsocket.soa.framework.core.mdc.MdcAttr;
import io.github.hpsocket.soa.framework.core.util.GeneralHelper;
import io.github.hpsocket.soa.framework.web.advice.RequestContext;
import io.github.hpsocket.soa.framework.web.model.RequestAttribute;
import io.github.hpsocket.soa.framework.web.support.WebServerHelper;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/** <b>HTTP 请求 MDC 过滤器</b><br>
 * 主要功能：
 * <ol>
 * <li>为 HTTP 请求注入调用链跟踪信息</li>
 * <li>创建HTTP 请求上下文 {@linkplain RequestContext}
 * </ol>
 */
@Slf4j
@Setter
public class SleuthFilter implements Filter
{
    public static final int ORDER           = -50;
    public static final String DISPLAY_NAME = SleuthFilter.class.getSimpleName();
    public static final String URL_PATTERNS = "/*";
    
    private Tracer tracer;
    private String[] headers;
    
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
        boolean getAppId = false;
        
        try
        {
            
        }
        finally
        {
            
        }
    }
    
    private void fillMdcAttr(MdcAttr mdcAttr, RequestAttribute reqAttr)
    {
        if(GeneralHelper.isStrNotEmpty(reqAttr.getClientId()))
            mdcAttr.setClientId(reqAttr.getClientId());
        if(GeneralHelper.isStrNotEmpty(reqAttr.getRequestId()))
            mdcAttr.setRequestId(reqAttr.getRequestId());
        if(GeneralHelper.isStrNotEmpty(reqAttr.getSessionId()))
            mdcAttr.setSessionId(reqAttr.getSessionId());
        if(GeneralHelper.isStrNotEmpty(reqAttr.getAppCode()))
            mdcAttr.setAppCode(reqAttr.getAppCode());
        if(GeneralHelper.isStrNotEmpty(reqAttr.getSrcAppCode()))
            mdcAttr.setSrcAppCode(reqAttr.getSrcAppCode());
        if(GeneralHelper.isStrNotEmpty(reqAttr.getToken()))
            mdcAttr.setToken(reqAttr.getToken());
        if(reqAttr.getUserId() != null)
            mdcAttr.setUserId(reqAttr.getUserId().toString());
        if(reqAttr.getGroupId() != null)
            mdcAttr.setGroupId(reqAttr.getGroupId().toString());
        if(GeneralHelper.isStrNotEmpty(reqAttr.getExtra()))
            mdcAttr.setExtra(reqAttr.getExtra());
    }
}
