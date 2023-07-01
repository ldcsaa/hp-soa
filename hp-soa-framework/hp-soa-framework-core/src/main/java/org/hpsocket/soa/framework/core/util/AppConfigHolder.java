
package org.hpsocket.soa.framework.core.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Properties;
import java.util.StringTokenizer;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.ResourceUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AppConfigHolder
{
	private static String[] CONFIG_FILES = {"config.properties", "application.properties", "config.yml", "application.yml"};

	private static Properties props = init();

	private static Properties init()
	{
		Properties props = null;

		try
		{
			log.info("load config properties from resource: {}", Arrays.toString(CONFIG_FILES));

			props = new Properties();

			for(int i = 0; i < CONFIG_FILES.length; i++)
			{
				String file = CONFIG_FILES[i];

				if(file.endsWith(".yml") || file.endsWith(".yaml"))
					props.putAll(loadYamlProperties(file));
				else
					props.putAll(PropertiesLoaderUtils.loadAllProperties(file));
			}
		}
		catch(IOException e)
		{
			log.error("load config properties from resource fail", e);
			throw new RuntimeException(e);
		}

		return props;
	}

	private static Properties loadYamlProperties(String resourceName) throws IOException
	{
		return loadYamlProperties(resourceName, null);
	}

	private static Properties loadYamlProperties(String resourceName, ClassLoader classLoader) throws IOException
	{
		Properties props = new Properties();
		YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
		
		if(classLoader == null)
			classLoader = ClassUtils.getDefaultClassLoader();

		Enumeration<URL> urls = (classLoader != null ? classLoader.getResources(resourceName) : ClassLoader.getSystemResources(resourceName));

		while(urls.hasMoreElements())
		{
			URL url = urls.nextElement();

			URLConnection con = url.openConnection();
			ResourceUtils.useCachesIfNecessary(con);
			
			try(InputStream is = con.getInputStream())
			{
				factory.setResources(new InputStreamResource(is, resourceName));
				factory.afterPropertiesSet();
				props.putAll(factory.getObject());
			}
		}

		return props;
	}

	public static final String get(String key)
	{
		return props.getProperty(key);
	}

	public static final String get(String key, String defaultValue)
	{
		return props.getProperty(key, defaultValue);
	}

	public static final String[] gets(String key)
	{
		String vals = props.getProperty(key);

		if(vals == null)
			return null;

		final String DELIMITERR_CHAR = " ,;\t";

		StringTokenizer st = new StringTokenizer(vals, DELIMITERR_CHAR);
		String[] rs = new String[st.countTokens()];
		int i = 0;

		while(st.hasMoreTokens())
			rs[i++] = st.nextToken();

		return rs;
	}

	public static final Properties getAll()
	{
		return props;
	}

}
