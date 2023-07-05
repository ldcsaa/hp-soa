package org.hpsocket.soa.framework.web.filter;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.hpsocket.soa.framework.core.mdc.MdcAttr;
import org.hpsocket.soa.framework.core.util.GeneralHelper;
import org.hpsocket.soa.framework.web.model.RequestAttribute;
import org.hpsocket.soa.framework.web.support.RequestContext;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Setter
public class MdcFilter implements Filter
{
	public static final int ORDER = Integer.MIN_VALUE + 100;
	public static final String DISPLAY_NAME	  = "Mdc-Filter";
	public static final String URL_PATTERNS	  = "/*";
	
	private String serviceId;
	private String serviceName;
	private String serviceAddr;
	private String organization;
	private String owner;
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException
	{
		log.info("starting up: ({}) ...", DISPLAY_NAME);
		
		if(GeneralHelper.isStrEmpty(serviceId) || GeneralHelper.isStrEmpty(serviceName))
			throw new ServletException(String.format("({}) init fail -> 'serviceId' or 'serviceName' property is empty", DISPLAY_NAME));
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
		
		try
		{
			RequestAttribute reqAttr = RequestContext.parseRequestAttribute();
			
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
		mdcAttr.setAppId(serviceName);
		mdcAttr.setAppName(serviceName);
		mdcAttr.setServiceId(serviceName);
		mdcAttr.setServiceName(serviceName);
		mdcAttr.setServiceAddr(serviceAddr);
		
		if(GeneralHelper.isStrNotEmpty(organization))
			mdcAttr.setOrganization(organization);
		if(GeneralHelper.isStrNotEmpty(owner))
			mdcAttr.setOwner(owner);
		
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
		if(GeneralHelper.isStrNotEmpty(reqAttr.getTokenAlias()))
			mdcAttr.setTokenAlias(reqAttr.getTokenAlias());

		return mdcAttr;
	}

}
