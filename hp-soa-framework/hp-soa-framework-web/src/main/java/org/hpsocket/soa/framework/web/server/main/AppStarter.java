
package org.hpsocket.soa.framework.web.server.main;

import org.hpsocket.soa.framework.web.server.init.ServerInitializer;
import org.hpsocket.soa.framework.web.support.YamlPropertySourceFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication(exclude = {	SecurityAutoConfiguration.class,
									DataSourceAutoConfiguration.class,
									RedisAutoConfiguration.class,
									RedisRepositoriesAutoConfiguration.class,
									MongoAutoConfiguration.class,
									KafkaAutoConfiguration.class,
									RabbitAutoConfiguration.class	})
@PropertySource(value = {"classpath:config.properties"}, ignoreResourceNotFound=true)
@PropertySource(value = {"classpath:config.yml"}, factory = YamlPropertySourceFactory.class, ignoreResourceNotFound=true)
@ComponentScan(basePackages = {"org.hpsocket.soa"})
public class AppStarter
{
	static
	{
		ServerInitializer.initSystemProperties();
	}
	
	public static void main(String[] args)
	{
		SpringApplication.run(AppStarter.class, args);
	}
}
