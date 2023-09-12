package io.github.hpsocket.demo.infra.mysql.entity;

import com.alibaba.fastjson2.JSONObject;
import io.github.hpsocket.soa.starter.data.mysql.entity.BaseLogicDeleteVersioningEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SuppressWarnings("serial")
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
