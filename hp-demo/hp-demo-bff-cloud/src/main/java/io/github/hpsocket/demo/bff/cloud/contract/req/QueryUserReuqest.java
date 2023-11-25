package io.github.hpsocket.demo.bff.cloud.contract.req;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "查询用户请求对象")
public class QueryUserReuqest
{
    @NotNull
    @Schema(description = "id", example = "123", requiredMode = RequiredMode.REQUIRED, nullable = false)
    private Long id;
}
