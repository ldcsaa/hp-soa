package io.github.hpsocket.soa.starter.mqtt.config;

import java.util.List;

import javax.net.SocketFactory;
import javax.net.ssl.HostnameVerifier;

import org.apache.commons.lang3.RandomStringUtils;
import org.eclipse.paho.mqttv5.client.MqttCallback;
import org.eclipse.paho.mqttv5.client.MqttClientPersistence;
import org.eclipse.paho.mqttv5.client.persist.MqttDefaultFilePersistence;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.ObjectProvider;

import io.github.hpsocket.soa.framework.core.util.GeneralHelper;
import io.github.hpsocket.soa.framework.core.util.SystemUtil;
import io.github.hpsocket.soa.framework.util.sstl.SSLUtil;
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
    protected SoaMqttProperties mqttProperties;
    
    public SoaAbstractMqttConfig(SoaMqttProperties mqttProperties)
    {
        this.mqttProperties = mqttProperties;
    }
    
    public MqttClientPersistence mqttClientPersistence()
    {
        String dataDir = mqttProperties.getDataDir();
        
        if(GeneralHelper.isStrEmpty(dataDir))
            dataDir = System.getProperty("user.dir");
        
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
        
        if(customiers != null)
            customiers.forEach((c) -> c.customize(mqttProperties));
        
        parseClientId();
        parseSocketFactory(socketFactoryProvider.getIfUnique(), hostnameVerifierProvider.getIfUnique());
        
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
        
        mqttClient.setTimeToWait(mqttProperties.getTimeToWait());
        mqttClient.setManualAcks(mqttProperties.isManualAcks());
        mqttClient.setCallback(callBack);
        mqttClient.connect(mqttProperties);
        
        return mqttClient;
    }

    public MqttMessagePublisher mqttMessagePublisher(ExtMqttClient mqttClient)
    {
        return new MqttMessagePublisherImpl(mqttClient , mqttProperties);
    }
    
    private void parseSocketFactory(SocketFactory socketFactory, HostnameVerifier hostnameVerifier)
    {
        SocketFactory sf = mqttProperties.getSocketFactory();
        HostnameVerifier hnv = mqttProperties.getSSLHostnameVerifier();
        
        if(sf == null)
        {
            if(socketFactory != null)
                mqttProperties.setSocketFactory(socketFactory);
            else
            {
                if(GeneralHelper.isStrNotEmpty(mqttProperties.getSslCaCertPath()))
                {
                    try
                    {
                        if( GeneralHelper.isStrNotEmpty(mqttProperties.getSslClientCertPath()) &&
                            GeneralHelper.isStrNotEmpty(mqttProperties.getSslClientKeyPath()))
                        {
                            sf = SSLUtil.getSocketFactory(  mqttProperties.getSslCaCertPath(),
                                                            mqttProperties.getSslClientCertPath(),
                                                            mqttProperties.getSslClientKeyPath(),
                                                            mqttProperties.getSslKeyPassword());
                        }
                        else
                        {
                            sf = SSLUtil.getSingleSocketFactory(mqttProperties.getSslCaCertPath());
                        }
                        
                        mqttProperties.setSocketFactory(sf);
                    }
                    catch(Exception e)
                    {
                        throw new BeanCreationException("create socket factory fail: " + e.getMessage(), e);
                    }
                }
            }
        }
        
        if(hnv == null && hostnameVerifier != null)
            mqttProperties.setSSLHostnameVerifier(hostnameVerifier);
    }
    
    private void parseClientId()
    {
        String clientId = mqttProperties.getClientId();
        
        if(GeneralHelper.isStrEmpty(clientId))
            clientId = RandomStringUtils.random(16, true, true);
        else
        {
            final String PH_ADDR = "%A";
            final String PH_PID  = "%P";
            final String PH_RAND = "%R";
            
            if(clientId.indexOf(PH_ADDR) >= 0)
                clientId = clientId.replaceAll(PH_ADDR, SystemUtil.getAddress());
            if(clientId.indexOf(PH_PID) >= 0)
                clientId = clientId.replaceAll(PH_PID, SystemUtil.getPid());
            if(clientId.indexOf(PH_RAND) >= 0)
                clientId = clientId.replaceAll(PH_RAND, RandomStringUtils.random(16, true, true));
        }
        
        mqttProperties.setClientId(clientId);
    }

    private MqttMessageListener parseMessageListener(ObjectProvider<MqttMessageListener> messageListenerProvider)
    {
        MqttMessageListener listener = messageListenerProvider.getIfUnique();
        
        if(listener == null && GeneralHelper.isNotNullOrEmpty(mqttProperties.getSubscribes()))
            listener = SpringContextHolder.getBean(MqttMessageListener.class);
        
        return listener;
    }

}
