package io.github.hpsocket.demo.infra.elasticsearch.document;

import java.time.LocalDate;
import java.time.ZonedDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.elasticsearch.annotations.Field;

import lombok.Getter;
import lombok.Setter;

/** <b>员工信息和员工历史文档基类</b><p>
 * 字段参考：{@linkplain com.juanvision.soa.elastic.app.contract.SaveEmployeeRequest SaveEmployeeRequest}
 */
@Getter
@Setter
public class EmployeeBase
{
    @Id
    private String id;
    
    @Field
    private String jobNumber;
    
    @Field
    private String name;
    
    @Field
    private String photoUri;
    
    @Field
    //@Field(type = FieldType.Date)
    private LocalDate birthday;
    
    @Field
    private Integer salary;
    
    @Field
    private Boolean resign;
    
    @Field
    private Department department;
    
    @Field
    //@Field(type = FieldType.Date)
    @LastModifiedDate
    private ZonedDateTime updateTime;

}
