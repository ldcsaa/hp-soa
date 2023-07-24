
package com.github.hpsocket.soa.framework.web.server.main;

import com.github.hpsocket.soa.framework.web.server.init.ServerInitializer;
import com.github.hpsocket.soa.framework.web.support.YamlPropertySourceFactory;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;

@EnableDubbo
@EnableAutoConfiguration
@SpringBootApplication(exclude = {	SecurityAutoConfiguration.class,
									DataSourceAutoConfiguration.class,
									RedisAutoConfiguration.class,
									RedisRepositoriesAutoConfiguration.class,
									MongoAutoConfiguration.class,
									KafkaAutoConfiguration.class,
									RabbitAutoConfiguration.class	})
@ComponentScan(basePackages = {"com.github.hpsocket.soa"})
@ImportResource(locations = {"classpath:config.xml"})
@PropertySource(value = {"classpath:config.properties"}, ignoreResourceNotFound=true)
@PropertySource(value = {"classpath:config.yml"}, factory = YamlPropertySourceFactory.class, ignoreResourceNotFound=true)
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
