package io.github.hpsocket.soa.starter.mqtt.service.impl;

import org.eclipse.paho.mqttv5.client.IMqttClient;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.MqttPersistenceException;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;
import org.springframework.beans.factory.annotation.Autowired;

import io.github.hpsocket.soa.starter.mqtt.properties.SoaMqttOptions;
import io.github.hpsocket.soa.starter.mqtt.service.MqttMessagePublisher;

/** <b>MQTT 消息发布器实现类</b> */
public class MqttMessagePublisherImpl implements MqttMessagePublisher
{
    @Autowired
    IMqttClient mqttClient;
    @Autowired
    SoaMqttOptions soaMqttOptions;
    
    @Override
    public MqttMessage createMqttMessage(byte[] payload)
    {
        return createMqttMessage(payload, soaMqttOptions.getPublish().getDefaultQos(), soaMqttOptions.getPublish().isDefaultRetained(), null);
    }
    
    @Override
    public MqttMessage createMqttMessage(byte[] payload, int qos)
    {
        return createMqttMessage(payload, qos, soaMqttOptions.getPublish().isDefaultRetained(), null);
    }
    
    @Override
    public MqttMessage createMqttMessage(byte[] payload, boolean retained)
    {
        return createMqttMessage(payload, soaMqttOptions.getPublish().getDefaultQos(), retained, null);
    }

    @Override
    public MqttMessage createMqttMessage(byte[] payload, int qos, boolean retained)
    {
        return createMqttMessage(payload, qos, retained, null);
    }

    @Override
    public MqttMessage createMqttMessage(byte[] payload, MqttProperties properties)
    {
        return createMqttMessage(payload, soaMqttOptions.getPublish().getDefaultQos(), soaMqttOptions.getPublish().isDefaultRetained(), properties);
    }

    @Override
    public MqttMessage createMqttMessage(byte[] payload, int qos, MqttProperties properties)
    {
        return createMqttMessage(payload, qos, soaMqttOptions.getPublish().isDefaultRetained(), properties);
    }

    @Override
    public MqttMessage createMqttMessage(byte[] payload, boolean retained, MqttProperties properties)
    {
        return createMqttMessage(payload, soaMqttOptions.getPublish().getDefaultQos(), retained, properties);
    }

    @Override
    public MqttMessage createMqttMessage(byte[] payload, int qos, boolean retained, MqttProperties properties)
    {
        return new MqttMessage(payload, qos, retained, properties);
    }

    @Override
    public void publish(String topic, MqttMessage message) throws MqttException, MqttPersistenceException
    {
        mqttClient.publish(topic, message);
    }

    @Override
    public void publish(String topic, byte[] payload) throws MqttException, MqttPersistenceException
    {
        publish(topic, payload, soaMqttOptions.getPublish().getDefaultQos(), soaMqttOptions.getPublish().isDefaultRetained());
    }

    @Override
    public void publish(String topic, byte[] payload, int qos) throws MqttException, MqttPersistenceException
    {
        publish(topic, payload, qos, soaMqttOptions.getPublish().isDefaultRetained());
    }

    @Override
    public void publish(String topic, byte[] payload, boolean retained) throws MqttException, MqttPersistenceException
    {
        publish(topic, payload, soaMqttOptions.getPublish().getDefaultQos(), retained);
    }

    @Override
    public void publish(String topic, byte[] payload, int qos, boolean retained) throws MqttException, MqttPersistenceException
    {
        mqttClient.publish(topic, payload, qos, retained);
    }

}
