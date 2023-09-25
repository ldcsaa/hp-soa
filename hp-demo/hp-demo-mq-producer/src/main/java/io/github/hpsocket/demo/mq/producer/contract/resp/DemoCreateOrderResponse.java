package io.github.hpsocket.demo.mq.producer.contract.resp;

import java.time.LocalDateTime;

import com.alibaba.fastjson2.JSON;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "响应对象示例")
public class DemoCreateOrderResponse
{
    @Schema(description = "ID", example = "123", requiredMode = RequiredMode.REQUIRED, nullable = false)
    private Long id;

    @Schema(description = "订单号", example = "1234567890987654", requiredMode = RequiredMode.REQUIRED, minLength = 16, nullable = false)
    private String orderNumber;

    @Schema(description = "创建时间", example = "2023-11-22 12:34:56.789", requiredMode = RequiredMode.REQUIRED, nullable = false)
    private LocalDateTime createTime;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
