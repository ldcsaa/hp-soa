package io.github.hpsocket.demo.bff.mongodb.contract.resp;

import java.time.ZonedDateTime;

import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** 保存员工信息响应对象 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SaveEmployeeResponse
{
    /** 文档 ID */
    private String docId;
    
    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX")
    private ZonedDateTime updateTime;

    @Override
    public String toString()
    {
        return JSON.toJSONString(this);
    }
}
