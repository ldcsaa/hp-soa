
package com.github.hpsocket.soa.framework.web.holder;

import com.github.hpsocket.soa.framework.core.util.GeneralHelper;
import com.github.hpsocket.soa.framework.core.util.SystemUtil;
import com.github.hpsocket.soa.framework.web.propertries.IAppProperties;
import com.github.hpsocket.soa.framework.web.propertries.IServletPropertries;

public class AppConfigHolder
{
	public static final String REQUEST_PATH_SEPARATOR	= "/";

	private static String appId;
	private static String appName;
	private static String appVersion;
	private static String appOrganization;
	private static String appOwner;
	private static String appAddress;
	
	private static int cookieMaxAge;
	private static String servletContextPath;
	private static String springMvcServletPath;
	private static String servletUriPrefix;
	private static String managementEndpointsBasePath;
	private static String managementEndpointsBaseFullPath;
	
	private static boolean initialized;
	
	public static final void init(IAppProperties appProperties, IServletPropertries servletProperties)
	{
		if(!initialized)
		{
			synchronized(AppConfigHolder.class)
			{
				if(!initialized)
				{
					appId			= appProperties.getId();
					appName			= appProperties.getName();
					appVersion		= appProperties.getVersion();
					appOrganization	= appProperties.getOrganization();
					appOwner		= appProperties.getOwner();
					appAddress		= SystemUtil.getAddress();
					
					servletContextPath			= servletProperties.getServletContextPath();
					springMvcServletPath		= servletProperties.getSpringMvcServletPath();
					managementEndpointsBasePath	= servletProperties.getManagementEndpointsBasePath();
					cookieMaxAge				= servletProperties.getCookieMaxAge();
					
					StringBuilder sb = new StringBuilder();;
					
					if(GeneralHelper.isStrNotEmpty(servletContextPath) && !servletContextPath.equals(REQUEST_PATH_SEPARATOR))
						sb.append(servletContextPath);
					if(GeneralHelper.isStrNotEmpty(springMvcServletPath) && !springMvcServletPath.equals(REQUEST_PATH_SEPARATOR))
						sb.append(springMvcServletPath);
					
					servletUriPrefix = sb.toString();
					managementEndpointsBaseFullPath = sb.append(managementEndpointsBasePath).toString();
					
					initialized = true;
				}
				
			}
		}
	}

	public static final String getAppId()
	{
		return appId;
	}

	public static final String getAppName()
	{
		return appName;
	}

	public static final String getAppVersion()
	{
		return appVersion;
	}

	public static final String getAppOrganization()
	{
		return appOrganization;
	}

	public static final String getAppOwner()
	{
		return appOwner;
	}

	public static final String getAppAddress()
	{
		return appAddress;
	}

	public static final int getCookieMaxAge()
	{
		return cookieMaxAge;
	}

	public static final String getServletContextPath()
	{
		return servletContextPath;
	}

	public static final String getSpringMvcServletPath()
	{
		return springMvcServletPath;
	}

	public static final String getServletUriPrefix()
	{
		return servletUriPrefix;
	}

	public static final String getManagementEndpointsBasePath()
	{
		return managementEndpointsBasePath;
	}

	public static final String getManagementEndpointsBaseFullPath()
	{
		return managementEndpointsBaseFullPath;
	}

	public static final boolean isInitialized()
	{
		return initialized;
	}

}
