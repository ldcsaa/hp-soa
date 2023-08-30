package io.github.hpsocket.soa.starter.data.mysql.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SuppressWarnings("serial")
public class BaseEntity implements Serializable
{
	@TableId
	private Long id;
	@TableField(fill = FieldFill.INSERT)
	private LocalDateTime createTime;
	@TableField(fill = FieldFill.INSERT_UPDATE)
	private LocalDateTime updateTime;

}
