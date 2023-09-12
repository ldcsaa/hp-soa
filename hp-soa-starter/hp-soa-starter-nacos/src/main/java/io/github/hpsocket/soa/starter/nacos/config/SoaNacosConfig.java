
package io.github.hpsocket.soa.starter.nacos.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

/** <b>HP-SOA Nacos 配置</b> */
@AutoConfiguration
@ConditionalOnProperty(name = "spring.cloud.nacos.config.enabled", matchIfMissing = true)
public class SoaNacosConfig
{
	
}
