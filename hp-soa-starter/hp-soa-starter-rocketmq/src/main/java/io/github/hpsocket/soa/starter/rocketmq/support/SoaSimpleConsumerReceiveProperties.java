package io.github.hpsocket.soa.starter.rocketmq.support;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "rocketmq.simple-consumer.receive")
public class SoaSimpleConsumerReceiveProperties implements Cloneable
{
    private boolean autoStart           = true;
    private int consumptionThreadCount  = 1;
    private int maxMessageNum           = 16;
    private int invisibleDuration       = 15;
    
    @Override
    public SoaSimpleConsumerReceiveProperties clone()
    {
        try
        {
            return (SoaSimpleConsumerReceiveProperties)super.clone();
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}
