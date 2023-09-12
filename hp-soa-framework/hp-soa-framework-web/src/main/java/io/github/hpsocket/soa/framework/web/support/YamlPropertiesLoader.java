package io.github.hpsocket.soa.framework.web.support;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.Properties;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.InputStreamResource;
import org.springframework.util.ClassUtils;
import org.springframework.util.ResourceUtils;

/** <b>YAML 属性加载器</b> */
public class YamlPropertiesLoader
{
	public static final Properties loadAllProperties(String resourceName) throws IOException
	{
		return loadAllProperties(resourceName, null);
	}

	public static final Properties loadAllProperties(String resourceName, ClassLoader classLoader) throws IOException
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

}
