package io.github.hpsocket.demo.infra.elasticsearch.bo;

import java.io.Serializable;
import java.time.ZonedDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** 保存员工信息响应对象 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("serial")
public class SaveEmployeeResponseBo implements Serializable
{
    /** 文档 ID */
    private String docId;
    
    /** 更新时间 */
    private ZonedDateTime updateTime;
}
