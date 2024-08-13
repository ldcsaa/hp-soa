package io.github.hpsocket.demo.rocketmq.entity;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SuppressWarnings("serial")
public class Order implements Serializable
{
    private Integer regionId;
    private String orderNumber;
    private Long price;
}
