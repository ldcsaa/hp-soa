package io.github.hpsocket.demo.infra.cloud.converter;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import io.github.hpsocket.demo.infra.cloud.bo.UserBo;
import io.github.hpsocket.demo.infra.cloud.entity.User;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserConverter
{
    UserConverter INSTANCE = Mappers.getMapper(UserConverter.class);
    
    UserBo toBo(User user);
    User fromBo(UserBo userBo);
}
