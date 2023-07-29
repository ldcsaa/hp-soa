
package com.github.hpsocket.soa.starter.sentinel.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;

import com.github.hpsocket.soa.starter.sentinel.properties.SoaSentinelProperties;

@AutoConfiguration
@ConditionalOnBean(SoaSentinelProperties.class)
public class SoaSentinelConfig
{
	
}
