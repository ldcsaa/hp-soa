package io.github.hpsocket.demo.infra.mongodb.bo;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** 查找员工信息响应对象 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("serial")
public class FindEmployeeResponseBo implements Serializable
{
    /** 结果数量 */
    private Integer count;
    
    /** 结果列表 */
    private List<Item> employees;
    
    public FindEmployeeResponseBo(List<Item> employees)
    {
        this(employees.size(), employees);
    }
    
    /** 查找员工信息条目 */
    @Getter
    @Setter
    public static class Item implements Serializable
    {
        private String jobNumber;
        private String name;       
        private String photoUri;
        private LocalDate birthday;
        private Integer salary;
        private Boolean resign;
        private String deptNumber;
        private String deptName;
        private ZonedDateTime updateTime;
    }
}
