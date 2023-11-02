package io.github.hpsocket.demo.infra.elasticsearch.document;

import org.springframework.data.elasticsearch.annotations.Field;

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
