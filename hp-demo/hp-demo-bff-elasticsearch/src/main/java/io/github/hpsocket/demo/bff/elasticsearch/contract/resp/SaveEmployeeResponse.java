package io.github.hpsocket.demo.bff.elasticsearch.contract.resp;

import java.time.OffsetDateTime;

import com.alibaba.fastjson2.JSON;

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
    //@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private OffsetDateTime updateTime;

    @Override
    public String toString()
    {
        return JSON.toJSONString(this);
    }
}
