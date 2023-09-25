package io.github.hpsocket.soa.starter.data.mysql.entity;

import com.baomidou.mybatisplus.annotation.Version;

import lombok.Getter;
import lombok.Setter;

/** <b>乐观锁基础实体</b><br>
 * 支持乐观锁的数据库实体基类，定义了乐观锁字段 {@linkplain #version}
 */
@Getter
@Setter
@SuppressWarnings("serial")
public class BaseVersioningEntity extends BaseEntity
{
    /** 乐观锁版本号 */
    @Version
    private Integer version;
}
