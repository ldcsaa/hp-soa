package io.github.hpsocket.soa.framework.web.server.init;

import java.io.IOException;
import java.util.Properties;

import org.springframework.core.env.PropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.DefaultPropertySourceFactory;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

import io.github.hpsocket.soa.framework.core.util.GeneralHelper;
import io.github.hpsocket.soa.framework.core.util.SystemUtil;

/** <b>应用程序初始化器</b><br>
 * <ol>
 * <li>加载由 {@linkplain #SYSTEM_PROPERTIES_FILE_KEY} JVM 启动参数指定的系统属性配置文件</li>
 * <li>设置其它系统属性</li>
 * </ol>
 */
public class ServerInitializer
{
	private static final String SYSTEM_PROPERTIES_FILE_KEY							= "hp.soa.system.properties.file";
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
		StandardEnvironment environment = new StandardEnvironment();
        String filePath = environment.getProperty(SYSTEM_PROPERTIES_FILE_KEY);
        
        if(GeneralHelper.isStrEmpty(filePath))
        {
        	System.out.println("hp-soa not uses system properties file");
            return;
        }

        System.out.println("load hp-soa system properties file -> " + filePath);

        String resolvedLocation = "file:" + environment.resolveRequiredPlaceholders(filePath);
        PropertySourceFactory factory = new DefaultPropertySourceFactory();
        Resource resource = new DefaultResourceLoader().getResource(resolvedLocation);
        
        try
        {
        	PropertySource<?> propertySource = factory.createPropertySource(filePath, new EncodedResource(resource));
        	Properties props = (Properties)propertySource.getSource();
        	
        	props.forEach((k, v) -> setSystemProperties((String)k, (String)v));
        }
        catch(IOException e)
        {
        	String msg = String.format("load hp-soa system properties file fail -> [%s] %s", filePath, e.getMessage());
        	
        	System.err.println(msg);
        	e.printStackTrace();
        	
        	System.exit(1);
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
