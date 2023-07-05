
package org.hpsocket.soa.framework.web.holder;

import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;
import java.util.StringTokenizer;

import org.hpsocket.soa.framework.web.support.YamlPropertiesLoader;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AppConfigHolder
{
	public static final String[] CONFIG_FILES		= {"config.properties", "application.properties", "config.yml", "application.yml"};
	public static final String PROP_SERVICE_ID		= "app.id";
	public static final String PROP_SERVICE_NAME	= "app.name";
	public static final String PROP_ORG_NAME		= "app.organization";
	public static final String PROP_OWNER_NAME		= "app.owner";

	private static final Properties PROPS = init();

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
					props.putAll(YamlPropertiesLoader.loadAllProperties(file));
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

	public static final String get(String key)
	{
		return PROPS.getProperty(key);
	}

	public static final String get(String key, String defaultValue)
	{
		return PROPS.getProperty(key, defaultValue);
	}

	public static final String[] gets(String key)
	{
		String vals = PROPS.getProperty(key);

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
		return PROPS;
	}

	public static final String getServiceId()
	{
		return get(PROP_SERVICE_ID);
	}

	public static final String getServiceName()
	{
		return get(PROP_SERVICE_NAME);
	}

	public static final String getOrganization()
	{
		return get(PROP_ORG_NAME);
	}

	public static final String getOwner()
	{
		return get(PROP_OWNER_NAME);
	}

}
