
package io.github.hpsocket.soa.starter.data.mysql.config;

import java.time.LocalDateTime;

import org.apache.ibatis.reflection.MetaObject;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;

import io.github.hpsocket.soa.starter.data.mysql.interceptor.ReadOnlyInterceptor;

@AutoConfiguration
@ConditionalOnProperty(name = "spring.datasource.dynamic.enabled", matchIfMissing = true)
public class SoaMybatisPlusConfig
{
	public static final String CREATE_TIME_FIELD_NAME = "createTime";
	public static final String UPDATE_TIME_FIELD_NAME = "updateTime";

	@Bean
	@ConditionalOnMissingBean(MybatisPlusInterceptor.class)
	public MybatisPlusInterceptor mybatisPlusInterceptor()
	{
		MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

		interceptor.addInnerInterceptor(new ReadOnlyInterceptor());
		interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
		interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
		interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());
		
		return interceptor;
	}
	
	@Bean
	@ConditionalOnMissingBean(MetaObjectHandler.class)
	MetaObjectHandler metaObjectHandler()
	{
		return new MetaObjectHandler()
		{
			@Override
			public void insertFill(MetaObject metaObject)
			{
				LocalDateTime now = LocalDateTime.now();
				fillStrategy(metaObject, CREATE_TIME_FIELD_NAME, now);
				fillStrategy(metaObject, UPDATE_TIME_FIELD_NAME, now);
			}

			@Override
			public void updateFill(MetaObject metaObject)
			{
				fillStrategy(metaObject, UPDATE_TIME_FIELD_NAME, LocalDateTime.now());
			}
		};
	}
}
