
package io.github.hpsocket.soa.framework.web.server.main;

import io.github.hpsocket.soa.framework.web.server.init.ServerInitializer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/** <b>默认应用程序启动器</b><br>
 * 启动应用程序
 */
@SpringBootApplication(exclude = {  DataSourceAutoConfiguration.class,
                                    RedisAutoConfiguration.class,
                                    RedisReactiveAutoConfiguration.class,
                                    RedisRepositoriesAutoConfiguration.class,
                                    MongoAutoConfiguration.class,
                                    KafkaAutoConfiguration.class,
                                    RabbitAutoConfiguration.class
                                })
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
