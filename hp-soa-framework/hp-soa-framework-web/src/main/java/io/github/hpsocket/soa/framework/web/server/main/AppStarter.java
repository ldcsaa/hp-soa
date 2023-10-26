
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

/** <b>默认应用程序启动器</b> */
/* 排除部分自动配置 */
@SpringBootApplication(exclude = {  DataSourceAutoConfiguration.class,
                                    RedisAutoConfiguration.class,
                                    RedisReactiveAutoConfiguration.class,
                                    RedisRepositoriesAutoConfiguration.class,
                                    MongoAutoConfiguration.class,
                                    KafkaAutoConfiguration.class,
                                    RabbitAutoConfiguration.class
                                })
/* 开启 AspectJ AOP */
@EnableAspectJAutoProxy(exposeProxy = true, proxyTargetClass = true)
/* 扫描 Spring Bean */
@ComponentScan(basePackages = {"${hp.soa.web.component-scan.base-package:}"})
public class AppStarter
{
    static
    {
        /* 调用应用服务初始化器，加载系统属性配置文件 */
        ServerInitializer.initSystemProperties();
    }
    
    /** 应用程序入口方法 */
    public static void main(String[] args)
    {
        SpringApplication.run(AppStarter.class, args);
    }
}
