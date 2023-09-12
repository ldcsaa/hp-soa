package io.github.hpsocket.demo.infra.mysql.converter;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;

import io.github.hpsocket.demo.infra.mysql.bo.UserBo;
import io.github.hpsocket.demo.infra.mysql.entity.User;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserConverter
{
	UserConverter INSTANCE = Mappers.getMapper(UserConverter.class);
	
	UserBo toBo(User user);
	User fromBo(UserBo userBo);
}
