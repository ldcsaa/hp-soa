package io.github.hpsocket.soa.starter.data.mysql.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;

import lombok.Getter;
import lombok.Setter;

/** <b>基础实体</b><br>
 * 数据库实体的基类，定义了 {@linkplain #id}、{@linkplain #createTime}、{@linkplain #updateTime} 字段
 */
@Getter
@Setter
@SuppressWarnings("serial")
public class BaseEntity implements Serializable
{
    /** 主键 */
    @TableId
    private Long id;
    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    /** 最后更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

}
