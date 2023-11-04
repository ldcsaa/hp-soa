package io.github.hpsocket.soa.starter.mqtt.config;

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
    public static final String mqttClientPersistence = "firstMqttClientPersistence";
    public static final String mqttCallback = "firstMqttCallback";
    public static final String mqttClient = "firstMqttClient";
    public static final String mqttMessagePublisher = "firstMqttMessagePublisher";
    public static final String mqttMessageListener = "firstMqttMessageListener";
    public static final String mqttPropertiesCustomizer = "firstMqttPropertiesCustomizer";
    public static final String mqttSocketFactory = "firstMqttSocketFactory";
    public static final String mqttHostnameVerifier = "firstMqttHostnameVerifier";
    
    public SoaFirstMqttConfig(SoaFirstMqttProperties mqttProperties)
    {
        super(mqttProperties);
    }
    
    /** 默认 MQTT 实例之外第一个 MQTT 实例持久化对象（可由应用程序覆盖）*/
    @Override
    @Bean(name = mqttClientPersistence, destroyMethod = "")
    @ConditionalOnMissingBean(name = mqttClientPersistence)
    public MqttClientPersistence mqttClientPersistence()
    {
        return super.mqttClientPersistence();
    }

    /** 默认 MQTT 实例之外第一个 MQTT 实例事件处理器（可由应用程序覆盖）*/
    @Override
    @Bean(name = mqttCallback)
    @ConditionalOnMissingBean(name = mqttCallback)
    public MqttCallback mqttCallback()
    {
        return super.mqttCallback();
    }

    /** 默认 MQTT 实例之外第一个 MQTT 实例客户端对象 */
    @Override
    @Bean(name = mqttClient, destroyMethod = "disconnectAndClose")
    public ExtMqttClient mqttClient(
        @Qualifier(mqttClientPersistence) MqttClientPersistence mqttClientPersistence,
        @Qualifier(mqttMessageListener) ObjectProvider<MqttMessageListener> messageListenerProvider,
        @Qualifier(mqttCallback) ObjectProvider<MqttCallback> mqttCallbackProvider,
        @Qualifier(mqttPropertiesCustomizer) ObjectProvider<MqttPropertiesCustomizer> mqttPropertiesCustomizerProvider,
        @Qualifier(mqttSocketFactory) ObjectProvider<SocketFactory> socketFactoryProvider,
        @Qualifier(mqttHostnameVerifier) ObjectProvider<HostnameVerifier> hostnameVerifierProvider) throws MqttException
    {
        return super.mqttClient(mqttClientPersistence,
                                messageListenerProvider,
                                mqttCallbackProvider,
                                mqttPropertiesCustomizerProvider,
                                socketFactoryProvider,
                                hostnameVerifierProvider);
    }
    
    /** 默认 MQTT 实例之外第一个 MQTT 消息发布器 */
    @Override
    @Bean(name = mqttMessagePublisher)
    public MqttMessagePublisher mqttMessagePublisher(@Qualifier(mqttClient) ExtMqttClient mqttClient)
    {
        return super.mqttMessagePublisher(mqttClient);
    }

}
