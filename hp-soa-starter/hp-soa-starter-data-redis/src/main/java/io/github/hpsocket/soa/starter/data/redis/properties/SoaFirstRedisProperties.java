package io.github.hpsocket.soa.starter.data.redis.properties;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component("soaFirstRedisProperties")
@ConfigurationProperties(prefix = "spring.data.redis-first")
@ConditionalOnExpression("'${spring.data.redis-first.host:}' != '' || '${spring.data.redis-first.url:}' != ''")
public class SoaFirstRedisProperties extends RedisProperties
{

}
