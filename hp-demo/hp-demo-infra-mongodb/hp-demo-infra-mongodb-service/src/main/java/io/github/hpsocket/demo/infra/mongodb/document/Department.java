package io.github.hpsocket.demo.infra.mongodb.document;

import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Getter;
import lombok.Setter;

/** 部门信息内嵌属性 */
@Getter
@Setter
public class Department
{
    @Field
    private String number;
    @Field
    private String name;
}
