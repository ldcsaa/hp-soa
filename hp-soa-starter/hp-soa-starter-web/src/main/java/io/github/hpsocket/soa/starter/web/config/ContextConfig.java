package io.github.hpsocket.soa.starter.web.config;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import io.github.hpsocket.soa.framework.core.util.GeneralHelper;
import io.github.hpsocket.soa.framework.web.holder.AppConfigHolder;
import io.github.hpsocket.soa.framework.web.holder.SpringContextHolder;
import io.github.hpsocket.soa.starter.web.properties.SecurityProperties;
import io.github.hpsocket.soa.starter.web.properties.WebProperties;
import io.github.hpsocket.soa.starter.web.properties.WebProperties.ProxyProperties;

/** <b>HP-SOA Web 上下文配置</b> */
@AutoConfiguration
public class ContextConfig
{
    private static final String SERVER_PORT_KEY = "server.port";

    /** {@linkplain SpringContextHolder} Spring 上下文持有者配置 */
    @Bean(SpringContextHolder.springContextHolderBeanName)
    SpringContextHolder springContextHolder(ApplicationContext applicationContext, WebProperties webProperties, SecurityProperties securityProperties)
    {
        Integer serverPort = GeneralHelper.str2Int(applicationContext.getEnvironment().getProperty(SERVER_PORT_KEY));
        
        if(serverPort == null)
            throw new RuntimeException(String.format("(%s) init fail -> '%s' property is empty or invalid", ContextConfig.class.getSimpleName(), SERVER_PORT_KEY));
            
        AppConfigHolder.init(webProperties, securityProperties, serverPort);
        
        checkProxy(webProperties.getProxy());
        
        return new SpringContextHolder(applicationContext);
    }
    
    private static final void checkProxy(ProxyProperties proxy)
    {
        if(!proxy.isEnabled())
            return;
        
        String scheme = GeneralHelper.safeTrimString(proxy.getScheme()).toLowerCase();
        String host = GeneralHelper.safeTrimString(proxy.getHost());
        String userName = GeneralHelper.safeTrimString(proxy.getUserName());
        String password = GeneralHelper.safeTrimString(proxy.getPassword());
        String nonProxyHosts = GeneralHelper.safeTrimString(proxy.getNonProxyHosts());
        int port = proxy.getPort();
        
        if(GeneralHelper.isStrEmpty(proxy.getHost()) || proxy.getPort() <= 0)
            throw new RuntimeException(String.format("(%s) init fail -> 'hp.soa.web.proxy.host' or 'hp.soa.web.proxy.port' property is empty or invalid", ContextConfig.class.getSimpleName()));
        
        if(GeneralHelper.isStrNotEmpty(scheme)
            && !scheme.equalsIgnoreCase("http")
            && !scheme.equalsIgnoreCase("https")
            && !scheme.equalsIgnoreCase("socks")
            && !scheme.equalsIgnoreCase("sock4")
            && !scheme.equalsIgnoreCase("sock5")
        )
            throw new RuntimeException(String.format("(%s) init fail -> 'hp.soa.web.proxy.scheme' property is invalid", ContextConfig.class.getSimpleName()));
        
        if(scheme.isEmpty() || scheme.startsWith("http"))
        {
            System.setProperty("http.proxySet", "true");
            System.setProperty("http.proxyHost", host);
            System.setProperty("http.proxyPort", String.valueOf(port));
            
            System.setProperty("https.proxySet", "true");
            System.setProperty("https.proxyHost", host);
            System.setProperty("https.proxyPort", String.valueOf(port));

            if(GeneralHelper.isStrNotEmpty(nonProxyHosts))
            {
                System.setProperty("http.nonProxyHosts", nonProxyHosts);
            }                    

            if(GeneralHelper.isStrNotEmpty(userName))
            {
                System.setProperty("http.proxyUserName", userName);
                System.setProperty("https.proxyUserName", userName);

                System.setProperty("http.proxyPassword", password);
                System.setProperty("https.proxyPassword", password);
            }
        }
        else if(scheme.startsWith("sock"))
        {
            System.setProperty("proxySet", "true");
            System.setProperty("socksProxyHost", host);
            System.setProperty("socksProxyPort", String.valueOf(port));
            System.setProperty("socksProxyVersion", String.valueOf(scheme.equals("sock4") ? 4 : 5));
            
            if(GeneralHelper.isStrNotEmpty(userName))
            {
                System.setProperty("java.net.socks.username", userName);
                System.setProperty("java.net.socks.password", password);
                
                Authenticator.setDefault(new Authenticator()
                {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication()
                    {
                        return new PasswordAuthentication(userName, password.toCharArray());
                    }
                });
            }
        }        
    }

}
