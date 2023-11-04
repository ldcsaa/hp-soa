package io.github.hpsocket.demo.bff.mongodb.converter;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import io.github.hpsocket.demo.bff.mongodb.contract.req.FindEmployeeRequest;
import io.github.hpsocket.demo.bff.mongodb.contract.req.SaveEmployeeRequest;
import io.github.hpsocket.demo.bff.mongodb.contract.resp.FindEmployeeResponse;
import io.github.hpsocket.demo.bff.mongodb.contract.resp.SaveEmployeeResponse;
import io.github.hpsocket.demo.infra.mongodb.bo.FindEmployeeRequestBo;
import io.github.hpsocket.demo.infra.mongodb.bo.FindEmployeeResponseBo;
import io.github.hpsocket.demo.infra.mongodb.bo.SaveEmployeeRequestBo;
import io.github.hpsocket.demo.infra.mongodb.bo.SaveEmployeeResponseBo;

/** Bean 转换器 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EmployeeConverter
{
    EmployeeConverter INSTANCE = Mappers.getMapper(EmployeeConverter.class);
    
    FindEmployeeRequestBo toBo(FindEmployeeRequest req);
    SaveEmployeeRequestBo toBo(SaveEmployeeRequest req);
    
    FindEmployeeResponse fromBo(FindEmployeeResponseBo bo);
    SaveEmployeeResponse fromBo(SaveEmployeeResponseBo bo);
}
