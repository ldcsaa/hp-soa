package io.github.hpsocket.demo.mq.producer.entity;

import io.github.hpsocket.soa.starter.rabbitmq.producer.entity.DomainEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@SuppressWarnings("serial")
public class DemoEvent extends DomainEvent
{
    private Long bizId;
    private Integer regionId;
    
    public DemoEvent(Long bizId)
    {
        this(bizId, 0);
    }
    
    public DemoEvent(Long bizId, Integer regionId)
    {
        this.bizId = bizId;
        this.regionId = regionId;
    }
}
