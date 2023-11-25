package io.github.hpsocket.demo.infra.cloud.bo;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SuppressWarnings("serial")
@Schema(description = "用户业务对象")
public class UserBo implements Serializable
{
    @Schema(description = "id", example = "123", requiredMode = RequiredMode.NOT_REQUIRED, nullable = true)
    private Long id;
    @NotBlank(message = "name is empty")
    @Schema(description = "姓名", example = "my name", requiredMode = RequiredMode.REQUIRED, minLength = 1, nullable = false)
    private String name;
    @NotNull
    @Min(1)
    @Max(100)
    @Schema(description = "年龄", example = "23", requiredMode = RequiredMode.REQUIRED, nullable = false)
    private Integer age;
}
