package com.github.hpsocket.soa.framework.web.propertries;

public interface IServletPropertries
{
	int getCookieMaxAge();
	
	String getServletContextPath();
	String getSpringMvcServletPath();
	String getManagementEndpointsBasePath();
}
