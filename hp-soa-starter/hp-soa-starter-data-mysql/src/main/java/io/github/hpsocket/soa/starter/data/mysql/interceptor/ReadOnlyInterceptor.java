package io.github.hpsocket.soa.starter.data.mysql.interceptor;

import java.sql.SQLException;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;

import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;

import io.github.hpsocket.soa.framework.web.support.WebServerHelper;

public class ReadOnlyInterceptor implements InnerInterceptor
{
	@Override
	public void beforeUpdate(Executor executor, MappedStatement ms, Object parameter) throws SQLException
	{
		SqlCommandType type = ms.getSqlCommandType();
		
		if(type == SqlCommandType.INSERT || type == SqlCommandType.UPDATE || type == SqlCommandType.DELETE)
			Assert.isFalse(WebServerHelper.isAppReadOnly(), "application is read-only, can NOT execute update");;
	}
}
