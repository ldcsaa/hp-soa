package com.github.hpsocket.demo.bff.contract.req;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * 请求 Model
 *
 * @author liangdongcheng
 * <p>@ApiModel Swagger Model 注解 </p>
 */
@Getter
@Setter
@Schema(description = "请求对象示例")
public class DemoReuqest {
    /**
     * <p>@NotBlank Validation 校验注解</p>
     * <p>@ApiModelProperty Swagger Model Property 注解</p>
     */
    //@NotBlank(message = "name is empty")
    @Schema(description = "姓名", example = "my name", requiredMode = RequiredMode.REQUIRED, minLength = 1, nullable = false)
    private String name;
    /**
     * <p>@ApiModelProperty Swagger Model Property 注解</p>
     */
    @Schema(description = "年龄", example = "23", requiredMode = RequiredMode.NOT_REQUIRED, nullable = false)
    private Integer age;
}
