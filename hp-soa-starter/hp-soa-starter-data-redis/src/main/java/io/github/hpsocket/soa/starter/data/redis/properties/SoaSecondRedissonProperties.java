package io.github.hpsocket.soa.starter.data.redis.properties;

import io.github.hpsocket.soa.starter.data.redis.redisson.RedissonProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component("soaSecondRedissonProperties")
@ConfigurationProperties(prefix = "spring.redis.redisson-second")
@ConditionalOnExpression("'${spring.redis.redisson-second.config:}' != '' || '${spring.redis.redisson-second.file:}' != ''")
public class SoaSecondRedissonProperties extends RedissonProperties
{

}
