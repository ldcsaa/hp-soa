package io.github.hpsocket.soa.starter.data.redis.properties;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@Component("soaDefaultRedisProperties")
@ConfigurationProperties(prefix = "spring.data.redis")
@ConditionalOnExpression("'${spring.data.redis.host:}' != '' || '${spring.data.redis.url:}' != ''")
public class SoaDefaultRedisProperties extends RedisProperties
{

}
