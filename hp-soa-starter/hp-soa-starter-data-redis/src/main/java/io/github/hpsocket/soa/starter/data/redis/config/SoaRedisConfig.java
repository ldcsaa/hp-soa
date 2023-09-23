package io.github.hpsocket.soa.starter.data.redis.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;

/** <b>HP-SOA Redis 公共配置</b> */
@AutoConfiguration
@EnableCaching(order = 0)
@ComponentScan("io.github.hpsocket.soa.starter.data.redis.properties")
public class SoaRedisConfig
{

}
