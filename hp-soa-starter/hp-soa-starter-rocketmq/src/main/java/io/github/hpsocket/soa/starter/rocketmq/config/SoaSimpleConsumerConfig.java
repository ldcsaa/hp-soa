
package io.github.hpsocket.soa.starter.rocketmq.config;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.core.annotation.AnnotationUtils;

import io.github.hpsocket.soa.framework.web.holder.SpringContextHolder;
import io.github.hpsocket.soa.starter.rocketmq.annotation.SoaSimpleMessageListener;
import io.github.hpsocket.soa.starter.rocketmq.support.SoaRocketMQClientTemplate;
import io.github.hpsocket.soa.starter.rocketmq.support.SoaSimpleConsumerListener;

/** <b>Simple Consumer 配置</b><br>
 * 用于粘合 {@linkplain org.apache.rocketmq.client.core.RocketMQClientTemplate RocketMQClientTemplate} 与 {@linkplain SoaSimpleMessageListener}，
 * 让 {@linkplain SoaSimpleMessageListener} 注解的消息监听器来处理 {@linkplain org.apache.rocketmq.client.core.RocketMQClientTemplate#receive(int, java.time.Duration) RocketMQClientTemplate.receive(int maxMessageNum, Duration invisibleDuration)} 接收到的消息
 */
@AutoConfiguration
public class SoaSimpleConsumerConfig implements SmartInitializingSingleton
{
    SoaSimpleConsumerConfig(SpringContextHolder springContextHolder)
    {
        
    }

    @Override
    public void afterSingletonsInstantiated()
    {
        Map<String, Object> beans = SpringContextHolder.getApplicationContext()
                                        .getBeansWithAnnotation(SoaSimpleMessageListener.class)
                                        .entrySet().stream().filter(entry -> !ScopedProxyUtils.isScopedTarget(entry.getKey()))
                                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        
        beans.forEach(this::handleSimpleMessageListener);
    }

    public void handleSimpleMessageListener(String beanName, Object bean)
    {
        Class<?> clazz = AopProxyUtils.ultimateTargetClass(bean);
        
        if(!SoaSimpleConsumerListener.class.isAssignableFrom(bean.getClass()))
        {
            throw new IllegalStateException(clazz + " is not instance of " + SoaSimpleConsumerListener.class.getName());
        }
        
        SoaSimpleMessageListener annotation = AnnotationUtils.findAnnotation(clazz, SoaSimpleMessageListener.class);
        
        if(Objects.isNull(annotation))
        {
            throw new IllegalStateException("The SoaSimpleMessageListener annotation is missing");
        }
        
        SoaRocketMQClientTemplate rocketMQTemplate = SpringContextHolder.getBean(annotation.rocketMQTemplateBeanName());
        rocketMQTemplate.setSimpleConsumerListener((SoaSimpleConsumerListener)bean);
        rocketMQTemplate.setAutoAck(annotation.autoAck());
    }

}
