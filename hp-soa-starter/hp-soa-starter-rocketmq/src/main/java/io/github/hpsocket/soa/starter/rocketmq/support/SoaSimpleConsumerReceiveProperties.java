package io.github.hpsocket.soa.starter.rocketmq.support;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

/** <b>Simple Consumer 默认消息接收属性</b><p>
 * 属性配置前缀："rocketmq.simple-consumer.receive"
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "rocketmq.simple-consumer.receive")
public class SoaSimpleConsumerReceiveProperties implements Cloneable
{
    /** 是否自动启动消息接收 */
    private boolean autoStart           = true;
    /** 消息接收线程数：{@linkplain org.apache.rocketmq.client.core.RocketMQClientTemplate#receive(int, java.time.Duration) receive(int maxMessageNum, Duration invisibleDuration)} 并发线程数 */
    private int consumptionThreadCount  = 1;
    /** 最大消息数量：{@linkplain org.apache.rocketmq.client.core.RocketMQClientTemplate#receive(int, java.time.Duration) receive(int maxMessageNum, Duration invisibleDuration)} 的 maxMessageNum 参数 */
    private int maxMessageNum           = 16;
    /** 消息不可见期限（秒）：{@linkplain org.apache.rocketmq.client.core.RocketMQClientTemplate#receive(int, java.time.Duration) receive(int maxMessageNum, Duration invisibleDuration)} 的 invisibleDuration 参数 */
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
