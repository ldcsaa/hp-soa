package com.github.hpsocket.demo.infra.mysql.converter;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.github.hpsocket.demo.infra.mysql.bo.UserBo;
import com.github.hpsocket.demo.infra.mysql.entity.User;

@Mapper
public interface UserConverter
{
	UserConverter INSTANCE = Mappers.getMapper(UserConverter.class);
	
	UserBo toBo(User user);
	User fromBo(UserBo userBo);
}
