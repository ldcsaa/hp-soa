
package com.github.hpsocket.soa.framework.web.server.main;

import com.github.hpsocket.soa.framework.web.server.init.ServerInitializer;

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
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication(exclude = {	SecurityAutoConfiguration.class,
									DataSourceAutoConfiguration.class,
									RedisAutoConfiguration.class,
									RedisRepositoriesAutoConfiguration.class,
									MongoAutoConfiguration.class,
									KafkaAutoConfiguration.class,
									RabbitAutoConfiguration.class	})
@EnableAutoConfiguration
@EnableAspectJAutoProxy(exposeProxy = true, proxyTargetClass = true)
@ComponentScan(basePackages = {"${hp.soa.web.component-scan.base-package:}"})
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
