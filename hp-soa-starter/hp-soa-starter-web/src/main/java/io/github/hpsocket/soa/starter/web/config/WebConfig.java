
package io.github.hpsocket.soa.starter.web.config;

import java.lang.reflect.Field;
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
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.ui.DefaultLoginPageGeneratingFilter;
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
import io.github.hpsocket.soa.framework.web.listener.DynamicLogLevelContextRefreshedEventListener;
import io.github.hpsocket.soa.framework.web.listener.DynamicLogLevelRefreshEventListener;
import io.github.hpsocket.soa.framework.web.listener.ReadOnlyContextRefreshedEventListener;
import io.github.hpsocket.soa.framework.web.listener.ReadOnlyRefreshEventListener;
import io.github.hpsocket.soa.framework.web.propertries.IAsyncProperties;
import io.github.hpsocket.soa.framework.web.service.AsyncService;
import io.github.hpsocket.soa.framework.web.service.impl.AsyncServiceImpl;
import io.github.hpsocket.soa.framework.web.support.WebServerHelper;
import io.github.hpsocket.soa.starter.web.properties.SecurityProperties;
import io.github.hpsocket.soa.starter.web.properties.WebProperties;
import io.github.hpsocket.soa.starter.web.properties.WebProperties.AppProperties;

import static io.github.hpsocket.soa.starter.web.config.ContextConfig.springContextHolderBeanName;

/** <b>HP-SOA Web 基础配置</b> */
@AutoConfiguration
@EnableConfigurationProperties({WebProperties.class, SecurityProperties.class})
public class WebConfig implements WebMvcConfigurer
{
    public static final String readOnlyContextRefreshedEventListenerBeanName = "readOnlyContextRefreshedEventListener";
    public static final String readOnlyRefreshEventListenerBeanName = "readOnlyRefreshEventListener";
    public static final String dynamicLogLevelContextRefreshedEventListenerBeanName = "dynamicLogLevelContextRefreshedEventListener";
    public static final String dynamicLogLevelRefreshEventListenerBeanName = "dynamicLogLevelRefreshEventListener";
    public static final String asyncThreadPoolExecutorBeanName = "asyncThreadPoolExecutor";
    public static final String httpMdcFilterRegistrationBeanName = "httpMdcFilterRegistration";
   
    private final WebProperties webProperties;
    
    public WebConfig(WebProperties webProperties, SpringContextHolder springContextHolder)
    {
        this.webProperties = webProperties;
        
        AppProperties app = webProperties.getApp();
        
        if(GeneralHelper.isStrEmpty(app.getId()) || GeneralHelper.isStrEmpty(app.getName()))
            throw new RuntimeException(String.format("(%s) init fail -> 'hp.soa.web.app.id' or 'hp.soa.web.app.name' property is empty", WebConfig.class.getSimpleName()));        
    }

    /** {@linkplain ReadOnlyContextRefreshedEventListener} 应用程序监听器配置 */
    @DependsOn(springContextHolderBeanName)
    @Bean(readOnlyContextRefreshedEventListenerBeanName)
    ReadOnlyContextRefreshedEventListener readOnlyContextRefreshedEventListener()
    {
        return new ReadOnlyContextRefreshedEventListener();
    }

    /** {@linkplain ReadOnlyRefreshEventListener} 应用程序监听器配置 */
    @RefreshScope
    @DependsOn(springContextHolderBeanName)
    @Bean(readOnlyRefreshEventListenerBeanName)
    ReadOnlyRefreshEventListener readOnlyRefreshEventListener()
    {
        return new ReadOnlyRefreshEventListener();
    }

    /** {@linkplain DynamicLogLevelContextRefreshedEventListener} 应用程序监听器配置 */
    @DependsOn(springContextHolderBeanName)
    @Bean(dynamicLogLevelContextRefreshedEventListenerBeanName)
    DynamicLogLevelContextRefreshedEventListener dynamicLogLevelContextRefreshedEventListener()
    {
        return new DynamicLogLevelContextRefreshedEventListener();
    }

    /** {@linkplain DynamicLogLevelRefreshEventListener} 应用程序监听器配置 */
    @RefreshScope
    @DependsOn(springContextHolderBeanName)
    @Bean(dynamicLogLevelRefreshEventListenerBeanName)
    DynamicLogLevelRefreshEventListener dynamicLogLevelRefreshEventListener()
    {
        return new DynamicLogLevelRefreshEventListener();
    }

    /** {@linkplain HttpMdcFilter} 过滤器配置 */
    @Bean(httpMdcFilterRegistrationBeanName)
    @ConditionalOnMissingBean(name = httpMdcFilterRegistrationBeanName)
    FilterRegistrationBean<HttpMdcFilter> httpMdcFilterRegistration()
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
    @DependsOn(springContextHolderBeanName)
    @ConditionalOnMissingBean(SecurityFilterChain.class)
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception
    {
        String endpointsWebBasePath = AppConfigHolder.getManagementEndpointsBasePath();
        
        if(GeneralHelper.isStrEmpty(endpointsWebBasePath) || endpointsWebBasePath.equals(AppConfigHolder.REQUEST_PATH_SEPARATOR))
            http.authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests.anyRequest().permitAll()).build();
        else
        {
            String mvcServletPath = AppConfigHolder.getSpringMvcServletPath();
            String prefix = (GeneralHelper.isStrNotEmpty(mvcServletPath) && !mvcServletPath.equals(AppConfigHolder.REQUEST_PATH_SEPARATOR)) ? mvcServletPath : "";
            String managementBasePath = prefix + endpointsWebBasePath + AppConfigHolder.ANT_PATH_WILDCARD;
            
            http
            .authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests
                .requestMatchers(AntPathRequestMatcher.antMatcher(managementBasePath)).authenticated()
                .requestMatchers(AntPathRequestMatcher.antMatcher(AppConfigHolder.ANT_PATH_WILDCARD)).permitAll())
            .csrf((csrf) -> csrf.disable())
            .httpBasic(Customizer.withDefaults())
            .formLogin(new Customizer<FormLoginConfigurer<HttpSecurity>>()
            {
                @Override
                public void customize(FormLoginConfigurer<HttpSecurity> cfg)
                {
                    if(GeneralHelper.isStrEmpty(prefix))
                        return;
                    
                    cfg.loginPage(prefix + DefaultLoginPageGeneratingFilter.DEFAULT_LOGIN_PAGE_URL);
                    
                    try
                    {
                        final String FIELD_NAME = "customLoginPage";
                        Field customLoginPage   = AbstractAuthenticationFilterConfigurer.class.getDeclaredField(FIELD_NAME);
                        
                        customLoginPage.setAccessible(true);
                        customLoginPage.set(cfg, false);
                    }
                    catch(Exception e)
                    {
                        throw new RuntimeException("set actuator login page fail", e);
                    }
                }
            });
        }
        
        return http.build();
    }

    /** {@linkplain WebSecurityCustomizer} Web 安全定制器配置 */
    @Bean
    @ConditionalOnMissingBean(WebSecurityCustomizer.class)
    WebSecurityCustomizer webSecurityCustomizer()
    {
       return ((web) -> {});
    }

    /** {@linkplain AsyncThreadPoolExecutor} 异步线程池配置 */
    @Bean(asyncThreadPoolExecutorBeanName)
    @ConditionalOnMissingBean(name = asyncThreadPoolExecutorBeanName)
    @ConditionalOnProperty(name = "hp.soa.web.async.enabled", havingValue = "true", matchIfMissing = true)
    AsyncThreadPoolExecutor asyncThreadPoolExecutor(IAsyncProperties asyncProperties)
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
    @ConditionalOnProperty(name = "hp.soa.web.async.enabled", havingValue = "true", matchIfMissing = true)
    AsyncService asyncService(AsyncThreadPoolExecutor asyncThreadPoolExecutor)
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
        
        String dateTimeFormat = webProperties.getHttp().getDateTimeFormat();
        
        if(GeneralHelper.isStrEmpty(dateTimeFormat))
            dateTimeFormat = WebProperties.HttpProperties.DEFAULT_DATE_TIME_FORMAT;
        
        config.setDateFormat(dateTimeFormat);
        
        converters.add(i, converter);
        
        WebMvcConfigurer.super.extendMessageConverters(converters);
    }

    /** 跨域配置 */
    @Override
    public void addCorsMappings(CorsRegistry registry)
    {
        WebProperties.HttpProperties.CorsProperties crosProperties = webProperties.getHttp().getCors();
        
        CorsRegistration mapping = registry.addMapping(crosProperties.getMapping());
        
        mapping
            .allowedOriginPatterns(crosProperties.getAllowedOrigins())
            .allowedHeaders(crosProperties.getAllowedHeaders())
            .allowedMethods(crosProperties.getAllowedMethods())
            .allowCredentials(crosProperties.isAllowCredentials())
            .maxAge(crosProperties.getMaxAge());
        
        String[] exposedHeaders = crosProperties.getExposedHeaders();
        
        if(exposedHeaders != null && exposedHeaders.length > 0 && !GeneralHelper.isStrEmpty(exposedHeaders[0]))
            mapping.exposedHeaders(exposedHeaders);
    }

}
