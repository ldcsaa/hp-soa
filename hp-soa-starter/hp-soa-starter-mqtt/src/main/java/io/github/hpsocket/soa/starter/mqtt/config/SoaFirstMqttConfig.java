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
    public SoaFirstMqttConfig(SoaFirstMqttProperties mqttProperties)
    {
        super(mqttProperties);
    }
    
    /** 默认 MQTT 实例之外第一个 MQTT 实例持久化对象（可由应用程序覆盖）*/
    @Override
    @Bean(name = "firstMqttClientPersistence", destroyMethod = "")
    @ConditionalOnMissingBean(name = "firstMqttClientPersistence")
    public MqttClientPersistence mqttClientPersistence()
    {
        return super.mqttClientPersistence();
    }

    /** 默认 MQTT 实例之外第一个 MQTT 实例事件处理器（可由应用程序覆盖）*/
    @Override
    @Bean(name = "firstMqttCallback")
    @ConditionalOnMissingBean(name = "firstMqttCallback")
    public MqttCallback mqttCallback()
    {
        return super.mqttCallback();
    }

    /** 默认 MQTT 实例之外第一个 MQTT 实例客户端对象 */
    @Override
    @Bean(name = "firstMqttClient", destroyMethod = "disconnectAndClose")
    public ExtMqttClient mqttClient(
        @Qualifier("firstMqttClientPersistence") MqttClientPersistence mqttClientPersistence,
        @Qualifier("firstMqttMessageListener") ObjectProvider<MqttMessageListener> messageListenerProvider,
        @Qualifier("firstMqttCallback") ObjectProvider<MqttCallback> mqttCallbackProvider,
        @Qualifier("firstMqttPropertiesCustomizer") ObjectProvider<MqttPropertiesCustomizer> mqttPropertiesCustomizerProvider,
        @Qualifier("firstMqttSocketFactory") ObjectProvider<SocketFactory> socketFactoryProvider,
        @Qualifier("firstMqttHostnameVerifier") ObjectProvider<HostnameVerifier> hostnameVerifierProvider) throws MqttException
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
    @Bean(name = "firstMqttMessagePublisher")
    public MqttMessagePublisher mqttMessagePublisher(@Qualifier("firstMqttClient") ExtMqttClient mqttClient)
    {
        return super.mqttMessagePublisher(mqttClient);
    }

}
