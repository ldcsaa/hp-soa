
package io.github.hpsocket.soa.framework.web.server.main;

import io.github.hpsocket.soa.framework.web.server.init.ServerInitializer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.amqp.autoconfigure.RabbitAutoConfiguration;
import org.springframework.boot.data.redis.autoconfigure.DataRedisAutoConfiguration;
import org.springframework.boot.data.redis.autoconfigure.DataRedisReactiveAutoConfiguration;
import org.springframework.boot.data.redis.autoconfigure.DataRedisRepositoriesAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/** <b>默认应用程序启动器</b> */
/* 排除部分自动配置 */
@SpringBootApplication(
                        /* 扫描 Spring Bean */
                        scanBasePackages = {"${hp.soa.web.component-scan.base-package:}"},
                        exclude = { DataSourceAutoConfiguration.class,
                                    DataRedisAutoConfiguration.class,
                                    DataRedisReactiveAutoConfiguration.class,
                                    DataRedisRepositoriesAutoConfiguration.class,
                                    RabbitAutoConfiguration.class
                                  })
/* 开启 AspectJ AOP */
@EnableAspectJAutoProxy(exposeProxy = true, proxyTargetClass = true)
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
