package io.github.hpsocket.soa.starter.web.processor;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

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

import io.github.hpsocket.soa.framework.core.util.GeneralHelper;

/** <b>HP-SOA 外部属性文件加载器</b><br>
 * <ul>
 * <li>加载由 {@linkplain #EXTERNAL_PROPERTIES_FILE_KEY} JVM 启动参数指定的外部属性配置文件</li>
 * </ul>
 */
@Component
public class ExternalPropertiesFilePostProcessor implements EnvironmentPostProcessor, Ordered, ApplicationListener<DubboConfigInitEvent>
{
	public static final String EXTERNAL_PROPERTIES_FILE_KEY = "hp.soa.external.properties.file";
	
	private static final DeferredLog logger	= new DeferredLog();
	private static AtomicBoolean processed	= new AtomicBoolean();
	
	@Override
	public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application)
	{
		if(!processed.compareAndSet(false, true))
			return;
		
        String filePath = environment.getProperty(EXTERNAL_PROPERTIES_FILE_KEY);
        
        if(GeneralHelper.isStrEmpty(filePath))
        {
        	logger.info("hp-soa not uses external properties file");
            return;
        }

    	logger.info("load hp-soa external properties file -> " + filePath);

        String resolvedLocation = "file:" + environment.resolveRequiredPlaceholders(filePath);
        PropertySourceFactory factory = new DefaultPropertySourceFactory();
        Resource resource = new DefaultResourceLoader().getResource(resolvedLocation);
        
        try
        {
        	PropertySource<?> propertySource = factory.createPropertySource(filePath, new EncodedResource(resource));
            MutablePropertySources propertySources = environment.getPropertySources();
            propertySources.addAfter(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME, propertySource);
        }
        catch(IOException e)
        {
        	String msg = String.format("load hp-soa external properties file fail -> [%s] %s", filePath, e.getMessage());
        	
        	logger.error(msg, e);
            throw new RuntimeException(msg, e);
        }
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
