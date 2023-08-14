package com.github.hpsocket.demo.infra.mysql.entity;

import java.time.LocalDateTime;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.Version;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User
{
	private Long id;
	private String name;
	private Integer age;
	@Version
	private Integer version;
	@TableLogic
	private Byte deleted;
	private LocalDateTime createTime;
	private LocalDateTime updateTime;
	
	@Override
	public String toString()
	{
		return JSONObject.toJSONString(this);
	}
}
