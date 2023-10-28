package io.github.hpsocket.soa.starter.mqtt.service.impl;

import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.MqttPersistenceException;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;

import io.github.hpsocket.soa.starter.mqtt.properties.SoaMqttProperties;
import io.github.hpsocket.soa.starter.mqtt.service.MqttMessagePublisher;
import io.github.hpsocket.soa.starter.mqtt.support.ExtMqttClient;

/** <b>MQTT 消息发布器实现类</b> */
public class MqttMessagePublisherImpl implements MqttMessagePublisher
{
    ExtMqttClient mqttClient;
    SoaMqttProperties mqttProperties;
    
    public MqttMessagePublisherImpl(ExtMqttClient mqttClient, SoaMqttProperties mqttProperties)
    {
        this.mqttClient = mqttClient;
        this.mqttProperties = mqttProperties;
    }

    @Override
    public MqttMessage createMqttMessage(byte[] payload)
    {
        return createMqttMessage(payload, mqttProperties.getPublish().getDefaultQos(), mqttProperties.getPublish().isDefaultRetained(), null);
    }
    
    @Override
    public MqttMessage createMqttMessage(byte[] payload, int qos)
    {
        return createMqttMessage(payload, qos, mqttProperties.getPublish().isDefaultRetained(), null);
    }
    
    @Override
    public MqttMessage createMqttMessage(byte[] payload, boolean retained)
    {
        return createMqttMessage(payload, mqttProperties.getPublish().getDefaultQos(), retained, null);
    }

    @Override
    public MqttMessage createMqttMessage(byte[] payload, int qos, boolean retained)
    {
        return createMqttMessage(payload, qos, retained, null);
    }

    @Override
    public MqttMessage createMqttMessage(byte[] payload, MqttProperties properties)
    {
        return createMqttMessage(payload, mqttProperties.getPublish().getDefaultQos(), mqttProperties.getPublish().isDefaultRetained(), properties);
    }

    @Override
    public MqttMessage createMqttMessage(byte[] payload, int qos, MqttProperties properties)
    {
        return createMqttMessage(payload, qos, mqttProperties.getPublish().isDefaultRetained(), properties);
    }

    @Override
    public MqttMessage createMqttMessage(byte[] payload, boolean retained, MqttProperties properties)
    {
        return createMqttMessage(payload, mqttProperties.getPublish().getDefaultQos(), retained, properties);
    }

    @Override
    public MqttMessage createMqttMessage(byte[] payload, int qos, boolean retained, MqttProperties properties)
    {
        return new MqttMessage(payload, qos, retained, properties);
    }

    @Override
    public IMqttToken publish(String topic, MqttMessage message) throws MqttException, MqttPersistenceException
    {
        return mqttClient.publish2(topic, message);
    }

    @Override
    public IMqttToken publish(String topic, byte[] payload) throws MqttException, MqttPersistenceException
    {
        return publish(topic, payload, mqttProperties.getPublish().getDefaultQos(), mqttProperties.getPublish().isDefaultRetained());
    }

    @Override
    public IMqttToken publish(String topic, byte[] payload, int qos) throws MqttException, MqttPersistenceException
    {
        return publish(topic, payload, qos, mqttProperties.getPublish().isDefaultRetained());
    }

    @Override
    public IMqttToken publish(String topic, byte[] payload, boolean retained) throws MqttException, MqttPersistenceException
    {
        return publish(topic, payload, mqttProperties.getPublish().getDefaultQos(), retained);
    }

    @Override
    public IMqttToken publish(String topic, byte[] payload, int qos, boolean retained) throws MqttException, MqttPersistenceException
    {
        return mqttClient.publish2(topic, payload, qos, retained);
    }

}
