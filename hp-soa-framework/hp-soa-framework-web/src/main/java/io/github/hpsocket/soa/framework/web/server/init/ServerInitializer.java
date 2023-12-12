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

/** <b>应用服务初始化器</b><br>
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
    public static final String SYSTEM_PROPERTIES_FILE_KEY           = "hp.soa.system.properties.file";
    /** 默认系统属性配置文件 */
    public static final String DEFAULT_SYSTEM_PROPERTIES_FILE_PATH  = "/opt/hp-soa/config/system-config.properties";
    /** HP-SOA 内部属性配置文件 */
    public static final String HP_SOA_INTERNAL_PROPERTIES_FILE_PATH = "classpath:hp-soa.properties";
    /** 本机 IP 地址 Key */
    public static final String LOCAL_IP_ADDRESS_KEY                 = "local.ip.address";
    
    private static final DeferredLogs LOG_FACTORY = new DeferredLogs();
    private static final Log LOGGER = LOG_FACTORY.getLog(ServerInitializer.class);
    
    public static final void initSystemProperties()
    {
        GeneralHelper.setSystemPropertyIfAbsent(LOCAL_IP_ADDRESS_KEY, SystemUtil.getAddress());

        StandardEnvironment environment = new StandardEnvironment();

        loadInternalSystemProperties(environment);
        loadExternalSystemProperties(environment);
    }
    
    private static void loadInternalSystemProperties(StandardEnvironment environment)
    {
        String filePath = HP_SOA_INTERNAL_PROPERTIES_FILE_PATH;
        
        LOGGER.info("hp-soa load internal properties file -> " + filePath);

        String resolvedLocation = environment.resolveRequiredPlaceholders(filePath);
        
        loadProperties(filePath, resolvedLocation);
    }
    
    private static void loadExternalSystemProperties(StandardEnvironment environment)
    {
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
        
        loadProperties(filePath, resolvedLocation);
    }

    private static void loadProperties(String filePath, String resolvedLocation)
    {
        PropertySourceFactory factory = new DefaultPropertySourceFactory();
        Resource resource = new DefaultResourceLoader().getResource(resolvedLocation);
        
        try
        {
            PropertySource<?> propertySource = factory.createPropertySource(filePath, new EncodedResource(resource));
            Properties props = (Properties)propertySource.getSource();
            
            props.forEach((k, v) -> GeneralHelper.setSystemPropertyIfAbsent((String)k, (String)v));
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
    
}
