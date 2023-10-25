package io.github.hpsocket.demo.mqtt.contract.resp;

import com.alibaba.fastjson2.JSON;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** 消息发布响应对象 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "消息发布响应对象")
public class PublishResponse
{
    private int code    = 0;
    private String desc = "ok";
    
    private Integer messageId;
    
    public PublishResponse(int code, String desc)
    {
         this.code = code;
         this.desc = desc;
    }

    public PublishResponse(Integer messageId)
    {
        this.messageId = messageId;
    }    
    
    @Override
    public String toString()
    {
        return JSON.toJSONString(this);
    }

}
