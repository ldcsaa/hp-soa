package io.github.hpsocket.soa.framework.web.server.init;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.springframework.boot.logging.DeferredLogs;
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
 * <li>加载系统属性配置文件
 *   <ul>
 *     <li>默认系统属性配置文件：{@linkplain #DEFAULT_SYSTEM_PROPERTIES_FILE_PATH}</li>
 *     <li>可通过 JVM 启动参数 {@linkplain #SYSTEM_PROPERTIES_FILE_KEY} 指定系统属性配置文件路径</li>
 *     <li>如果系统属性配置文件不存在则忽略</li>
 *   </ul>
 * </li>
 * <li>设置其它系统属性</li>
 * </ol>
 */
public class ServerInitializer
{
    /** 系统属性配置文件 Key */
    public static final String SYSTEM_PROPERTIES_FILE_KEY                            = "hp.soa.system.properties.file";
    /** 默认系统属性配置文件 */
    public static final String DEFAULT_SYSTEM_PROPERTIES_FILE_PATH                    = "/opt/hp-soa/config/system-config.properties";
    
    private static final String LOCAL_IP_ADDRESS                                    = "local.ip.address";
    private static final String LOG4J2_CONTEXT_SELECTOR                                = "log4j2.contextSelector";
    private static final String LOG4J2_CONTEXT_SELECTOR_VALUE                        = "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector";
    private static final String LOG4J2_GARBAGE_FREE_THREAD_CONTEXT_MAP                = "log4j2.garbagefreeThreadContextMap";
    private static final String LOG4J2_IS_THREAD_CONTEXT_MAP_INHERITABLE            = "log4j2.isThreadContextMapInheritable";
    private static final String LOG4J2_LAYOUT_JSON_TEMPLATE_LOCATION_INFO_ENABLED    = "log4j.layout.jsonTemplate.locationInfoEnabled";
    
    private static final DeferredLogs LOG_FACTORY = new DeferredLogs();
    private static final Log LOGGER = LOG_FACTORY.getLog(ServerInitializer.class);
    
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
            filePath = DEFAULT_SYSTEM_PROPERTIES_FILE_PATH;
        
        if(!(new File(filePath).isFile()))
        {
            LOGGER.warn(String.format("hp-soa ignore system properties file (File Not Exist) -> '%s'", filePath));
            return;
        }

        LOGGER.info("hp-soa load system properties file -> " + filePath);

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
            String msg = String.format("load system properties file fail -> [%s] %s", filePath, e.getMessage());
            
            LOGGER.error(msg, e);
            e.printStackTrace();
            
            System.exit(1);
        }
    }
    
    public static final void switchOverAllLogs()
    {
        LOG_FACTORY.switchOverAll();
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
