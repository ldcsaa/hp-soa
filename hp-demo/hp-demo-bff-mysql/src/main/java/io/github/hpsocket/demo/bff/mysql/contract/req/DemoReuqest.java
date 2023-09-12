package io.github.hpsocket.demo.bff.mysql.contract.req;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "请求对象示例")
public class DemoReuqest {
    @Schema(description = "id", example = "123", requiredMode = RequiredMode.NOT_REQUIRED, nullable = true)
    private Long id;
    @NotBlank(message = "name is empty")
    @Schema(description = "姓名", example = "my name", requiredMode = RequiredMode.REQUIRED, minLength = 1, nullable = false)
    private String name;
    @Schema(description = "年龄", example = "23", requiredMode = RequiredMode.NOT_REQUIRED, nullable = false)
    private Integer age;
}
