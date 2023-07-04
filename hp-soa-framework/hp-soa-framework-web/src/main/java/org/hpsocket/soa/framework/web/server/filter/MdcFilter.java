package org.hpsocket.soa.framework.web.server.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hpsocket.soa.framework.core.mdc.MdcAttr;
import org.hpsocket.soa.framework.web.holder.AppConfigHolder;
import org.hpsocket.soa.framework.web.server.RequestContext;
import org.hpsocket.soa.framework.web.server.ServerHelper;
import org.hpsocket.soa.framework.web.server.model.RequestAttribute;
import org.hpsocket.soa.framework.web.server.model.Response;
import org.slf4j.MDC;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import static org.hpsocket.soa.framework.web.server.ServerHelper.*;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@WebFilter	(
				filterName=MdcFilter.SERVER_NAME,
				displayName=MdcFilter.DISPLAY_NAME,
				urlPatterns={MdcFilter.URL_PATTERNS}
			)
public class MdcFilter implements Filter
{
	public static final String SERVER_NAME	= "mdcFilter";
	public static final String DISPLAY_NAME	= "Mdc-Filter";
	public static final String URL_PATTERNS	= "/*";
	
	private Map<String, String> initMdcMap	= new HashMap<>();
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException
	{
		log.info("starting up: ({})", DISPLAY_NAME);
		
		initMdc();
	}

	@Override
	public void destroy()
	{
		log.info("shutting down: ({}) OK!", SERVER_NAME);
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
	{
		HttpServletRequest req	 = (HttpServletRequest)request;
		HttpServletResponse resp = (HttpServletResponse)response;
		
		try
		{
			RequestAttribute reqAttr = RequestContext.parseRequestAttribute();
			MdcAttr mdc = 
			
			putClientAndRequestMDC(reqAttr.getClientId(), reqAttr.getRequestId(), reqAttr.getAppCode(), null, null, reqAttr.getSessionId(), reqAttr.getToken());

			chain.doFilter(req, resp);
		}
		finally
		{
			removeClientAndRequestMDC();
		}
	}

	private void initMdc()
	{
		initMdcMap.put(MDC_SERVICE_ID_KEY, ServerHelper.getAppId());
		initMdcMap.put(MDC_SERVICE_NAME_KEY, ServerHelper.getAppName());
		
	}

}
