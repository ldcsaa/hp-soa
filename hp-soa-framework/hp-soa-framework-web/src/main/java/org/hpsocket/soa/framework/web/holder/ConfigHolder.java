
package org.hpsocket.soa.framework.web.holder;

import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;
import java.util.StringTokenizer;

import org.hpsocket.soa.framework.web.support.YamlPropertiesLoader;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class ConfigHolder
{
	public static final String[] DEFAULT_CONFIG_FILES = {"config.properties", "application.properties", "config.yml", "application.yml"};

	private static Properties PROPS;

	public static Properties init(String ... configFiles)
	{
		if(PROPS == null)
		{
			synchronized(ConfigHolder.class)
			{
				if(PROPS == null)
				{
					if(configFiles.length == 0)
						configFiles = DEFAULT_CONFIG_FILES;

					log.info("load config properties from resource: {}", Arrays.toString(configFiles));
					
					try
					{
						Properties props = new Properties();
	
						for(int i = 0; i < DEFAULT_CONFIG_FILES.length; i++)
						{
							String file = DEFAULT_CONFIG_FILES[i];
	
							if(file.endsWith(".yml") || file.endsWith(".yaml"))
								props.putAll(YamlPropertiesLoader.loadAllProperties(file));
							else
								props.putAll(PropertiesLoaderUtils.loadAllProperties(file));
						}
						
						PROPS = props;
					}
					catch(IOException e)
					{
						log.error("load config properties from resource fail", e);
						throw new RuntimeException(e);
					}
					
				}
			}
		}

		return PROPS;
	}
	
	public static final void clear()
	{
		if(PROPS != null)
		{
			synchronized(ConfigHolder.class)
			{
				if(PROPS != null)
				{
					PROPS.clear();
					PROPS = null;
				}
			}
		}
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
