package io.github.hpsocket.soa.starter.websocket.config;

import io.github.hpsocket.soa.framework.web.service.AsyncService;
import io.github.hpsocket.soa.starter.websocket.exception.WebSocketExceptionHandler;
import io.github.hpsocket.soa.starter.websocket.handler.JsonWebSocketMessageHandler;
import io.github.hpsocket.soa.starter.websocket.handler.WebSocketSessionHandlerDecorator;
import io.github.hpsocket.soa.starter.websocket.listener.WebSocketMessageListener;
import io.github.hpsocket.soa.starter.websocket.properties.SoaWebSocketProperties;
import io.github.hpsocket.soa.starter.websocket.interceptor.DefaultLoginUserHandshakeInterceptor;
import io.github.hpsocket.soa.starter.websocket.sender.kafka.KafkaWebSocketMessageConsumer;
import io.github.hpsocket.soa.starter.websocket.sender.kafka.KafkaWebSocketMessageSender;
import io.github.hpsocket.soa.starter.websocket.sender.local.LocalWebSocketMessageSender;
import io.github.hpsocket.soa.starter.websocket.sender.rabbitmq.RabbitMQWebSocketMessageConsumer;
import io.github.hpsocket.soa.starter.websocket.sender.rabbitmq.RabbitMQWebSocketMessageSender;
import io.github.hpsocket.soa.starter.websocket.sender.redis.RedisWebSocketMessageConsumer;
import io.github.hpsocket.soa.starter.websocket.sender.redis.RedisWebSocketMessageSender;
import io.github.hpsocket.soa.starter.websocket.sender.rocketmq.RocketMQWebSocketMessageConsumer;
import io.github.hpsocket.soa.starter.websocket.sender.rocketmq.RocketMQWebSocketMessageSender;
import io.github.hpsocket.soa.starter.websocket.session.WebSocketSessionManager;
import io.github.hpsocket.soa.starter.websocket.session.WebSocketSessionManagerImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.core.RocketMQClientTemplate;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

import java.util.List;

/**
 * <b>WebSocket 配置</b>
 */
@Slf4j
@EnableWebSocket
@AutoConfiguration
@EnableConfigurationProperties(SoaWebSocketProperties.class)
@ConditionalOnProperty(prefix = "hp.soa.websocket", value = "enable", matchIfMissing = true)
public class SoaWebSocketAutoConfiguration
{
    private final SoaWebSocketProperties soaWebSocketProperties;

    public SoaWebSocketAutoConfiguration(SoaWebSocketProperties soaWebSocketProperties)
    {
        this.soaWebSocketProperties = soaWebSocketProperties;
    }

    @Configuration
    @ConditionalOnProperty(prefix = "hp.soa.websocket", name = "enable-message-consumer", havingValue = "true")
    public class WebSocketMessageConsumerConfiguration
    {
        @Bean
        public WebSocketConfigurer webSocketConfigurer(HandshakeInterceptor[] handshakeInterceptors, WebSocketHandler webSocketHandler)
        {
            return (registry) -> registry
                    .addHandler(webSocketHandler, soaWebSocketProperties.getPath())
                    .setAllowedOrigins(soaWebSocketProperties.getAllowedOrigins())
                    .setAllowedOriginPatterns(soaWebSocketProperties.getAllowedOriginPatterns());
        }

        @Bean
        public ServletServerContainerFactoryBean createWebSocketContainer()
        {
            ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
            container.setMaxSessionIdleTimeout(soaWebSocketProperties.getMaxSessionIdleTimeout());
            container.setAsyncSendTimeout(soaWebSocketProperties.getAsyncSendTimeout());
            container.setMaxTextMessageBufferSize(soaWebSocketProperties.getMaxTextMessageBufferSize());
            container.setMaxBinaryMessageBufferSize(soaWebSocketProperties.getMaxBinaryMessageBufferSize());

            return container;
        }

        @Bean
        @ConditionalOnMissingBean(HandshakeInterceptor.class)
        public HandshakeInterceptor handshakeInterceptor()
        {
            return new DefaultLoginUserHandshakeInterceptor();
        }

        @Bean
        @ConditionalOnBean(WebSocketHandler.class)
        public WebSocketHandler webSocketHandler(
                WebSocketSessionManager sessionManager,
                List<? extends WebSocketMessageListener<?>> messageListeners,
                ObjectProvider<AsyncService> asyncService,
                ObjectProvider<WebSocketExceptionHandler> exceptionHandler)
        {
            JsonWebSocketMessageHandler messageHandler = new JsonWebSocketMessageHandler(messageListeners);
            WebSocketSessionHandlerDecorator messageHandlerDecorator = new WebSocketSessionHandlerDecorator(messageHandler, sessionManager, soaWebSocketProperties);
            asyncService.ifUnique(messageHandlerDecorator::setAsyncService);
            exceptionHandler.ifUnique(messageHandlerDecorator::setExceptionHandler);

            return messageHandlerDecorator;
        }

        @Bean
        public WebSocketSessionManager webSocketSessionManager()
        {
            return new WebSocketSessionManagerImpl();
        }

        @Bean
        public LocalWebSocketMessageSender localWebSocketMessageSender(WebSocketSessionManager sessionManager)
        {
            return new LocalWebSocketMessageSender(sessionManager);
        }
    }

    /**
     * Redis 消息发送器 / 消费者
     */
    @Configuration
    @ConditionalOnProperty(prefix = "hp.soa.websocket", name = "sender-type", havingValue = "redis")
    public class RedisWebSocketMessageSenderConfiguration
    {
        @Bean
        @ConditionalOnMissingBean(RedisWebSocketMessageSender.class)
        public RedisWebSocketMessageSender redisWebSocketMessageSender(RedissonClient redissonClient)
        {
            return new RedisWebSocketMessageSender(redissonClient, soaWebSocketProperties.getMessageTopic());
        }

        @Bean
        @ConditionalOnBean(LocalWebSocketMessageSender.class)
        @ConditionalOnMissingBean(RedisWebSocketMessageConsumer.class)
        public RedisWebSocketMessageConsumer redisWebSocketMessageConsumer(LocalWebSocketMessageSender localWebSocketMessageSender, RedissonClient redissonClient)
        {
            return new RedisWebSocketMessageConsumer(localWebSocketMessageSender, redissonClient, soaWebSocketProperties.getMessageTopic());
        }
    }

    /**
     * RocketMQ 消息发送器 / 消费者
     */
    @Configuration
    @ConditionalOnProperty(prefix = "hp.soa.websocket", name = "sender-type", havingValue = "rocketmq")
    public class RocketMQWebSocketMessageSenderConfiguration
    {
        @Bean
        @ConditionalOnMissingBean(RocketMQWebSocketMessageSender.class)
        public RocketMQWebSocketMessageSender rocketMQWebSocketMessageSender(RocketMQClientTemplate rocketMQClientTemplate)
        {
            return new RocketMQWebSocketMessageSender(rocketMQClientTemplate, soaWebSocketProperties.getMessageTopic());
        }

        @Bean
        @ConditionalOnBean(LocalWebSocketMessageSender.class)
        @ConditionalOnMissingBean(RocketMQWebSocketMessageConsumer.class)
        public RocketMQWebSocketMessageConsumer rocketMQWebSocketMessageConsumer(LocalWebSocketMessageSender localWebSocketMessageSender)
        {
            return new RocketMQWebSocketMessageConsumer(localWebSocketMessageSender);
        }
    }

    /**
     * RabbitMQ 消息发送器 / 消费者
     */
    @Configuration
    @ConditionalOnProperty(prefix = "hp.soa.websocket", name = "sender-type", havingValue = "rabbitmq")
    public class RabbitMQWebSocketMessageSenderConfiguration
    {
        @Bean
        @ConditionalOnMissingBean(RabbitMQWebSocketMessageSender.class)
        public RabbitMQWebSocketMessageSender rabbitMQWebSocketMessageSender(RabbitTemplate rabbitTemplate)
        {
            return new RabbitMQWebSocketMessageSender(rabbitTemplate, soaWebSocketProperties.getMessageExchange());
        }

        @Bean
        @ConditionalOnBean(LocalWebSocketMessageSender.class)
        @ConditionalOnMissingBean(RabbitMQWebSocketMessageConsumer.class)
        public RabbitMQWebSocketMessageConsumer rabbitMQWebSocketMessageConsumer(LocalWebSocketMessageSender localWebSocketMessageSender)
        {
            return new RabbitMQWebSocketMessageConsumer(localWebSocketMessageSender);
        }
    }

    /**
     * Kafka 消息发送器 / 消费者
     */
    @Configuration
    @ConditionalOnProperty(prefix = "hp.soa.websocket", name = "sender-type", havingValue = "kafka")
    public class KafkaWebSocketMessageSenderConfiguration
    {
        @Bean
        @ConditionalOnMissingBean(KafkaWebSocketMessageSender.class)
        public KafkaWebSocketMessageSender kafkaWebSocketMessageSender(KafkaTemplate<Object, Object> kafkaTemplate)
        {
            return new KafkaWebSocketMessageSender(kafkaTemplate, soaWebSocketProperties.getMessageTopic());
        }

        @Bean
        @ConditionalOnBean(LocalWebSocketMessageSender.class)
        @ConditionalOnMissingBean(KafkaWebSocketMessageConsumer.class)
        public KafkaWebSocketMessageConsumer kafkaWebSocketMessageConsumer(final LocalWebSocketMessageSender localWebSocketMessageSender)
        {
            return new KafkaWebSocketMessageConsumer(localWebSocketMessageSender);
        }
    }
}
