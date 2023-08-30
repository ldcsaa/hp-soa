package io.github.hpsocket.demo.mq.producer.contract.req;

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
public class DemoCreateOrderReuqest {
    /**
     * <p>@NotBlank Validation 校验注解</p>
     * <p>@ApiModelProperty Swagger Model Property 注解</p>
     */
    @NotBlank(message = "orderNumber is empty")
    @Schema(description = "订单号", example = "1234567890987654", requiredMode = RequiredMode.REQUIRED, minLength = 16, nullable = false)
    private String orderNumber;
    /**
     * <p>@ApiModelProperty Swagger Model Property 注解</p>
     */
    @Schema(description = "金额", example = "23000", requiredMode = RequiredMode.REQUIRED, nullable = false)
    private Long price;
}
