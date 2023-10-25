package io.github.hpsocket.soa.starter.mqtt.support;

import org.eclipse.paho.mqttv5.client.IMqttMessageListener;
import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.eclipse.paho.mqttv5.client.MqttClient;
import org.eclipse.paho.mqttv5.client.MqttClientPersistence;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.MqttPersistenceException;
import org.eclipse.paho.mqttv5.common.MqttSubscription;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/** <b>扩展 MQTT Client 对象</b> */
@Slf4j
@Getter
public class ExtMqttClient extends MqttClient
{
    private boolean manualAcks;
    
    public ExtMqttClient(String serverURI, String clientId, MqttClientPersistence persistence) throws MqttException
    {
        super(serverURI, clientId, persistence);
    }
    
    public ExtMqttClient(String serverURI, String clientId, MqttClientPersistence persistence, boolean manualAcks) throws MqttException
    {
        this(serverURI, clientId, persistence);
        this.setManualAcks(manualAcks);
    }
    
    @Override
    public void setManualAcks(boolean manualAcks)
    {
        super.setManualAcks(manualAcks);
        this.manualAcks = manualAcks;
    }
    
    public IMqttToken subscribe(MqttSubscription subscription, Integer subscriptionIdentifier) throws MqttException
    {
        return subscribe(subscription, subscriptionIdentifier, null);
    }
    
    public IMqttToken subscribe(MqttSubscription subscription, Integer subscriptionIdentifier, Integer topicAlias) throws MqttException
    {
        return subscribe(new MqttSubscription[] {subscription}, subscriptionIdentifier, topicAlias);
    }
    
    public IMqttToken subscribe(MqttSubscription[] subscriptions, Integer subscriptionIdentifier) throws MqttException
    {
        return subscribe(subscriptions, subscriptionIdentifier, null);
    }
    
    public IMqttToken subscribe(MqttSubscription[] subscriptions, Integer subscriptionIdentifier, Integer topicAlias) throws MqttException
    {
        MqttProperties mqttProperties = new MqttProperties();
        
        if(topicAlias != null)
            mqttProperties.setTopicAlias(topicAlias);
        if(subscriptionIdentifier != null)
            mqttProperties.setSubscriptionIdentifier(subscriptionIdentifier);
        
        return subscribe(subscriptions, mqttProperties);
    }
    
    public IMqttToken subscribe(MqttSubscription subscription, MqttProperties mqttProperties) throws MqttException
    {
        return subscribe(new MqttSubscription[] {subscription}, mqttProperties);
    }
    
    public IMqttToken subscribe(MqttSubscription[] subscriptions, MqttProperties mqttProperties) throws MqttException
    {
        IMqttToken token = aClient.subscribe(subscriptions, null, null, (IMqttMessageListener[])null, mqttProperties);
        token.waitForCompletion(getTimeToWait());
        
        return token;
    }
    
    public IMqttToken publish2(String topic, byte[] payload, int qos, boolean retained) throws MqttException, MqttPersistenceException
    {
        MqttMessage message = new MqttMessage(payload);
        message.setQos(qos);
        message.setRetained(retained);
        
        return publish2(topic, message);
    }

    public IMqttToken publish2(String topic, MqttMessage message) throws MqttException, MqttPersistenceException
    {
        IMqttToken token = aClient.publish(topic, message, null, null);
        token.waitForCompletion(getTimeToWait());
        
        return token;
    }
    
    public void disconnectAndClose()
    {
        String clientId = getClientId();
        
        if(isConnected())
        {
            try
            {
                log.info("({}) mqtt client disconnect", clientId);
                disconnect();
            }
            catch(MqttException e)
            {
                log.error("({}) mqtt client disconnect fail -> {}", clientId, e.getMessage(), e);
            }
        }
        
        try
        {
            log.info("({}) mqtt client close", clientId);
            close();
        }
        catch(Exception e)
        {
            log.error("({}) mqtt client close fail -> {}", clientId, e.getMessage(), e);
        }
    }
    
}
