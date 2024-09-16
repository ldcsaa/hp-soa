package io.github.hpsocket.demo.rocketmq.contract.resp;

import java.time.OffsetDateTime;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.annotation.JSONField;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "响应对象示例")
public class CreateOrderResponse
{
    @Schema(description = "messageId", example = "123", requiredMode = RequiredMode.REQUIRED, nullable = false)
    private String messageId;

    @Schema(description = "internalMessageId", example = "999", requiredMode = RequiredMode.REQUIRED, nullable = false)
    private String internalMessageId;

    @Schema(description = "订单号", example = "1234567890987654", requiredMode = RequiredMode.REQUIRED, minLength = 16, nullable = false)
    private String orderNumber;

    @JSONField(format = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    @Schema(description = "创建时间", example = "2023-11-22 12:34:56.789", requiredMode = RequiredMode.REQUIRED, nullable = false)
    private OffsetDateTime createTime;

    public CreateOrderResponse(String messageId, String internalMessageId, String orderNumber)
    {
        this(messageId, internalMessageId, orderNumber, OffsetDateTime.now());
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

}
