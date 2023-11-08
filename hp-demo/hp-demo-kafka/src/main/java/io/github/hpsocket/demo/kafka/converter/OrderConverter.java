package io.github.hpsocket.demo.kafka.converter;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import io.github.hpsocket.demo.kafka.contract.req.DemoCreateOrderReuqest;
import io.github.hpsocket.demo.kafka.contract.resp.DemoCreateOrderResponse;
import io.github.hpsocket.demo.kafka.entity.Order;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderConverter
{
    OrderConverter INSTANCE = Mappers.getMapper(OrderConverter.class);
    
    DemoCreateOrderResponse toResponse(Order order);
    Order fromRequest(DemoCreateOrderReuqest req);
}
