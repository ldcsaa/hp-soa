package io.github.hpsocket.demo.mqtt.contract.req;

import org.hibernate.validator.constraints.Range;

import com.alibaba.fastjson2.JSON;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/** 消息发布请求对象 */
@Getter
@Setter
@Schema(description = "消息发布请求对象")
public class PublishRequest
{
    private boolean useDefaultOptions;
    @Range(min = 0, max = 2)
    private int qos;
    private boolean retained;
    @NotBlank
    private String topic;
    @NotBlank
    private String message;
    
    @Override
    public String toString()
    {
        return JSON.toJSONString(this);
    }
}
