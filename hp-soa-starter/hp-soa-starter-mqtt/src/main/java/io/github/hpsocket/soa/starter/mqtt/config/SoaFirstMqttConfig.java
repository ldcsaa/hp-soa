package io.github.hpsocket.soa.starter.mqtt.config;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

import javax.net.SocketFactory;
import javax.net.ssl.HostnameVerifier;

import org.eclipse.paho.mqttv5.client.MqttCallback;
import org.eclipse.paho.mqttv5.client.MqttClientPersistence;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

import io.github.hpsocket.soa.starter.mqtt.properties.SoaFirstMqttProperties;
import io.github.hpsocket.soa.starter.mqtt.service.MqttMessageListener;
import io.github.hpsocket.soa.starter.mqtt.service.MqttMessagePublisher;
import io.github.hpsocket.soa.starter.mqtt.service.MqttPropertiesCustomizer;
import io.github.hpsocket.soa.starter.mqtt.support.ExtMqttClient;

/** <b>默认 MQTT 实例之外第一个 MQTT 配置</b> */
@AutoConfiguration
@ConditionalOnBean(SoaFirstMqttProperties.class)
public class SoaFirstMqttConfig extends SoaAbstractMqttConfig
{
    public static final String mqttClientPersistenceBeanName = "firstMqttClientPersistence";
    public static final String mqttCallbackBeanName = "firstMqttCallback";
    public static final String mqttClientBeanName = "firstMqttClient";
    public static final String mqttMessagePublisherBeanName = "firstMqttMessagePublisher";
    public static final String mqttMessageListenerBeanName = "firstMqttMessageListener";
    public static final String mqttPropertiesCustomizersBeanName = "firstMqttPropertiesCustomizers";
    public static final String mqttExecutorServiceBeanName = "firstMqttExecutorService";
    public static final String mqttSocketFactoryBeanName = "firstMqttSocketFactory";
    public static final String mqttHostnameVerifierBeanName = "firstMqttHostnameVerifier";
    
    public SoaFirstMqttConfig(SoaFirstMqttProperties mqttProperties)
    {
        super(mqttProperties);
    }
    
    /** 默认 MQTT 实例之外第一个 MQTT 实例持久化对象（可由应用程序覆盖）*/
    @Override
    @Bean(name = mqttClientPersistenceBeanName, destroyMethod = "")
    @ConditionalOnMissingBean(name = mqttClientPersistenceBeanName)
    protected MqttClientPersistence mqttClientPersistence()
    {
        return super.mqttClientPersistence();
    }

    /** 默认 MQTT 实例之外第一个 MQTT 实例事件处理器（可由应用程序覆盖）*/
    @Override
    @Bean(name = mqttCallbackBeanName)
    @ConditionalOnMissingBean(name = mqttCallbackBeanName)
    protected MqttCallback mqttCallback()
    {
        return super.mqttCallback();
    }

    /** 默认 MQTT 实例之外第一个 MQTT 实例客户端对象 */
    @Override
    @Bean(name = mqttClientBeanName, destroyMethod = "disconnectAndClose")
    protected ExtMqttClient mqttClient(
        @Qualifier(mqttClientPersistenceBeanName) MqttClientPersistence mqttClientPersistence,
        @Qualifier(mqttMessageListenerBeanName) ObjectProvider<MqttMessageListener> messageListenerProvider,
        @Qualifier(mqttCallbackBeanName) ObjectProvider<MqttCallback> mqttCallbackProvider,
        @Qualifier(mqttPropertiesCustomizersBeanName) ObjectProvider<List<MqttPropertiesCustomizer>> mqttPropertiesCustomizerProviders,
        @Qualifier(mqttExecutorServiceBeanName) ObjectProvider<ScheduledExecutorService> executorServiceProvider,
        @Qualifier(mqttSocketFactoryBeanName) ObjectProvider<SocketFactory> socketFactoryProvider,
        @Qualifier(mqttHostnameVerifierBeanName) ObjectProvider<HostnameVerifier> hostnameVerifierProvider) throws MqttException
    {
        return super.mqttClient(mqttClientPersistence,
                                messageListenerProvider,
                                mqttCallbackProvider,
                                mqttPropertiesCustomizerProviders,
                                executorServiceProvider,
                                socketFactoryProvider,
                                hostnameVerifierProvider);
    }
    
    /** 默认 MQTT 实例之外第一个 MQTT 消息发布器 */
    @Override
    @Bean(name = mqttMessagePublisherBeanName)
    protected MqttMessagePublisher mqttMessagePublisher(@Qualifier(mqttClientBeanName) ExtMqttClient mqttClient)
    {
        return super.mqttMessagePublisher(mqttClient);
    }

}
