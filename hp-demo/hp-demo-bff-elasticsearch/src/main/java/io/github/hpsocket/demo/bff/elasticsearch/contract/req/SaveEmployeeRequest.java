package io.github.hpsocket.demo.bff.elasticsearch.contract.req;

import java.time.LocalDate;

import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/** 保存员工信息请求对象 */
@Getter
@Setter
public class SaveEmployeeRequest
{
    /** 工号（唯一属性） */
    @NotBlank
    private String jobNumber;
    
    /** 姓名 */
    @NotBlank
    private String name;
    
    /** 照片 */
    private String photoUri;
    
    /** 生日 */
    @JsonFormat(pattern = "yyyy-M-d")
    private LocalDate birthday;
    
    /** 薪资 */
    private Integer salary;
    
    /** 是否离职 */
    @NotNull
    private Boolean resign;
    
    /** 部门编号 */
    @NotBlank
    private String deptNumber;
    
    /** 部门名称 */
    @NotBlank
    private String deptName;
    
    @Override
    public String toString()
    {
        return JSON.toJSONString(this);
    }
}
