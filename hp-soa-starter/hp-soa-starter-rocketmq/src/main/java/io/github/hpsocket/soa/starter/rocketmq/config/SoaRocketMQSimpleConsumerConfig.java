
package io.github.hpsocket.soa.starter.rocketmq.config;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.boot.autoconfigure.AutoConfiguration;

import io.github.hpsocket.soa.framework.web.holder.SpringContextHolder;
import io.github.hpsocket.soa.starter.rocketmq.annotation.SoaRocketMQSimpleMessageListener;
import io.github.hpsocket.soa.starter.rocketmq.support.SoaRocketMQClientTemplate;
import io.github.hpsocket.soa.starter.rocketmq.support.SoaRocketMQSimpleConsumerListener;

@AutoConfiguration
public class SoaRocketMQSimpleConsumerConfig implements SmartInitializingSingleton
{
    SoaRocketMQSimpleConsumerConfig(SpringContextHolder springContextHolder)
    {
        
    }

    @Override
    public void afterSingletonsInstantiated()
    {
        Map<String, Object> beans = SpringContextHolder.getApplicationContext()
                                        .getBeansWithAnnotation(SoaRocketMQSimpleMessageListener.class)
                                        .entrySet().stream().filter(entry -> !ScopedProxyUtils.isScopedTarget(entry.getKey()))
                                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        
        beans.forEach(this::handleSimpleMessageListener);
    }

    public void handleSimpleMessageListener(String beanName, Object bean)
    {
        Class<?> clazz = AopProxyUtils.ultimateTargetClass(bean);
        
        if(!SoaRocketMQSimpleConsumerListener.class.isAssignableFrom(bean.getClass()))
        {
            throw new IllegalStateException(clazz + " is not instance of " + SoaRocketMQSimpleConsumerListener.class.getName());
        }
        
        SoaRocketMQSimpleMessageListener annotation = clazz.getAnnotation(SoaRocketMQSimpleMessageListener.class);
        
        if(Objects.isNull(annotation))
        {
            throw new IllegalStateException("The SoaRocketMQSimpleMessageListener annotation is missing");
        }
        
        SoaRocketMQClientTemplate rocketMQTemplate = SpringContextHolder.getBean(annotation.rocketMQTemplateBeanName());
        rocketMQTemplate.setSimpleConsumerListener((SoaRocketMQSimpleConsumerListener)bean);
    }

}
