package io.github.hpsocket.soa.starter.data.redis.properties;

import io.github.hpsocket.soa.starter.data.redis.redisson.RedissonProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@Component("soaDefaultRedissonProperties")
@ConfigurationProperties(prefix = "spring.redis.redisson")
@ConditionalOnExpression("'${spring.redis.redisson.config:}' != '' || '${spring.redis.redisson.file:}' != ''")
public class SoaDefaultRedissonProperties extends RedissonProperties
{

}
