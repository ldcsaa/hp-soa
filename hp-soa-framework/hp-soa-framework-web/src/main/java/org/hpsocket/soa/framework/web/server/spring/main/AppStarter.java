
package org.hpsocket.soa.framework.web.server.spring.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication(exclude = {	SecurityAutoConfiguration.class,
									DataSourceAutoConfiguration.class,
									RedisAutoConfiguration.class,
									RedisRepositoriesAutoConfiguration.class,
									RabbitAutoConfiguration.class	})
@ImportResource(locations = {"classpath:applicationContext.xml"})
@PropertySource(value = {"classpath:config.properties"}, ignoreResourceNotFound=true)
@ComponentScan(basePackages = {	"com.lcb.soa.misc.server.spring.config",
								"com.lcb.soa.misc.server.spring.advice",
								"com.lcb.soa.misc.spring",
								"com.lcb.soa.misc.dubbo.listener",
								"com.lcb.soa.misc.common.service",
								"com.lcb.soa.misc.aspect.slave",
								"com.lcb.soa.misc.cache.config",
								"com.lcb.soa.misc.biz.ext"})
public class AppStarter
{
	public static void main(String[] args)
	{
		SpringApplication.run(AppStarter.class, args);
	}
}
