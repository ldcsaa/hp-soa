package com.github.hpsocket.demo.infra.mysql.entity;

import com.alibaba.fastjson2.JSONObject;
import com.github.hpsocket.soa.starter.data.mysql.entity.BaseLogicDeleteVersioningEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User extends BaseLogicDeleteVersioningEntity
{
	private String name;
	private Integer age;
	
	@Override
	public String toString()
	{
		return JSONObject.toJSONString(this);
	}
}
