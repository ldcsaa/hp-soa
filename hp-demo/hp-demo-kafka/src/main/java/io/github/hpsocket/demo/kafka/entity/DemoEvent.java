package io.github.hpsocket.demo.kafka.entity;

import com.baomidou.mybatisplus.annotation.TableName;

import io.github.hpsocket.soa.starter.kafka.producer.entity.DomainEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@SuppressWarnings("serial")
@TableName("t_kafka_demo_event")
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
