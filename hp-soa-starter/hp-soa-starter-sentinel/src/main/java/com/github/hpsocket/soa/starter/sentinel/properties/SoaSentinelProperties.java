package com.github.hpsocket.soa.starter.sentinel.properties;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;


@Component
@ConditionalOnProperty(name = "spring.cloud.sentinel.enabled", matchIfMissing = true)
public class SoaSentinelProperties
{

}
