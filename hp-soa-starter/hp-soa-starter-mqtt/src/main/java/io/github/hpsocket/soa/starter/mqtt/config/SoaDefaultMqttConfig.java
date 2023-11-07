package io.github.hpsocket.soa.starter.mqtt.config;

import java.util.List;

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
import org.springframework.context.annotation.Primary;

import io.github.hpsocket.soa.starter.mqtt.properties.SoaDefaultMqttProperties;
import io.github.hpsocket.soa.starter.mqtt.service.MqttMessageListener;
import io.github.hpsocket.soa.starter.mqtt.service.MqttMessagePublisher;
import io.github.hpsocket.soa.starter.mqtt.service.MqttPropertiesCustomizer;
import io.github.hpsocket.soa.starter.mqtt.support.ExtMqttClient;

/** <b>默认 MQTT 配置</b> */
@AutoConfiguration
@ConditionalOnBean(SoaDefaultMqttProperties.class)
public class SoaDefaultMqttConfig extends SoaAbstractMqttConfig
{
    public static final String mqttClientPersistenceBeanName = "mqttClientPersistence";
    public static final String mqttCallbackBeanName = "mqttCallback";
    public static final String mqttClientBeanName = "mqttClient";
    public static final String mqttMessagePublisherBeanName = "mqttMessagePublisher";
    public static final String mqttMessageListenerBeanName = "mqttMessageListener";
    public static final String mqttPropertiesCustomizersBeanName = "mqttPropertiesCustomizers";
    public static final String mqttSocketFactoryBeanName = "mqttSocketFactory";
    public static final String mqttHostnameVerifierBeanName = "mqttHostnameVerifier";
    
    public SoaDefaultMqttConfig(SoaDefaultMqttProperties mqttProperties)
    {
        super(mqttProperties);
    }
    
    /** 默认 MQTT 实例持久化对象（可由应用程序覆盖）*/
    @Primary
    @Override
    @Bean(name = mqttClientPersistenceBeanName, destroyMethod = "")
    @ConditionalOnMissingBean(name = mqttClientPersistenceBeanName)
    public MqttClientPersistence mqttClientPersistence()
    {
        return super.mqttClientPersistence();
    }

    /** 默认 MQTT 实例事件处理器（可由应用程序覆盖）*/
    @Primary
    @Override
    @Bean(name = mqttCallbackBeanName)
    @ConditionalOnMissingBean(name = mqttCallbackBeanName)
    public MqttCallback mqttCallback()
    {
        return super.mqttCallback();
    }

    /** 默认 MQTT 实例客户端对象 */
    @Primary
    @Override
    @Bean(name = mqttClientBeanName, destroyMethod = "disconnectAndClose")
    public ExtMqttClient mqttClient(
        @Qualifier(mqttClientPersistenceBeanName) MqttClientPersistence mqttClientPersistence,
        @Qualifier(mqttMessageListenerBeanName) ObjectProvider<MqttMessageListener> messageListenerProvider,
        @Qualifier(mqttCallbackBeanName) ObjectProvider<MqttCallback> mqttCallbackProvider,
        @Qualifier(mqttPropertiesCustomizersBeanName) ObjectProvider<List<MqttPropertiesCustomizer>> mqttPropertiesCustomizerProviders,
        @Qualifier(mqttSocketFactoryBeanName) ObjectProvider<SocketFactory> socketFactoryProvider,
        @Qualifier(mqttHostnameVerifierBeanName) ObjectProvider<HostnameVerifier> hostnameVerifierProvider) throws MqttException
    {
        return super.mqttClient(mqttClientPersistence,
                                messageListenerProvider,
                                mqttCallbackProvider,
                                mqttPropertiesCustomizerProviders,
                                socketFactoryProvider,
                                hostnameVerifierProvider);
    }

    /** 默认 MQTT 消息发布器 */
    @Primary
    @Override
    @Bean(name = mqttMessagePublisherBeanName)
    public MqttMessagePublisher mqttMessagePublisher(@Qualifier(mqttClientBeanName) ExtMqttClient mqttClient)
    {
        return super.mqttMessagePublisher(mqttClient);
    }
    
}
