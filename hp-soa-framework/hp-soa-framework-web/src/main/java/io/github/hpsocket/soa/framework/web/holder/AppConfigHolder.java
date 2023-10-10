
package io.github.hpsocket.soa.framework.web.holder;

import java.util.HashSet;
import java.util.Set;

import io.github.hpsocket.soa.framework.core.util.GeneralHelper;
import io.github.hpsocket.soa.framework.core.util.SystemUtil;
import io.github.hpsocket.soa.framework.web.propertries.IAppProperties;
import io.github.hpsocket.soa.framework.web.propertries.IServletPathsPropertries;

/** <b>应用程序 Web 基本配置持有者</b> */
public class AppConfigHolder
{
    public static final String REQUEST_PATH_SEPARATOR   = "/";
    public static final String FAVICON_PATH             = "/favicon.ico";
    
    private static boolean readOnly;

    private static String appId;
    private static String appName;
    private static String appVersion;
    private static String appOrganization;
    private static String appOwner;
    private static String appAddress;
    
    private static int cookieMaxAge;
    private static String servletContextPath;
    private static String springMvcServletPath;
    private static String servletUriPrefix;
    private static String managementEndpointsBasePath;
    private static String springdocApiDocsPath;
    private static String springdocSwaggerUiPath;
    private static String managementEndpointsBaseFullPath;
    private static String springdocApiDocsFullPath;
    private static String springdocSwaggerUiFullPath;
    
    private static Set<String> excludedLogPaths = new HashSet<>(); 
    
    private static boolean initialized;
    
    public static final void init(IAppProperties appProperties, IServletPathsPropertries servletProperties)
    {
        if(!initialized)
        {
            synchronized(AppConfigHolder.class)
            {
                if(!initialized)
                {
                    readOnly        = appProperties.isReadOnly();
                    appId           = appProperties.getId();
                    appName         = appProperties.getName();
                    appVersion      = appProperties.getVersion();
                    appOrganization = appProperties.getOrganization();
                    appOwner        = appProperties.getOwner();
                    cookieMaxAge    = appProperties.getCookieMaxAge();
                    appAddress      = SystemUtil.getAddress();
                    
                    servletContextPath          = servletProperties.getServletContextPath();
                    springMvcServletPath        = servletProperties.getSpringMvcServletPath();
                    managementEndpointsBasePath = servletProperties.getManagementEndpointsBasePath();
                    springdocApiDocsPath        = servletProperties.getSpringdocApiDocsPath();
                    springdocSwaggerUiPath      = servletProperties.getSpringdocSwaggerUiPath();
                    
                    StringBuilder sb = new StringBuilder();;
                    
                    if(GeneralHelper.isStrNotEmpty(servletContextPath) && !servletContextPath.equals(REQUEST_PATH_SEPARATOR))
                        sb.append(servletContextPath);
                    if(GeneralHelper.isStrNotEmpty(springMvcServletPath) && !springMvcServletPath.equals(REQUEST_PATH_SEPARATOR))
                        sb.append(springMvcServletPath);
                    
                    servletUriPrefix = sb.toString();
                    managementEndpointsBaseFullPath = servletUriPrefix + managementEndpointsBasePath;
                    springdocApiDocsFullPath        = servletUriPrefix + springdocApiDocsPath;
                    springdocSwaggerUiFullPath      = servletUriPrefix + springdocSwaggerUiPath;
                    
                    
                    excludedLogPaths.add(managementEndpointsBaseFullPath);
                    /*
                    excludedLogPaths.add(springdocApiDocsFullPath);
                    excludedLogPaths.add(springdocSwaggerUiFullPath);
                    excludedLogPaths.add(FAVICON_PATH);
                    */
                    
                    initialized = true;
                }
            }
        }
    }

    public static boolean isReadOnly()
    {
        return readOnly;
    }

    public static void setReadOnly(boolean readOnly)
    {
        AppConfigHolder.readOnly = readOnly;
    }

    public static String getAppId()
    {
        return appId;
    }

    public static String getAppName()
    {
        return appName;
    }

    public static String getAppVersion()
    {
        return appVersion;
    }

    public static String getAppOrganization()
    {
        return appOrganization;
    }

    public static String getAppOwner()
    {
        return appOwner;
    }

    public static String getAppAddress()
    {
        return appAddress;
    }

    public static int getCookieMaxAge()
    {
        return cookieMaxAge;
    }

    public static String getServletContextPath()
    {
        return servletContextPath;
    }

    public static String getSpringMvcServletPath()
    {
        return springMvcServletPath;
    }

    public static String getServletUriPrefix()
    {
        return servletUriPrefix;
    }

    public static String getManagementEndpointsBasePath()
    {
        return managementEndpointsBasePath;
    }

    public static String getSpringdocApiDocsPath()
    {
        return springdocApiDocsPath;
    }

    public static String getSpringdocSwaggerUiPath()
    {
        return springdocSwaggerUiPath;
    }

    public static String getManagementEndpointsBaseFullPath()
    {
        return managementEndpointsBaseFullPath;
    }

    public static String getSpringdocApiDocsFullPath()
    {
        return springdocApiDocsFullPath;
    }

    public static String getSpringdocSwaggerUiFullPath()
    {
        return springdocSwaggerUiFullPath;
    }

    public static Set<String> getExcludedLogPaths()
    {
        return excludedLogPaths;
    }

    public static boolean isInitialized()
    {
        return initialized;
    }

}
