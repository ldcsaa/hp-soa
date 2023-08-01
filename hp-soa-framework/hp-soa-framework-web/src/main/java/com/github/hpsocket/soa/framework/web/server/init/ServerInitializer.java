package com.github.hpsocket.soa.framework.web.server.init;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Properties;

import com.github.hpsocket.soa.framework.core.util.GeneralHelper;
import com.github.hpsocket.soa.framework.core.util.SystemUtil;

public class ServerInitializer
{
	private static final String SYSTEM_PROPERTIES_FILE_KEY							= "hp.soa.system.properties.file";
	private static final String DEFAULT_SYSTEM_PROPERTIES_FILE						= "/data/hp-soa/config/system-config.properties";
	private static final String LOCAL_IP_ADDRESS									= "local.ip.address";
	private static final String LOG4J2_CONTEXT_SELECTOR								= "log4j2.contextSelector";
	private static final String LOG4J2_CONTEXT_SELECTOR_VALUE						= "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector";
	private static final String LOG4J2_GARBAGE_FREE_THREAD_CONTEXT_MAP				= "log4j2.garbagefreeThreadContextMap";
	private static final String LOG4J2_IS_THREAD_CONTEXT_MAP_INHERITABLE			= "log4j2.isThreadContextMapInheritable";
	private static final String LOG4J2_LAYOUT_JSON_TEMPLATE_LOCATION_INFO_ENABLED	= "log4j.layout.jsonTemplate.locationInfoEnabled";
	
	public static final void initSystemProperties()
	{
		loadExternalSystemProperties();

		setSystemProperties(LOCAL_IP_ADDRESS, SystemUtil.getAddress());
		setSystemProperties(LOG4J2_CONTEXT_SELECTOR, LOG4J2_CONTEXT_SELECTOR_VALUE);
		setSystemProperties(LOG4J2_GARBAGE_FREE_THREAD_CONTEXT_MAP, Boolean.TRUE);
		setSystemProperties(LOG4J2_IS_THREAD_CONTEXT_MAP_INHERITABLE, Boolean.TRUE);
		setSystemProperties(LOG4J2_LAYOUT_JSON_TEMPLATE_LOCATION_INFO_ENABLED, Boolean.TRUE);
	}
	
	private static void loadExternalSystemProperties()
	{
		String filePath = System.getProperty(SYSTEM_PROPERTIES_FILE_KEY);
		
		if(GeneralHelper.isStrEmpty(filePath))
		{
			filePath = System.getenv(SYSTEM_PROPERTIES_FILE_KEY);
			
			if(GeneralHelper.isStrEmpty(filePath))
				filePath = DEFAULT_SYSTEM_PROPERTIES_FILE;
		}
		
		File file = new File(filePath);
		
		if(!file.isFile())
			return;
		
		Properties props = new Properties();
		
		try (BufferedReader rd = new BufferedReader(new FileReader(file)))
		{
			props.load(rd);
			props.forEach((k, v) -> setSystemProperties((String)k, (String)v));
		}
		catch(Throwable e)
		{
			throw new RuntimeException(e);
		}
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
