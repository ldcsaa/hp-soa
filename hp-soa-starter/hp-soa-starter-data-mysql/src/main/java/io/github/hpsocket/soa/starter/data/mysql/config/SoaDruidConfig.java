
package io.github.hpsocket.soa.starter.data.mysql.config;

import java.io.IOException;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

import com.alibaba.druid.spring.boot3.autoconfigure.DruidDataSourceAutoConfigure;
import com.alibaba.druid.spring.boot3.autoconfigure.properties.DruidStatProperties;
import com.alibaba.druid.util.Utils;
import io.github.hpsocket.soa.framework.core.util.GeneralHelper;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import lombok.extern.slf4j.Slf4j;

/** <b>HP-SOA Druid 数据库连接池配置</b> */
@Slf4j
@AutoConfiguration
@ConditionalOnWebApplication
@AutoConfigureAfter(DruidDataSourceAutoConfigure.class)
@ConditionalOnProperty(name = "spring.datasource.druid.stat-view-servlet.enabled", havingValue = "true")
public class SoaDruidConfig
{

    /** <b>Druid 监控页面广告屏蔽 {@linkplain Filter} */
    @Bean
    public FilterRegistrationBean<Filter> druidAdFilterRegistrationBean(DruidStatProperties properties)
    {
        DruidStatProperties.StatViewServlet config = properties.getStatViewServlet();
        String pattern = config.getUrlPattern() != null ? config.getUrlPattern() : "/druid/*";
        String commonJsPattern = pattern.replaceAll("\\*", "js/common.js");
        final String filePath = "support/http/resources/js/common.js";

        Filter filter = new Filter()
        {
            private String commonJs;
            
            @Override
            public void init(FilterConfig filterConfig) throws ServletException
            {
                log.info("(DruidAdRemoverFilter) starting up ...");
            }

            @Override
            public void destroy()
            {
                log.info("(DruidAdRemoverFilter) shutted down !");
            }

            @Override
            public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
            {
                chain.doFilter(request, response);
                response.resetBuffer();
                
                if(GeneralHelper.isStrEmpty(commonJs))
                {
                    synchronized(this)
                    {
                        if(GeneralHelper.isStrEmpty(commonJs))
                        {
                            String text = Utils.readFromResource(filePath);
                            commonJs = text.replaceAll("<footer[\\s\\S]*?</footer>", "");
                        }
                    }
                }
                
                response.getWriter().write(commonJs);
            }
        };
        
        FilterRegistrationBean<Filter> registrationBean = new FilterRegistrationBean<>();

        registrationBean.setFilter(filter);
        registrationBean.addUrlPatterns(commonJsPattern);

        return registrationBean;
    }

}
