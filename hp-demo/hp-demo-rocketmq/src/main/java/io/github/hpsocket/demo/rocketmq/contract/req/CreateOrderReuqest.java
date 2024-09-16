package io.github.hpsocket.demo.rocketmq.contract.req;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "请求对象示例")
public class CreateOrderReuqest
{
    @NotNull(message = "区域不能为空")
    @Schema(description = "区域", example = "1", requiredMode = RequiredMode.REQUIRED, nullable = false)
    private Integer regionId;

    @NotBlank(message = "订单号不能为空")
    @Size(min = 6, message = "订单号不能小于6位")
    @Schema(description = "订单号", example = "1234567890987654", requiredMode = RequiredMode.REQUIRED, minLength = 16, nullable = false)
    private String orderNumber;

    @NotNull(message = "金额不能为空")
    @PositiveOrZero(message = "金额不能为负数")
    @Schema(description = "金额", example = "23000", requiredMode = RequiredMode.REQUIRED, nullable = false)
    private Long price;
}
