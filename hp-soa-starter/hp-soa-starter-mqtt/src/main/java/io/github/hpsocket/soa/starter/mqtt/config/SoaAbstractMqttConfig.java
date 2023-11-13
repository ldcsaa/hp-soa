package io.github.hpsocket.soa.starter.mqtt.config;

import java.util.List;

import javax.net.SocketFactory;
import javax.net.ssl.HostnameVerifier;

import org.eclipse.paho.mqttv5.client.MqttCallback;
import org.eclipse.paho.mqttv5.client.MqttClientPersistence;
import org.eclipse.paho.mqttv5.client.persist.MqttDefaultFilePersistence;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.springframework.beans.factory.ObjectProvider;

import io.github.hpsocket.soa.framework.core.util.GeneralHelper;
import io.github.hpsocket.soa.framework.web.holder.SpringContextHolder;
import io.github.hpsocket.soa.starter.mqtt.properties.SoaMqttProperties;
import io.github.hpsocket.soa.starter.mqtt.service.MqttMessageListener;
import io.github.hpsocket.soa.starter.mqtt.service.MqttMessagePublisher;
import io.github.hpsocket.soa.starter.mqtt.service.MqttPropertiesCustomizer;
import io.github.hpsocket.soa.starter.mqtt.service.impl.MqttMessagePublisherImpl;
import io.github.hpsocket.soa.starter.mqtt.support.DefaultMqttCallback;
import io.github.hpsocket.soa.starter.mqtt.support.ExtMqttClient;

public abstract class SoaAbstractMqttConfig
{
    public static final String DEFAULT_MQTT_CLIENT_PERSISTENCE_DIR = System.getProperty("user.dir");
    
    protected SoaMqttProperties mqttProperties;
    
    public SoaAbstractMqttConfig(SoaMqttProperties mqttProperties)
    {
        this.mqttProperties = mqttProperties;
    }
    
    public MqttClientPersistence mqttClientPersistence()
    {
        String dataDir = mqttProperties.getDataDir();
        
        if(GeneralHelper.isStrEmpty(dataDir))
            dataDir = DEFAULT_MQTT_CLIENT_PERSISTENCE_DIR;
        
        return new MqttDefaultFilePersistence(dataDir);
    }

    public MqttCallback mqttCallback()
    {
        return new DefaultMqttCallback();
    }

    public ExtMqttClient mqttClient(
        MqttClientPersistence mqttClientPersistence,
        ObjectProvider<MqttMessageListener> messageListenerProvider,
        ObjectProvider<MqttCallback> mqttCallbackProvider,
        ObjectProvider<List<MqttPropertiesCustomizer>> mqttPropertiesCustomizerProviders,
        ObjectProvider<SocketFactory> socketFactoryProvider,
        ObjectProvider<HostnameVerifier> hostnameVerifierProvider) throws MqttException
    {
        List<MqttPropertiesCustomizer> customiers = mqttPropertiesCustomizerProviders.getIfUnique();
        SocketFactory socketFactory = socketFactoryProvider.getIfUnique();
        HostnameVerifier hostnameVerifier = hostnameVerifierProvider.getIfUnique();
        
        if(customiers != null)
            customiers.forEach((c) -> c.customize(mqttProperties));
        if(socketFactory != null)
            mqttProperties.setSocketFactory(socketFactory);
        if(hostnameVerifier != null)
            mqttProperties.setSSLHostnameVerifier(hostnameVerifier);
        
        MqttMessageListener listener = parseMessageListener(messageListenerProvider);
        ExtMqttClient mqttClient     = new ExtMqttClient(mqttProperties.getServerURIs()[0], mqttProperties.getClientId(), mqttClientPersistence);
        MqttCallback callBack        = mqttCallbackProvider.getIfUnique();
        
        if(callBack == null)
            callBack = mqttCallback();
        
        if(callBack instanceof DefaultMqttCallback defCallBack)
        {
            defCallBack.setMqttClient(mqttClient);
            defCallBack.setMessageListener(listener);
            defCallBack.setSubscribes(mqttProperties.getSubscribes());
        }
        
        mqttClient.setCallback(callBack);
        mqttClient.setManualAcks(mqttProperties.isManualAcks());
        mqttClient.setTimeToWait(mqttProperties.getTimeToWait());
        mqttClient.setMqttConnectionOptions(mqttProperties);
        
        return mqttClient;
    }

    public MqttMessagePublisher mqttMessagePublisher(ExtMqttClient mqttClient)
    {
        return new MqttMessagePublisherImpl(mqttClient , mqttProperties);
    }
    
    private MqttMessageListener parseMessageListener(ObjectProvider<MqttMessageListener> messageListenerProvider)
    {
        MqttMessageListener listener = messageListenerProvider.getIfUnique();
        
        if(listener == null && GeneralHelper.isNotNullOrEmpty(mqttProperties.getSubscribes()))
            listener = SpringContextHolder.getBean(MqttMessageListener.class);
        
        return listener;
    }

}
