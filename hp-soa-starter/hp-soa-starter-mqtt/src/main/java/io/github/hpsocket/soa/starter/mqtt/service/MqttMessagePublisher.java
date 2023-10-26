package io.github.hpsocket.soa.starter.mqtt.service;

import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.MqttPersistenceException;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;

/** <b>MQTT 消息发布器</b><p> 
 * 由应用注入，发布 MQTT 消息。
 */
public interface MqttMessagePublisher
{
    /** 创建 {@linkplain MqttMessage} 对象 */
    MqttMessage createMqttMessage(byte[] payload);
    /** 创建 {@linkplain MqttMessage} 对象 */
    MqttMessage createMqttMessage(byte[] payload, int qos);
    /** 创建 {@linkplain MqttMessage} 对象 */
    MqttMessage createMqttMessage(byte[] payload, boolean retained);
    /** 创建 {@linkplain MqttMessage} 对象 */
    MqttMessage createMqttMessage(byte[] payload, int qos, boolean retained);
    /** 创建 {@linkplain MqttMessage} 对象 */
    MqttMessage createMqttMessage(byte[] payload, MqttProperties properties);
    /** 创建 {@linkplain MqttMessage} 对象 */
    MqttMessage createMqttMessage(byte[] payload, int qos, MqttProperties properties);
    /** 创建 {@linkplain MqttMessage} 对象 */
    MqttMessage createMqttMessage(byte[] payload, boolean retained, MqttProperties properties);
    /** 创建 {@linkplain MqttMessage} 对象 */
    MqttMessage createMqttMessage(byte[] payload, int qos, boolean retained, MqttProperties properties);
    
    /** 发布消息 */
    IMqttToken publish(String topic, MqttMessage message) throws MqttException, MqttPersistenceException;
    /** 发布消息 */
    IMqttToken publish(String topic, byte[] payload) throws MqttException, MqttPersistenceException;
    /** 发布消息 */
    IMqttToken publish(String topic, byte[] payload, int qos) throws MqttException, MqttPersistenceException;
    /** 发布消息 */
    IMqttToken publish(String topic, byte[] payload, boolean retained) throws MqttException, MqttPersistenceException;
    /** 发布消息 */
    IMqttToken publish(String topic, byte[] payload, int qos, boolean retained) throws MqttException, MqttPersistenceException;
}
