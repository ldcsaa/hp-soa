package io.github.hpsocket.soa.starter.rocketmq.support;

import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.message.MessageView;

/** <b>Simple Consumer 消息接收监听器接口</b><p>
 * 与 {@linkplain io.github.hpsocket.soa.starter.rocketmq.annotation.SoaSimpleMessageListener SoaSimpleMessageListener} 注解配合，处理 Simple Consumer 消息
 * 
 */
public interface SoaSimpleConsumerListener
{
    /** Simple Consumer 消息处理入口方法 */
    void consume(MessageView messageView, SoaRocketMQClientTemplate rocketMQClientTemplate) throws ClientException;
}
