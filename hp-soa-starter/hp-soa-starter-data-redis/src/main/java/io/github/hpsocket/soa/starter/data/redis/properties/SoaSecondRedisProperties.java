package io.github.hpsocket.soa.starter.data.redis.properties;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component("soaSecondRedisProperties")
@ConfigurationProperties(prefix = "spring.data.redis-second")
@ConditionalOnExpression("'${spring.data.redis-second.host:}' != '' || '${spring.data.redis-second.url:}' != ''")
public class SoaSecondRedisProperties extends RedisProperties
{

}
