package io.github.hpsocket.soa.starter.data.mysql.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;

import lombok.Getter;
import lombok.Setter;

/** <b>逻辑删除基础实体</b><br>
 * 支持逻辑删除的数据库实体基类，定义了逻辑删除字段 {@linkplain #deleted}
 */
@Getter
@Setter
@SuppressWarnings("serial")
public class BaseLogicDeleteEntity extends BaseEntity
{
	/** 逻辑删除标记（0 - 未删除，1 - 已删除） */
	@TableLogic
	private Byte deleted;
}
