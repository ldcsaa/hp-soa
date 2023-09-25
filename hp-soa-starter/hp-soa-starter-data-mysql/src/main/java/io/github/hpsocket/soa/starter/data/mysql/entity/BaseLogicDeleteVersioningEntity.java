package io.github.hpsocket.soa.starter.data.mysql.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.Version;

import lombok.Getter;
import lombok.Setter;

/** <b>逻辑删除与乐观锁基础实体</b><br>
 * 支持逻辑删除与乐观锁的数据库实体基类，定义了逻辑删除字段 {@linkplain #deleted}，乐观锁字段 {@linkplain #version}
 */@Getter
@Setter
@SuppressWarnings("serial")
public class BaseLogicDeleteVersioningEntity extends BaseEntity
{
     /** 逻辑删除标记（0 - 未删除，1 - 已删除） */
    @TableLogic
    private Byte deleted;
    /** 乐观锁版本号 */
    @Version
    private Integer version;
}
