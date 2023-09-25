
package io.github.hpsocket.soa.starter.web.config;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.alibaba.fastjson2.support.config.FastJsonConfig;
import com.alibaba.fastjson2.support.spring6.http.converter.FastJsonHttpMessageConverter;
import io.github.hpsocket.soa.framework.core.thread.AsyncThreadPoolExecutor;
import io.github.hpsocket.soa.framework.core.util.GeneralHelper;
import io.github.hpsocket.soa.framework.web.filter.HttpMdcFilter;
import io.github.hpsocket.soa.framework.web.holder.AppConfigHolder;
import io.github.hpsocket.soa.framework.web.holder.SpringContextHolder;
import io.github.hpsocket.soa.framework.web.json.FastJsonExcludePropertyFilter;
import io.github.hpsocket.soa.framework.web.listener.ReadOnlyContextRefreshedEventListener;
import io.github.hpsocket.soa.framework.web.listener.ReadOnlyRefreshEventListener;
import io.github.hpsocket.soa.framework.web.propertries.IAsyncProperties;
import io.github.hpsocket.soa.framework.web.service.AsyncService;
import io.github.hpsocket.soa.framework.web.service.impl.AsyncServiceImpl;
import io.github.hpsocket.soa.framework.web.support.WebServerHelper;
import io.github.hpsocket.soa.starter.web.properties.SecurityProperties;
import io.github.hpsocket.soa.starter.web.properties.WebProperties;
import io.github.hpsocket.soa.starter.web.properties.WebProperties.AppProperties;
import io.github.hpsocket.soa.starter.web.properties.WebProperties.ProxyProperties;

/** <b>HP-SOA Web 基础配置</b> */
@AutoConfiguration
@EnableConfigurationProperties({WebProperties.class, SecurityProperties.class})
public class WebConfig implements WebMvcConfigurer
{
    private final WebProperties webProperties;
    private final SecurityProperties securityProperties;
    
    public WebConfig(WebProperties webProperties, SecurityProperties securityProperties)
    {
        AppProperties app = webProperties.getApp();
        
        if(GeneralHelper.isStrEmpty(app.getId()) || GeneralHelper.isStrEmpty(app.getName()))
            throw new RuntimeException(String.format("({}) init fail -> 'hp.soa.web.app.id' or 'hp.soa.web.app.name' property is empty", WebConfig.class.getSimpleName()));
        
        ProxyProperties proxy = webProperties.getProxy();
        
        checkProxy(proxy);

        this.webProperties = webProperties;
        this.securityProperties = securityProperties;
        
        AppConfigHolder.init(webProperties, securityProperties);
    }
    
    /** {@linkplain SpringContextHolder} Spring 上下文持有者配置 */
    @Bean("springContextHolder")
    public SpringContextHolder springContextHolder()
    {
        return new SpringContextHolder();
    }

    /** {@linkplain ReadOnlyContextRefreshedEventListener} 应用程序监听器配置 */
    @Bean("readOnlyContextRefreshedEventListener")
    @DependsOn("springContextHolder")
    public ReadOnlyContextRefreshedEventListener readOnlyContextRefreshedEventListener()
    {
        return new ReadOnlyContextRefreshedEventListener();
    }

    /** {@linkplain ReadOnlyRefreshEventListener} 应用程序监听器配置 */
    @Bean("readOnlyRefreshEventListener")
    @DependsOn("springContextHolder")
    public ReadOnlyRefreshEventListener readOnlyRefreshEventListener()
    {
        return new ReadOnlyRefreshEventListener();
    }

    /** {@linkplain HttpMdcFilter} 过滤器配置 */
    @Bean
    public FilterRegistrationBean<HttpMdcFilter> mdcTracingFilterRegistration()
    {
        FilterRegistrationBean<HttpMdcFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new HttpMdcFilter());
        registration.setName(HttpMdcFilter.DISPLAY_NAME);
        registration.addUrlPatterns(HttpMdcFilter.URL_PATTERNS);
        registration.setOrder(HttpMdcFilter.ORDER);
        registration.setEnabled(true);
        
        return registration;
    }
    
    /** {@linkplain SecurityFilterChain} 安全过滤器链配置 */
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception
    {
        String endpointsWebBasePath = securityProperties.getManagementEndpointsBasePath();
        
        if(GeneralHelper.isStrEmpty(endpointsWebBasePath) || endpointsWebBasePath.equals("/"))
            http.authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests.anyRequest().permitAll());
        else
        {
            String servletContextPath = securityProperties.getServletContextPath();
            String mvcServletPath = securityProperties.getSpringMvcServletPath();
            
            StringBuilder sb = new StringBuilder();
            
            if(GeneralHelper.isStrNotEmpty(servletContextPath) && !servletContextPath.equals("/"))
                sb.append(servletContextPath);
            if(GeneralHelper.isStrNotEmpty(mvcServletPath) && !mvcServletPath.equals("/"))
                sb.append(mvcServletPath);
            
            sb.append(endpointsWebBasePath);
        
            String managementBasePath = sb.append(SecurityProperties.ANY_PATH_PATTERN).toString();
            
            http
            .authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests
                .requestMatchers(AntPathRequestMatcher.antMatcher(managementBasePath)).authenticated()
                .requestMatchers(AntPathRequestMatcher.antMatcher(SecurityProperties.ANY_PATH_PATTERN)).permitAll())
            .formLogin(Customizer.withDefaults())
            .httpBasic(Customizer.withDefaults());
        }
        
        http.csrf((csrf) -> csrf.disable());
        
        return http.build();
    }
    
    /** {@linkplain WebSecurityCustomizer} Web 安全定制器配置 */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer()
    {
       return ((web) -> {});
    }
    
    /** {@linkplain AsyncThreadPoolExecutor} 异步线程池配置 */
    @Bean("asyncThreadPoolExecutor")
    @ConditionalOnMissingBean(name = "asyncThreadPoolExecutor")
    @ConditionalOnProperty(name="hp.soa.web.async.enabled", havingValue="true", matchIfMissing = true)
    public AsyncThreadPoolExecutor asyncThreadPoolExecutor(IAsyncProperties asyncProperties)
    {
        RejectedExecutionHandler rjh  = WebServerHelper.parseRejectedExecutionHandler(asyncProperties.getRejectionPolicy(), "CALLER_RUNS");
        BlockingQueue<Runnable> queue = asyncProperties.getQueueCapacity() == 0 
                                        ? new SynchronousQueue<>() 
                                        : new LinkedBlockingDeque<>(asyncProperties.getQueueCapacity());
        
        AsyncThreadPoolExecutor executor = new AsyncThreadPoolExecutor(
                                            asyncProperties.getCorePoolSize(),
                                            asyncProperties.getMaxPoolSize(),
                                            asyncProperties.getKeepAliveSeconds(),
                                            TimeUnit.SECONDS,
                                            queue,
                                            rjh);
        
        executor.allowCoreThreadTimeOut(asyncProperties.isAllowCoreThreadTimeOut());
        
        return executor;
    }
    
    /** {@linkplain AsyncService} 异步服务配置 */
    @Bean
    @ConditionalOnProperty(name="hp.soa.web.async.enabled", havingValue="true", matchIfMissing = true)
    public AsyncService asyncService(AsyncThreadPoolExecutor asyncThreadPoolExecutor)
    {
        return new AsyncServiceImpl(asyncThreadPoolExecutor);
    }
    
    /** HTTP 请求报文转换器配置 */
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters)
    {
        int i = 0;
        List<MediaType> mediaTypes = null;
        
        for(; i < converters.size(); i++)
        {
            if(converters.get(i) instanceof MappingJackson2HttpMessageConverter jackson2Converter)
            {
                mediaTypes = jackson2Converter.getSupportedMediaTypes();
                break;
            }
        }
        
        if(mediaTypes == null)
            mediaTypes = Arrays.asList(MediaType.APPLICATION_JSON, new MediaType("application", "*+json"));
        
        FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();
        converter.setSupportedMediaTypes(mediaTypes);

        FastJsonConfig config = converter.getFastJsonConfig();
        config.setWriterFeatures(WebServerHelper.JSON_SERIAL_FEATURES_DEFAULT);
        config.setWriterFilters(new FastJsonExcludePropertyFilter());
        config.setJSONB(true);
        
        converters.add(i, converter);
        
        WebMvcConfigurer.super.extendMessageConverters(converters);
    }

    /** 跨域配置 */
    @Override
    public void addCorsMappings(CorsRegistry registry)
    {
        WebProperties.CorsProperties crosProperties = webProperties.getCors();
        
        CorsRegistration mapping = registry.addMapping(crosProperties.getMapping());
        
        mapping
            .allowedOrigins(crosProperties.getAllowedOrigins())
            .allowedHeaders(crosProperties.getAllowedHeaders())
            .allowedMethods(crosProperties.getAllowedMethods())
            .allowCredentials(crosProperties.isAllowCredentials())
            .maxAge(crosProperties.getMaxAge());
        
        String[] exposedHeaders = crosProperties.getExposedHeaders();
        
        if(exposedHeaders != null && exposedHeaders.length > 0 && !GeneralHelper.isStrEmpty(exposedHeaders[0]))
            mapping.exposedHeaders(exposedHeaders);
    }

    private static final void checkProxy(ProxyProperties proxy)
    {
        if(!proxy.isEnabled())
            return;
        
        String lcScheme = GeneralHelper.safeTrimString(proxy.getScheme()).toLowerCase();
        String host = GeneralHelper.safeTrimString(proxy.getHost());
        String userName = GeneralHelper.safeTrimString(proxy.getUserName());
        String password = GeneralHelper.safeTrimString(proxy.getPassword());
        String nonProxyHosts = GeneralHelper.safeTrimString(proxy.getNonProxyHosts());
        int port = proxy.getPort();
        
        if(GeneralHelper.isStrEmpty(proxy.getHost()) || proxy.getPort() <= 0)
            throw new RuntimeException(String.format("({}) init fail -> 'hp.soa.web.proxy.host' or 'hp.soa.web.proxy.port' property is empty or invalid", WebConfig.class.getSimpleName()));
        
        if(GeneralHelper.isStrNotEmpty(lcScheme)
            && !lcScheme.equalsIgnoreCase("http")
            && !lcScheme.equalsIgnoreCase("https")
            && !lcScheme.equalsIgnoreCase("socks")
            && !lcScheme.equalsIgnoreCase("sock4")
            && !lcScheme.equalsIgnoreCase("sock5")
        )
            throw new RuntimeException(String.format("({}) init fail -> 'hp.soa.web.proxy.scheme' property is invalid", WebConfig.class.getSimpleName()));
        
        if(lcScheme.isEmpty() || lcScheme.startsWith("http"))
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
        else if(lcScheme.startsWith("sock"))
        {
            System.setProperty("proxySet", "true");
            System.setProperty("socksProxyHost", host);
            System.setProperty("socksProxyPort", String.valueOf(port));
            System.setProperty("socksProxyVersion", String.valueOf(lcScheme.equals("sock4") ? 4 : 5));
            
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
