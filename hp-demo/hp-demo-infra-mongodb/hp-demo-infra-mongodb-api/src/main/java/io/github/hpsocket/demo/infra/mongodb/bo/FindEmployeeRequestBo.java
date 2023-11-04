package io.github.hpsocket.demo.infra.mongodb.bo;

import java.io.Serializable;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/** <b>查找员工信息请求对象</b><p>
 * {@linkplain #findType}查找类型：
 * <ul>
 * <li>0. 根据{@linkplain #id}查找</li>
 * <li>1. 根据{@linkplain #salary}和{@linkplain #resign}查找</li>
 * <li>2. 根据{@linkplain #deptNumber}和{@linkplain #name}查找</li>
 * </ul>
 */
@Getter
@Setter
@SuppressWarnings("serial")
public class FindEmployeeRequestBo implements Serializable
{
    @NotNull
    @Min(0)
    @Max(2)
    private Integer findType;
    
    private String id;
    private String name;
    private String deptNumber;    
    private Integer salary;
    private Boolean resign;
}
