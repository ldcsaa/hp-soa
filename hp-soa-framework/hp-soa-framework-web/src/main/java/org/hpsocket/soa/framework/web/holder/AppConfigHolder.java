
package org.hpsocket.soa.framework.web.holder;

import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;
import java.util.StringTokenizer;

import org.hpsocket.soa.framework.web.util.YamlPropertiesLoader;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AppConfigHolder
{
	private static final String[] CONFIG_FILES = {"config.properties", "application.properties", "config.yml", "application.yml"};

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

}
