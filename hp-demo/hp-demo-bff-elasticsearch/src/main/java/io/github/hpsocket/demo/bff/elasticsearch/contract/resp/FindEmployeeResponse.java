package io.github.hpsocket.demo.bff.elasticsearch.contract.resp;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** 查找员工信息响应对象 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FindEmployeeResponse
{
    /** 结果数量 */
    private Integer count;
    /** 结果列表 */
    private List<Item> employees;
    
    public FindEmployeeResponse(List<Item> employees)
    {
        this(employees.size(), employees);
    }
    
    @Override
    public String toString()
    {
        return JSON.toJSONString(this);
    }
    
    /** 查找员工信息条目 */
    @Getter
    @Setter
    public static class Item
    {
        private String jobNumber;
        private String name;
        private String photoUri;
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate birthday;
        private Integer salary;
        private Boolean resign;
        private String deptNumber;
        private String deptName;
        //@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
        private OffsetDateTime updateTime;
    }
}
