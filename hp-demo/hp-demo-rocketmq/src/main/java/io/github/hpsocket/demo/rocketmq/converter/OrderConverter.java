package io.github.hpsocket.demo.rocketmq.converter;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import io.github.hpsocket.demo.rocketmq.contract.req.CreateOrderReuqest;
import io.github.hpsocket.demo.rocketmq.entity.Order;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderConverter
{
    OrderConverter INSTANCE = Mappers.getMapper(OrderConverter.class);
    
    Order fromRequest(CreateOrderReuqest req);
}
