package com.github.hpsocket.soa.framework.web.filter;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.github.hpsocket.soa.framework.core.mdc.MdcAttr;
import com.github.hpsocket.soa.framework.core.util.GeneralHelper;
import com.github.hpsocket.soa.framework.web.advice.RequestContext;
import com.github.hpsocket.soa.framework.web.holder.AppConfigHolder;
import com.github.hpsocket.soa.framework.web.model.RequestAttribute;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Setter
public class HttpMdcFilter implements Filter
{
	public static final int ORDER = -100;
	public static final String DISPLAY_NAME	  = "Mdc-Filter";
	public static final String URL_PATTERNS	  = "/*";
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException
	{
		log.info("starting up: ({}) ...", DISPLAY_NAME);
	}

	@Override
	public void destroy()
	{
		log.info("shutting down: ({}) OK!", DISPLAY_NAME);
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
	{
		HttpServletRequest req	 = (HttpServletRequest)request;
		HttpServletResponse resp = (HttpServletResponse)response;
		
		MdcAttr mdcAttr = new MdcAttr();
		RequestAttribute reqAttr = RequestContext.parseRequestAttribute();
		
		try
		{			
			setMdcAttr(mdcAttr, reqAttr);
			mdcAttr.putMdc();

			chain.doFilter(req, resp);
		}
		finally
		{
			mdcAttr.removeMdc();
		}
	}
	
	private MdcAttr setMdcAttr(MdcAttr mdcAttr, RequestAttribute reqAttr)
	{
		mdcAttr.setAppId(AppConfigHolder.getAppId());
		mdcAttr.setAppName(AppConfigHolder.getAppName());
		mdcAttr.setServiceId(AppConfigHolder.getAppId());
		mdcAttr.setServiceName(AppConfigHolder.getAppName());
		mdcAttr.setServiceAddr(AppConfigHolder.getAppAddress());
		
		if(GeneralHelper.isStrNotEmpty(AppConfigHolder.getAppOrganization()))
			mdcAttr.setOrganization(AppConfigHolder.getAppOrganization());
		if(GeneralHelper.isStrNotEmpty(AppConfigHolder.getAppOwner()))
			mdcAttr.setOwner(AppConfigHolder.getAppOwner());
		
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

		return mdcAttr;
	}
}
