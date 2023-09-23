package io.github.hpsocket.soa.starter.data.redis.properties;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import io.github.hpsocket.soa.starter.data.redis.redisson.RedissonProperties;

@Primary
@Component("soaDefaultRedissionProperties")
@ConfigurationProperties(prefix = "spring.redis.redisson")
@ConditionalOnExpression("'${spring.redis.redisson.config:}' != '' || '${spring.redis.redisson.file:}' != ''")
public class SoaDefaultRedissionProperties extends RedissonProperties
{

}
