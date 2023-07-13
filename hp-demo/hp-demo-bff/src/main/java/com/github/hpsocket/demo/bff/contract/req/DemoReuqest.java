package com.github.hpsocket.demo.bff.contract.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;

/**
 * 请求 Model
 *
 * @author liangdongcheng
 * <p>@ApiModel Swagger Model 注解 </p>
 */
@Getter
@Setter
@ApiModel(description = "请求对象示例")
public class DemoReuqest {
    /**
     * <p>@NotBlank Validation 校验注解</p>
     * <p>@ApiModelProperty Swagger Model Property 注解</p>
     */
    @NotBlank(message = "name is empty")
    @ApiModelProperty(value = "姓名", example = "my name", required = true, allowEmptyValue = false)
    private String name;
    /**
     * <p>@ApiModelProperty Swagger Model Property 注解</p>
     */
    @ApiModelProperty(value = "年龄", example = "23", required = false, allowEmptyValue = false)
    private Integer age;
}
