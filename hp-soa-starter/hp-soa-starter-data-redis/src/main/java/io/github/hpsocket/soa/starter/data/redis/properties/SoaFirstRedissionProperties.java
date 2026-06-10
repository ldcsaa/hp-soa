package io.github.hpsocket.soa.starter.data.redis.properties;

import io.github.hpsocket.soa.starter.data.redis.redisson.RedissonProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component("soaFirstRedissionProperties")
@ConfigurationProperties(prefix = "spring.redis.redisson-first")
@ConditionalOnExpression("'${spring.redis.redisson-first.config:}' != '' || '${spring.redis.redisson-first.file:}' != ''")
public class SoaFirstRedissionProperties extends RedissonProperties
{

}
