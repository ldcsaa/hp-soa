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
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import io.github.hpsocket.soa.framework.core.util.GeneralHelper;
import io.github.hpsocket.soa.framework.core.util.SystemUtil;
import io.github.hpsocket.soa.framework.util.sstl.SSLUtil;
import io.github.hpsocket.soa.framework.web.support.NumberByteArrayConverter;
import io.github.hpsocket.soa.framework.web.support.StringByteArrayConverter;
import io.github.hpsocket.soa.starter.mqtt.properties.SoaMqttProperties;
import io.github.hpsocket.soa.starter.mqtt.service.MqttMessageListener;
import io.github.hpsocket.soa.starter.mqtt.service.MqttPropertiesCustomizer;
import io.github.hpsocket.soa.starter.mqtt.service.impl.MqttMessagePublisherImpl;
import io.github.hpsocket.soa.starter.mqtt.support.DefaultMqttCallback;
import io.github.hpsocket.soa.starter.mqtt.support.ExtMqttClient;

/** <b>MQTT 配置类</b> */
@AutoConfiguration
@EnableConfigurationProperties(SoaMqttProperties.class)
@Import({StringByteArrayConverter.class, NumberByteArrayConverter.class, MqttMessagePublisherImpl.class})
public class SoaMqttConfig
{
    /** 默认持久化对象（可由应用程序覆盖）*/
    @Bean(destroyMethod = "")
    @ConditionalOnMissingBean(MqttClientPersistence.class)
    public MqttClientPersistence mqttClientPersistence(SoaMqttProperties mqttProperties)
    {
        String dataDir = mqttProperties.getDataDir();
        
        if(GeneralHelper.isStrEmpty(dataDir))
            dataDir = System.getProperty("user.dir");
        
        return new MqttDefaultFilePersistence(dataDir);
    }

    /** 默认 MQTT 事件处理器（可由应用程序覆盖）*/
    @Bean
    @ConditionalOnMissingBean(MqttCallback.class)
    public MqttCallback mqttCallback()
    {
        return new DefaultMqttCallback();
    }

    /** MQTT 客户端对象 */
    @Bean(destroyMethod = "disconnectAndClose")
    public ExtMqttClient mqttClient(
        SoaMqttProperties mqttProperties,
        MqttClientPersistence mqttClientPersistence,
        ObjectProvider<MqttMessageListener> messageListenerProvider,
        ObjectProvider<MqttCallback> mqttCallbackProvider,
        ObjectProvider<List<MqttPropertiesCustomizer>> mqttPropertiesCustomizersProvider,
        ObjectProvider<SocketFactory> socketFactoryProvider,
        ObjectProvider<HostnameVerifier> hostnameVerifierProvider) throws MqttException
    {
        List<MqttPropertiesCustomizer> customiers = mqttPropertiesCustomizersProvider.getIfUnique();
        
        if(customiers != null)
            customiers.forEach((c) -> c.customize(mqttProperties));
        
        parseSocketFactory(mqttProperties, socketFactoryProvider.getIfUnique(), hostnameVerifierProvider.getIfUnique());
        parseClientId(mqttProperties);
        
        ExtMqttClient mqttClient = new ExtMqttClient(mqttProperties.getServerURIs()[0], mqttProperties.getClientId(), mqttClientPersistence);
        MqttCallback callBack = mqttCallbackProvider.getIfUnique();
        
        if(callBack == null)
            callBack = mqttCallback();
        
        if(callBack instanceof DefaultMqttCallback defCallBack)
        {
            defCallBack.setMqttClient(mqttClient);
            defCallBack.setSubscribes(mqttProperties.getSubscribes());
            defCallBack.setMessageListener(messageListenerProvider.getIfUnique());
        }
        
        mqttClient.setTimeToWait(mqttProperties.getTimeToWait());
        mqttClient.setManualAcks(mqttProperties.isManualAcks());
        mqttClient.setCallback(callBack);
        mqttClient.connect(mqttProperties);
        
        return mqttClient;
    }

    private void parseSocketFactory(SoaMqttProperties mqttProperties, SocketFactory socketFactory, HostnameVerifier hostnameVerifier)
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
    
    private void parseClientId(SoaMqttProperties mqttProperties)
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

}
