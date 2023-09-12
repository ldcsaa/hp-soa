package io.github.hpsocket.demo.mq.producer.converter;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;

import io.github.hpsocket.demo.mq.producer.contract.req.DemoCreateOrderReuqest;
import io.github.hpsocket.demo.mq.producer.contract.resp.DemoCreateOrderResponse;
import io.github.hpsocket.demo.mq.producer.entity.Order;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderConverter
{
	OrderConverter INSTANCE = Mappers.getMapper(OrderConverter.class);
	
	DemoCreateOrderResponse toResponse(Order order);
	Order fromRequest(DemoCreateOrderReuqest req);
}
