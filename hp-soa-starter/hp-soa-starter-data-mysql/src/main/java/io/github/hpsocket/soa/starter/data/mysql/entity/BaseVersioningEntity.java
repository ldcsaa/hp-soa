package io.github.hpsocket.soa.starter.data.mysql.entity;

import com.baomidou.mybatisplus.annotation.Version;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SuppressWarnings("serial")
public class BaseVersioningEntity extends BaseEntity
{
	@Version
	private Integer version;
}
