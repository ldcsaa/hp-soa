
package org.hpsocket.soa.framework.web.holder;

import org.hpsocket.soa.framework.web.propertries.IAppProperties;
import org.hpsocket.soa.framework.web.propertries.ICookiePropertries;

public class AppConfigHolder
{
	private static String appId;
	private static String appName;
	private static String appVersion;
	private static String appOrganization;
	private static String appOwner;
	private static int cookieMaxAge;
	
	private static boolean initialized;
	
	public static final void init(IAppProperties appProperties, ICookiePropertries cookieProperties)
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
					cookieMaxAge	= cookieProperties.getMaxAge();
					
					initialized		= true;
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

	public static final int getCookieMaxAge()
	{
		return cookieMaxAge;
	}

	public static final boolean isInitialized()
	{
		return initialized;
	}

}
