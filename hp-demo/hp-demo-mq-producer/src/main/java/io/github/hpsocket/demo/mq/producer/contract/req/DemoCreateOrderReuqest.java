package io.github.hpsocket.demo.mq.producer.contract.req;

import org.hibernate.validator.constraints.Range;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "请求对象示例")
public class DemoCreateOrderReuqest
{
    @NotNull(message = "区域不能为空")
    @Range(min = 0, max = 3, message = "区域超出范围 [0 ~ 3]")
    @Schema(description = "区域", example = "1", requiredMode = RequiredMode.REQUIRED, nullable = false)
    private Integer regionId;

    @NotBlank(message = "orderNumber is empty")
    @Schema(description = "订单号", example = "1234567890987654", requiredMode = RequiredMode.REQUIRED, minLength = 16, nullable = false)
    private String orderNumber;

    @NotNull(message = "金额不能为空")
    @PositiveOrZero(message = "金额不能为负数")
    @Schema(description = "金额", example = "23000", requiredMode = RequiredMode.REQUIRED, nullable = false)
    private Long price;
}
