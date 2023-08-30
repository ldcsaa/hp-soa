package io.github.hpsocket.soa.starter.data.mysql.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SuppressWarnings("serial")
public class BaseLogicDeleteEntity extends BaseEntity
{
	@TableLogic
	private Byte deleted;
}
