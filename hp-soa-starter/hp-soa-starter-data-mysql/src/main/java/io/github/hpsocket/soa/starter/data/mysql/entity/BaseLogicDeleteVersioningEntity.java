package io.github.hpsocket.soa.starter.data.mysql.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.Version;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SuppressWarnings("serial")
public class BaseLogicDeleteVersioningEntity extends BaseEntity
{
	@TableLogic
	private Byte deleted;
	@Version
	private Integer version;
}
