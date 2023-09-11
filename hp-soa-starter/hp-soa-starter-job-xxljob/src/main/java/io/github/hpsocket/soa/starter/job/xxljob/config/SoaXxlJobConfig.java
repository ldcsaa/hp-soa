
package io.github.hpsocket.soa.starter.job.xxljob.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import io.github.hpsocket.soa.starter.job.xxljob.aspect.XxlJobInspector;
import io.github.hpsocket.soa.starter.job.xxljob.properties.SoaXxlJobProperties;
import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;

/** <b>HP-SOA XxlJob 配置</b> */
@AutoConfiguration
@Import(XxlJobInspector.class)
@ConditionalOnClass(XxlJobSpringExecutor.class)
@EnableConfigurationProperties({SoaXxlJobProperties.class})
@ConditionalOnProperty(name = "xxl.job.enabled", matchIfMissing = true)
public class SoaXxlJobConfig
{
	private SoaXxlJobProperties soaXxlJobProperties;
	
	public SoaXxlJobConfig(SoaXxlJobProperties soaXxlJobProperties)
	{
		this.soaXxlJobProperties = soaXxlJobProperties;
	}
	
	@Bean
	public XxlJobSpringExecutor xxlJobSpringExecutor()
	{
		XxlJobSpringExecutor executor = new XxlJobSpringExecutor();
		
		executor.setAdminAddresses(soaXxlJobProperties.getAdmin().getAddresses());
		executor.setAppname(soaXxlJobProperties.getExecutor().getAppname());
		executor.setAddress(soaXxlJobProperties.getExecutor().getAddress());
		executor.setIp(soaXxlJobProperties.getExecutor().getIp());
		executor.setPort(soaXxlJobProperties.getExecutor().getPort());
		executor.setLogPath(soaXxlJobProperties.getExecutor().getLogPath());
		executor.setLogRetentionDays(soaXxlJobProperties.getExecutor().getLogRetentionDays());
		executor.setAccessToken(soaXxlJobProperties.getAccessToken());

		return executor;
	}

}
