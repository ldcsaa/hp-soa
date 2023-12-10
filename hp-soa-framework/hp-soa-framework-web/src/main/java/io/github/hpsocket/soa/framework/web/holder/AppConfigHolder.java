
package io.github.hpsocket.soa.framework.web.holder;

import java.util.HashSet;
import java.util.Set;

import io.github.hpsocket.soa.framework.core.util.GeneralHelper;
import io.github.hpsocket.soa.framework.core.util.SystemUtil;
import io.github.hpsocket.soa.framework.web.propertries.IAppProperties;
import io.github.hpsocket.soa.framework.web.propertries.IServletPathsPropertries;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

/** <b>应用程序 Web 基本配置持有者</b> */
public class AppConfigHolder
{
    public static final String REQUEST_PATH_SEPARATOR   = "/";
    public static final String ANT_PATH_WILDCARD        = "/**";
    public static final String LOGIN_PATH               = "/login";
    public static final String FAVICON_PATH             = "/favicon.ico";
    
    private static final PathMatcher PATH_MATCHER       = new AntPathMatcher("/");
    
    private static boolean readOnly;

    private static String appId;
    private static String appName;
    private static String appVersion;
    private static String appOrganization;
    private static String appOwner;
    private static String appAddress;
    private static int appPort;
    
    private static int cookieMaxAge;
    private static boolean cookieSecure;
    private static boolean cookieHttpOnly;
    private static String cookieSameSite;
    private static String servletContextPath;
    private static String springMvcServletPath;
    private static String servletUriPrefix;
    private static String managementEndpointsBasePath;
    private static String springdocApiDocsPath;
    private static String springdocSwaggerUiPath;
    private static String managementEndpointsBaseFullPath;
    private static String springdocApiDocsFullPath;
    private static String springdocSwaggerUiFullPath;
    
    private static Set<String> excludedPaths = new HashSet<>(); 
    
    private static boolean initialized;
    
    public static final void init(IAppProperties appProperties, IServletPathsPropertries servletProperties, int serverPort)
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
                    cookieSecure    = appProperties.isCookieSecure();
                    cookieHttpOnly  = appProperties.isCookieHttpOnly();
                    cookieSameSite  = appProperties.getCookieSameSite();
                    appAddress      = SystemUtil.getAddress();
                    appPort         = serverPort;
                    
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
                    
                    
                    excludedPaths.add(managementEndpointsBaseFullPath + ANT_PATH_WILDCARD);
                    /*
                    excludedPaths.add(springdocApiDocsFullPath + ANT_PATH_WILDCARD);
                    excludedPaths.add(springdocSwaggerUiFullPath + ANT_PATH_WILDCARD);
                    excludedPaths.add(servletUriPrefix + LOGIN_PATH);
                    excludedPaths.add(FAVICON_PATH);
                    */
                    
                    initialized = true;
                }
            }
        }
    }
    
    public static final boolean excludedPath(String path)
    {
        for(String pattern : excludedPaths)
        {
            if(PATH_MATCHER.match(pattern, path))
                return true;
        }
        
        return false;
    }

    public static final boolean isReadOnly()
    {
        return readOnly;
    }

    public static final void setReadOnly(boolean readOnly)
    {
        AppConfigHolder.readOnly = readOnly;
    }

    public static final String getAppId()
    {
        return appId;
    }

    public static final String getAppName()
    {
        return appName;
    }

    public static final String getAppVersion()
    {
        return appVersion;
    }

    public static final String getAppOrganization()
    {
        return appOrganization;
    }

    public static final String getAppOwner()
    {
        return appOwner;
    }

    public static final String getAppAddress()
    {
        return appAddress;
    }
    
    public static final int getAppPort()
    {
        return appPort;
    }

    public static final int getCookieMaxAge()
    {
        return cookieMaxAge;
    }
    
    public static final boolean isCookieSecure()
    {
        return cookieSecure;
    }
    
    public static final boolean isCookieHttpOnly()
    {
        return cookieHttpOnly;
    }
    
    public static final String getCookieSameSite()
    {
        return cookieSameSite;
    }

    public static final String getServletContextPath()
    {
        return servletContextPath;
    }

    public static final String getSpringMvcServletPath()
    {
        return springMvcServletPath;
    }

    public static final String getServletUriPrefix()
    {
        return servletUriPrefix;
    }

    public static final String getManagementEndpointsBasePath()
    {
        return managementEndpointsBasePath;
    }

    public static final String getSpringdocApiDocsPath()
    {
        return springdocApiDocsPath;
    }

    public static final String getSpringdocSwaggerUiPath()
    {
        return springdocSwaggerUiPath;
    }

    public static final String getManagementEndpointsBaseFullPath()
    {
        return managementEndpointsBaseFullPath;
    }

    public static final String getSpringdocApiDocsFullPath()
    {
        return springdocApiDocsFullPath;
    }

    public static final String getSpringdocSwaggerUiFullPath()
    {
        return springdocSwaggerUiFullPath;
    }

    public static final Set<String> getExcludedPaths()
    {
        return excludedPaths;
    }

    public static final boolean isInitialized()
    {
        return initialized;
    }

}
