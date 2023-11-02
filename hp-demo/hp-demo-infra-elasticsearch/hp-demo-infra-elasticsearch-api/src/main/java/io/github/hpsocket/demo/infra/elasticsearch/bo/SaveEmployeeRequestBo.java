package io.github.hpsocket.demo.infra.elasticsearch.bo;

import java.io.Serializable;
import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/** 保存员工信息请求对象 */
@Getter
@Setter
@SuppressWarnings("serial")
public class SaveEmployeeRequestBo implements Serializable
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
}
