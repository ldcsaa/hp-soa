package io.github.hpsocket.demo.kafka.entity;

import io.github.hpsocket.soa.starter.data.mysql.entity.BaseLogicDeleteEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SuppressWarnings("serial")
public class Order extends BaseLogicDeleteEntity
{
    private Integer regionId;
    private String orderNumber;
    private Long price;
}
