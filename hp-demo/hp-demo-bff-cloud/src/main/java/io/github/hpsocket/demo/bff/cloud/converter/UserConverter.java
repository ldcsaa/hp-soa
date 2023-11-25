package io.github.hpsocket.demo.bff.cloud.converter;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import io.github.hpsocket.demo.bff.cloud.contract.req.SaveUserReuqest;
import io.github.hpsocket.demo.bff.cloud.contract.resp.QueryUserResponse;
import io.github.hpsocket.demo.infra.cloud.bo.UserBo;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserConverter
{
    UserConverter INSTANCE = Mappers.getMapper(UserConverter.class);
    
    UserBo fromSaveUserReuqest(SaveUserReuqest req);
    QueryUserResponse toQueryUserResponse(UserBo bo);
}
