package io.github.hpsocket.demo.mqtt.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;

import io.github.hpsocket.soa.starter.mqtt.config.SoaDefaultMqttConfig;
import io.github.hpsocket.soa.starter.mqtt.properties.SoaMqttProperties;
import io.github.hpsocket.soa.starter.mqtt.service.MqttPropertiesCustomizer;

@Configuration
public class AppConfig
{
    @Bean(SoaDefaultMqttConfig.mqttPropertiesCustomizersBeanName)
    List<MqttPropertiesCustomizer> mqttPropertiesCustomizers()
    {
        return Arrays.asList(customizer1(), customizer2(), customizer3());
    }
    
    private MqttPropertiesCustomizer customizer1()
    {
        return new MqttPropertiesCustomizer() {
            
            @Override
            public void customize(SoaMqttProperties mqttProperties)
            {

            }
        };
    }
    
    private MqttPropertiesCustomizer customizer2()
    {
        return new MqttPropertiesCustomizer() {
            
            @Override
            public void customize(SoaMqttProperties mqttProperties)
            {

            }
        };
    }
    
    private MqttPropertiesCustomizer customizer3()
    {
        return new MqttPropertiesCustomizer() {
            
            @Override
            public void customize(SoaMqttProperties mqttProperties)
            {

            }
        };
    }
    
}
