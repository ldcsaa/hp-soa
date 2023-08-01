
package com.github.hpsocket.soa.starter.nacos.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;

import com.github.hpsocket.soa.starter.nacos.properties.SoaNacosProperties;

@AutoConfiguration
@ConditionalOnBean(SoaNacosProperties.class)
public class SoaNacosConfig
{
	
}
