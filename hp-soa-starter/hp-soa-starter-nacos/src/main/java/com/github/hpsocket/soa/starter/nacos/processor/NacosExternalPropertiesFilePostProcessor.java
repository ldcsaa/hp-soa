package com.github.hpsocket.soa.starter.nacos.processor;

import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.logging.DeferredLog;
import org.springframework.boot.logging.DeferredLogFactory;
import org.springframework.context.ApplicationEvent;
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

import com.github.hpsocket.soa.framework.core.util.GeneralHelper;

public class NacosExternalPropertiesFilePostProcessor implements EnvironmentPostProcessor, Ordered, ApplicationListener<ApplicationEvent>
{
	public  static final String NACOS_PROPERTIES_FILE_KEY = "nacos.properties.file";
	
	private final DeferredLog logger;
	
	public NacosExternalPropertiesFilePostProcessor(DeferredLogFactory logFactory)
	{
		logger = (DeferredLog)logFactory.getLog(getClass());
	}
	
	@Override
	public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application)
	{
        String filePath = environment.getProperty(NACOS_PROPERTIES_FILE_KEY);
        
        if(GeneralHelper.isStrEmpty(filePath))
        {
        	logger.info("nacos not uses external properties file");
            return;
        }
       
        String resolvedLocation = environment.resolveRequiredPlaceholders(filePath);
        resolvedLocation = "file:"+resolvedLocation;
        PropertySourceFactory factory = new DefaultPropertySourceFactory();
        Resource resource = new DefaultResourceLoader().getResource(resolvedLocation);
        
        try {
            addPropertySource(factory.createPropertySource(filePath, new EncodedResource(resource)),environment);
        }
        catch (IOException e)
        {
        	logger.error("load nacos external properties file fail [" + filePath + "] -> " + e.getMessage(), e);
            throw new RuntimeException(e);
        }
	}
	
    private void addPropertySource(PropertySource<?> propertySource,ConfigurableEnvironment environment)
    {
        MutablePropertySources propertySources = environment.getPropertySources();
        propertySources.addAfter(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME, propertySource);
    }

	@Override
	public void onApplicationEvent(ApplicationEvent event)
	{
		logger.replayTo(getClass());
	}

	@Override
	public int getOrder()
	{
		return Ordered.HIGHEST_PRECEDENCE + 3;
	}

}
