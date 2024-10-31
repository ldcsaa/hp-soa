
package io.github.hpsocket.soa.starter.job.xxljob.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;

import io.github.hpsocket.soa.starter.job.xxljob.aspect.XxlJobMdcInspector;
import io.github.hpsocket.soa.starter.job.xxljob.properties.SoaXxlJobProperties;

/** <b>HP-SOA XxlJob 配置</b> */
@AutoConfiguration
@ConditionalOnClass(XxlJobSpringExecutor.class)
@EnableConfigurationProperties({SoaXxlJobProperties.class})
@ConditionalOnProperty(name = "hp.soa.job.xxl.enabled", matchIfMissing = true)
public class SoaXxlJobConfig
{
    private SoaXxlJobProperties soaXxlJobProperties;
    
    public SoaXxlJobConfig(SoaXxlJobProperties soaXxlJobProperties)
    {
        this.soaXxlJobProperties = soaXxlJobProperties;
    }

    @Bean
    XxlJobSpringExecutor xxlJobSpringExecutor()
    {
        XxlJobSpringExecutor executor = new XxlJobSpringExecutor();
        
        executor.setAdminAddresses(soaXxlJobProperties.getAdmin().getAddresses());
        executor.setAccessToken(soaXxlJobProperties.getAdmin().getAccessToken());
        executor.setAppname(soaXxlJobProperties.getExecutor().getAppname());
        executor.setAddress(soaXxlJobProperties.getExecutor().getAddress());
        executor.setIp(soaXxlJobProperties.getExecutor().getIp());
        executor.setPort(soaXxlJobProperties.getExecutor().getPort());
        executor.setLogPath(soaXxlJobProperties.getExecutor().getLogPath());
        executor.setLogRetentionDays(soaXxlJobProperties.getExecutor().getLogRetentionDays());

        return executor;
    }
    
    @Bean
    XxlJobMdcInspector xxlJobMdcInspector()
    {
        return new XxlJobMdcInspector();
    }

}
