package io.github.hpsocket.demo.infra.elasticsearch.converter;

import java.util.List;

import org.mapstruct.InheritConfiguration;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import io.github.hpsocket.demo.infra.elasticsearch.bo.FindEmployeeResponseBo;
import io.github.hpsocket.demo.infra.elasticsearch.bo.SaveEmployeeRequestBo;
import io.github.hpsocket.demo.infra.elasticsearch.bo.SaveEmployeeResponseBo;
import io.github.hpsocket.demo.infra.elasticsearch.document.EmployeeHistory;
import io.github.hpsocket.demo.infra.elasticsearch.document.EmployeeInfo;

/** Bean 转换器 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EmployeeConverter
{
    EmployeeConverter INSTANCE = Mappers.getMapper(EmployeeConverter.class);
    
    /** 对象转换：{@linkplain EmployeeInfo} -> {@linkplain EmployeeHistory}<br>
     * 注意：不拷贝 id 字段，保存 {@linkplain EmployeeHistory} 对象永远是 INSERT 操作。
     */
    @Mapping(target = "id", ignore = true)
    EmployeeHistory toEmployeeHistory(EmployeeInfo info);
    
    /** 对象转换：{@linkplain SaveEmployeeRequest} -> {@linkplain EmployeeInfo} */
    @Mappings({
                @Mapping(source = "deptNumber", target = "department.number"),
                @Mapping(source = "deptName", target = "department.name")
             })
    EmployeeInfo fromSaveEmployeeRequest(SaveEmployeeRequestBo req);

    /** 对象转换：{@linkplain EmployeeInfo} -> {@linkplain SaveEmployeeRequest} */
    @InheritInverseConfiguration(name = "fromSaveEmployeeRequest")
    SaveEmployeeRequestBo toSaveEmployeeRequest(EmployeeInfo info);
    
    /** 对象转换：{@linkplain EmployeeInfo} -> {@linkplain SaveEmployeeResponse} */
    @Mapping(source = "id", target = "docId")
    SaveEmployeeResponseBo toSaveEmployeeResponse(EmployeeInfo info);
    
    /** 对象转换：{@linkplain EmployeeInfo} -> {@linkplain FindEmployeeResponse.Item} */
    @Mappings({
                @Mapping(source = "department.number", target = "deptNumber"),
                @Mapping(source = "department.name", target = "deptName")
             })
    FindEmployeeResponseBo.Item toFindEmployeeResponseItem(EmployeeInfo info);
    
    /** 对象转换：{@linkplain EmployeeInfo Iterable&lt;EmployeeInfo&gt;} -> {@linkplain FindEmployeeResponse.Item List&lt;FindEmployeeResponse.Item&gt;} */
    @InheritConfiguration(name = "toFindEmployeeResponseItem")
    List<FindEmployeeResponseBo.Item> toFindEmployeeResponseItems(Iterable<EmployeeInfo> infos);
}
