package com.lcb.hapi.demo.contract.resp;

import com.alibaba.fastjson.JSON;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 响应对象
 *
 * @author liangdongcheng
 * <p>@ApiModel Swagger Model 注解 </p>
 */
@Getter
@Setter
@ApiModel(description = "响应对象示例")
public class DemoResponse {
    /**
     * Swagger Model Property 注解
     */
    @ApiModelProperty(value = "ID", example = "123", required = true, allowEmptyValue = false)
    private Long id;
    /**
     * Swagger Model Property 注解
     */
    @ApiModelProperty(value = "姓名", example = "my name", required = true, allowEmptyValue = false)
    private String name;
    /**
     * Swagger Model Property 注解
     */
    @ApiModelProperty(value = "年龄", example = "23", required = false, allowEmptyValue = false)
    private Integer age;

    private String token;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
