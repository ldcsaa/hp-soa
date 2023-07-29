package com.github.hpsocket.soa.starter.web.processor;

import java.io.IOException;

import org.apache.dubbo.config.spring.context.event.DubboConfigInitEvent;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.logging.DeferredLog;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.DefaultPropertySourceFactory;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;
import org.springframework.stereotype.Component;

import com.github.hpsocket.soa.framework.core.util.GeneralHelper;

@Component
public class ExternalPropertiesFilePostProcessor implements EnvironmentPostProcessor, Ordered, ApplicationListener<DubboConfigInitEvent>
{
	public  static final String EXTERNAL_PROPERTIES_FILE_KEY = "hp.soa.config.file";
	
	private static final DeferredLog logger	= new DeferredLog();
	private static boolean processed		= false;
	
	@Override
	public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application)
	{
		if(processed)
			return;

		processed = true;
		
        String filePath = environment.getProperty(EXTERNAL_PROPERTIES_FILE_KEY);
        
        if(GeneralHelper.isStrEmpty(filePath))
        {
        	logger.info("hp-soa not uses external properties file");
            return;
        }

    	logger.info("hp-soa uses external properties file -> " + filePath);

        String resolvedLocation = "file:" + environment.resolveRequiredPlaceholders(filePath);
        PropertySourceFactory factory = new DefaultPropertySourceFactory();
        Resource resource = new DefaultResourceLoader().getResource(resolvedLocation);
        
        try
        {
            addPropertySource(factory.createPropertySource(filePath, new EncodedResource(resource)),environment);
        }
        catch(IOException e)
        {
        	logger.error("hp-soa load external properties file fail [" + filePath + "] -> " + e.getMessage(), e);
            throw new RuntimeException(e);
        }
	}
	
    private void addPropertySource(PropertySource<?> propertySource,ConfigurableEnvironment environment)
    {
        MutablePropertySources propertySources = environment.getPropertySources();
        propertySources.addAfter(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME, propertySource);
    }

	@Override
	public void onApplicationEvent(DubboConfigInitEvent event)
	{
		logger.replayTo(getClass());
	}

	@Override
	public int getOrder()
	{
		return Ordered.HIGHEST_PRECEDENCE + 3;
	}

}
