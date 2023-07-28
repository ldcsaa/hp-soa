
package com.github.hpsocket.soa.starter.nacos.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;

import com.alibaba.cloud.nacos.NacosConfigAutoConfiguration;

@AutoConfiguration
@ConditionalOnBean(NacosConfigAutoConfiguration.class)
public class NacosConfig
{
	
}
