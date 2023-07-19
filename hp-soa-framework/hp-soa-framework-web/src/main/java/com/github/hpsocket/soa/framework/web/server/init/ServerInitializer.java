package com.github.hpsocket.soa.framework.web.server.init;

import com.github.hpsocket.soa.framework.core.util.GeneralHelper;
import com.github.hpsocket.soa.framework.core.util.SystemUtil;

public class ServerInitializer
{
	public static final String LOCAL_IP_ADDRESS										= "local.ip.address";
	public static final String LOG4J2_GARBAGE_FREE_THREAD_CONTEXT_MAP				= "log4j2.garbagefreeThreadContextMap";
	public static final String LOG4J2_IS_THREAD_CONTEXT_MAP_INHERITABLE				= "log4j2.isThreadContextMapInheritable";
	public static final String LOG4J2_LAYOUT_JSON_TEMPLATE_LOCATION_INFO_ENABLED	= "log4j.layout.jsonTemplate.locationInfoEnabled";
	
	public static final void initSystemProperties()
	{
		setSystemProperties(LOCAL_IP_ADDRESS, SystemUtil.getAddress());
		setSystemProperties(LOG4J2_GARBAGE_FREE_THREAD_CONTEXT_MAP, Boolean.TRUE);
		setSystemProperties(LOG4J2_IS_THREAD_CONTEXT_MAP_INHERITABLE, Boolean.TRUE);
		setSystemProperties(LOG4J2_LAYOUT_JSON_TEMPLATE_LOCATION_INFO_ENABLED, Boolean.TRUE);
	}
	
	public static final boolean setSystemProperties(String key, Object value)
	{
		return setSystemProperties(key, value, false);
	}
	
	public static final boolean setSystemProperties(String key, Object value, boolean override)
	{
		if(value == null)
		{
			System.getProperties().remove(key);
			return true;
		}
		
		boolean rs = true;
		String realVal = value.toString();
		
		if(override)
			System.setProperty(key, realVal);
		else
		{
			String val = System.getProperty(key);
			
			if(GeneralHelper.isStrEmpty(val))
				System.setProperty(key, realVal);
			else
				rs = false;
		}
		
		return rs;
	}
}
