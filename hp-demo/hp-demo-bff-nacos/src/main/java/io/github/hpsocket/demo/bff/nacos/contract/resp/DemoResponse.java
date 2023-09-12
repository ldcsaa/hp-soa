package io.github.hpsocket.demo.bff.nacos.contract.resp;

import com.alibaba.fastjson2.JSON;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "响应对象示例")
public class DemoResponse
{
	@Schema(description = "ID", example = "123", requiredMode = RequiredMode.REQUIRED, nullable = false)
    private Long id;

	@Schema(description = "姓名", example = "my name", requiredMode = RequiredMode.REQUIRED, nullable = false)
    private String name;

	@Schema(description = "年龄", example = "23", requiredMode = RequiredMode.NOT_REQUIRED, nullable = false)
    private Integer age;

    private String token;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
