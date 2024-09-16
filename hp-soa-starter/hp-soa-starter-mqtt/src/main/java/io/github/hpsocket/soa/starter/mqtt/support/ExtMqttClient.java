package io.github.hpsocket.soa.starter.mqtt.support;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

import org.eclipse.paho.mqttv5.client.IMqttMessageListener;
import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.eclipse.paho.mqttv5.client.MqttClient;
import org.eclipse.paho.mqttv5.client.MqttClientPersistence;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.MqttPersistenceException;
import org.eclipse.paho.mqttv5.common.MqttSecurityException;
import org.eclipse.paho.mqttv5.common.MqttSubscription;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;
import org.eclipse.paho.mqttv5.common.packet.UserProperty;
import org.slf4j.MDC;

import io.github.hpsocket.soa.framework.core.id.IdGenerator;
import io.github.hpsocket.soa.framework.core.mdc.MdcAttr;
import io.github.hpsocket.soa.framework.core.util.GeneralHelper;
import io.github.hpsocket.soa.framework.web.support.WebServerHelper;
import io.github.hpsocket.soa.starter.mqtt.util.MqttConstant;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/** <b>扩展 MQTT Client 对象</b> */
@Slf4j
@Getter
@Setter
public class ExtMqttClient extends MqttClient
{
    private boolean manualAcks;
    private MqttConnectionOptions mqttConnectionOptions;
    
    public ExtMqttClient(String serverURI, String clientId, MqttClientPersistence persistence) throws MqttException
    {
        super(serverURI, clientId, persistence);
    }
    
    public ExtMqttClient(String serverURI, String clientId, MqttClientPersistence persistence, boolean manualAcks) throws MqttException
    {
        this(serverURI, clientId, persistence);
        this.setManualAcks(manualAcks);
    }
    
    public ExtMqttClient(String serverURI, String clientId, MqttClientPersistence persistence, ScheduledExecutorService executorService) throws MqttException
    {
        super(serverURI, clientId, persistence, executorService);
    }
    
    public ExtMqttClient(String serverURI, String clientId, MqttClientPersistence persistence, ScheduledExecutorService executorService, boolean manualAcks) throws MqttException
    {
        this(serverURI, clientId, persistence, executorService);
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
    
    @Override
    public void publish(String topic, byte[] payload, int qos, boolean retained) throws MqttException, MqttPersistenceException
    {
        publish2(topic, payload, qos, retained);
    }
    
    @Override
    public void publish(String topic, MqttMessage message) throws MqttException, MqttPersistenceException
    {
        publish2(topic, message);
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
        injectMdcProperties(message);

        IMqttToken token = aClient.publish(topic, message, null, null);
        token.waitForCompletion(getTimeToWait());
        
        return token;
    }
    
    private void injectMdcProperties(MqttMessage message)
    {
        WebServerHelper.assertAppIsNotReadOnly();
        
        MqttProperties props = message.getProperties();
        
        if(props == null)
        {
            props = new MqttProperties();
            message.setProperties(props);
        }
        
        List<UserProperty> userProps = props.getUserProperties();
        
        if(userProps == null)
        {
            userProps = new ArrayList<>();
            props.setUserProperties(userProps);
        }
        
        String messageId         = null;
        String sourceRequestId   = null;

        for(UserProperty p : userProps)
        {
            String key = p.getKey();
            String val = p.getValue();
            
            if(MqttConstant.HEADER_MSG_ID.equalsIgnoreCase(key))
                messageId = val;
            else if(MqttConstant.HEADER_SOURCE_REQUEST_ID.equalsIgnoreCase(key))
                sourceRequestId = val;
            
            if(GeneralHelper.isStrNotEmpty(messageId) && GeneralHelper.isStrNotEmpty(sourceRequestId))
                break;
        }
        
        if(GeneralHelper.isStrEmpty(messageId))
            userProps.add(new UserProperty(MqttConstant.HEADER_MSG_ID, IdGenerator.nextIdStr()));
        if(GeneralHelper.isStrEmpty(sourceRequestId))
            userProps.add(new UserProperty(MqttConstant.HEADER_SOURCE_REQUEST_ID, MDC.get(MdcAttr.MDC_REQUEST_ID_KEY)));
    }

    @Override
    public void connect() throws MqttSecurityException, MqttException
    {
        connect(mqttConnectionOptions);
    }
    
    public IMqttToken connectWithResult() throws MqttSecurityException, MqttException
    {
        return connectWithResult(mqttConnectionOptions);
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
