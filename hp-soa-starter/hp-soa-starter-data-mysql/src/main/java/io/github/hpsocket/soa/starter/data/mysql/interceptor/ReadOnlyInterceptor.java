package io.github.hpsocket.soa.starter.data.mysql.interceptor;

import java.sql.SQLException;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;

import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;

import io.github.hpsocket.soa.framework.web.support.WebServerHelper;

import static org.apache.ibatis.mapping.SqlCommandType.*;

/** <b>mybatis-plus 应用程序只读拦截器</b><br>
 * 当应用程序为只读时，不能执行 {@linkplain SqlCommandType#INSERT INSERT}、{@linkplain SqlCommandType#UPDATE UPDATE}、{@linkplain SqlCommandType#DELETE DELETE} 等数据库更新操作
 */
public class ReadOnlyInterceptor implements InnerInterceptor
{
    @Override
    public void beforeUpdate(Executor executor, MappedStatement ms, Object parameter) throws SQLException
    {
        SqlCommandType type = ms.getSqlCommandType();
        
        if(type == INSERT || type == UPDATE || type == DELETE)
            Assert.isFalse(WebServerHelper.isAppReadOnly(), "application is read-only, can NOT execute update");;
    }
}
