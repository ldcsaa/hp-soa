package io.github.hpsocket.demo.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
/* default mybatis mapper scan package -> ${hp.soa.data.mysql.mapper-scan.base-package} */
@MapperScan("io.github.hpsocket.demo.kafka.mapper")
public class AppConfig
{
    public static final String DOMAIN_NAME              = "demo.order";
    public static final String CREATE_ORDER_EVENT_NAME  = "createOrder";
    
    public static final String TOPIC_0  = "TOPIC_0";
    public static final String TOPIC_1  = "TOPIC_1";
    public static final String TOPIC_2  = "TOPIC_2";

    public static final String[] TOPICS = {TOPIC_0, TOPIC_1, TOPIC_2};
    
    @Value("${spring.kafka.listener.concurrency:3}")
    private int partitions;
    
    @Bean
    NewTopic topic0()
    {
        return new NewTopic(TOPICS[0], partitions, (short)1);
    }
    
    @Bean
    NewTopic topic1()
    {
        return new NewTopic(TOPICS[1], partitions, (short)1);
    }
    
    @Bean
    NewTopic topic2()
    {
        return new NewTopic(TOPICS[2], partitions, (short)1);
    }

}
