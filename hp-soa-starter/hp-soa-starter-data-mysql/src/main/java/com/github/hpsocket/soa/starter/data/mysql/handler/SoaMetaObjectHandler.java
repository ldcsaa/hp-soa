package com.github.hpsocket.soa.starter.data.mysql.handler;

import java.time.LocalDateTime;

import org.apache.ibatis.reflection.MetaObject;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;

public class SoaMetaObjectHandler implements MetaObjectHandler
{
	private static final String CREATE_TIME_FIELD_NAME = "createTime";
	private static final String UPDATE_TIME_FIELD_NAME = "updateTime";

	@Override
	public void insertFill(MetaObject metaObject)
	{
		strictFillStrategy(metaObject, CREATE_TIME_FIELD_NAME, LocalDateTime::now);
	}

	@Override
	public void updateFill(MetaObject metaObject)
	{
		strictFillStrategy(metaObject, UPDATE_TIME_FIELD_NAME, LocalDateTime::now);
	}

}
