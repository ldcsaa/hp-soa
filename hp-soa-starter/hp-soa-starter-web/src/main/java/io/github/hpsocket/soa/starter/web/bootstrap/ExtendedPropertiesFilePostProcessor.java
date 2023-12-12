package io.github.hpsocket.soa.starter.web.bootstrap;

import java.io.File;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.logging.DeferredLogFactory;
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

import io.github.hpsocket.soa.framework.core.util.GeneralHelper;

/** <b>HP-SOA 扩展属性配置文件加载器</b><br>
 * <ul>
 * <li>默认扩展属性配置文件：{@linkplain #DEFAULT_EXTENDED_PROPERTIES_FILE_PATH}</li>
 * <li>可通过 JVM 启动参数 {@linkplain #EXTENDED_PROPERTIES_FILE_KEY} 指定扩展属性配置文件路径</li>
 * <li>如果扩展属性配置文件不存在则忽略</li>
 * </ul>
 */
public class ExtendedPropertiesFilePostProcessor implements EnvironmentPostProcessor, Ordered
{
    /** 扩展属性配置文件 Key */
    public static final String EXTENDED_PROPERTIES_FILE_KEY          = "hp.soa.extended.properties.file";
    /** 默认扩展属性配置文件 */
    public static final String DEFAULT_EXTENDED_PROPERTIES_FILE_PATH = "/opt/hp-soa/config/extended-config.properties";
    
    private static boolean hasPrintLog;
    
    private static Log logger;

    public ExtendedPropertiesFilePostProcessor(DeferredLogFactory logFactory)
    {
        if(logger == null)
            logger = logFactory.getLog(getClass());
    }
    
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application)
    {
        String filePath = environment.getProperty(EXTENDED_PROPERTIES_FILE_KEY);
        
        if(GeneralHelper.isStrEmpty(filePath))
            filePath = DEFAULT_EXTENDED_PROPERTIES_FILE_PATH;
        
        if(!(new File(filePath).isFile()))
        {
            if(!hasPrintLog)
                logger.warn(String.format("hp-soa ignore extended properties file (File Not Exist) -> '%s'", filePath));
            
            return;
        }

        if(!hasPrintLog)
            logger.info("hp-soa load extended properties file -> " + filePath);

        String resolvedLocation = "file:" + environment.resolveRequiredPlaceholders(filePath);
        
        loadProperties(environment, filePath, resolvedLocation);
        
        if(!hasPrintLog)
            hasPrintLog = true;
    }

    private void loadProperties(ConfigurableEnvironment environment, String filePath, String resolvedLocation)
    {
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
            String msg = String.format("hp-soa load extended properties file fail -> [%s] %s", filePath, e.getMessage());
            
            if(!hasPrintLog)
                logger.error(msg, e);
            
            throw new RuntimeException(msg, e);
        }
    }

    @Override
    public int getOrder()
    {
        return Ordered.HIGHEST_PRECEDENCE + 3;
    }

}
