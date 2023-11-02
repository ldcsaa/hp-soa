
package io.github.hpsocket.demo.infra.elasticsearch.config;

import java.time.ZonedDateTime;

import org.springframework.boot.autoconfigure.AutoConfiguration;

import io.github.hpsocket.soa.framework.core.util.GeneralHelper;

/** <b>应用程序配置类</b> */
@AutoConfiguration
public class AppConfig
{
    /** 员工信息索引名称（实际上是索引别名，真实索引名称为：employee_info_internal）*/
    public static final String EMPLOYEE_INFO_INDEX = "employee_info";
    /** 员工历史索引名称前缀（完整索引名称为：employee_history_{YYYYMMDD}）*/
    public static final String EMPLOYEE_HISTORY_INDEX_PREFIX = "employee_history_";
    /** 员工历史索引别名（查询操作必须用索引别名）*/
    public static final String EMPLOYEE_HISTORY_INDEX_ALIAS = "employee_history";

    /** 获取当前员工历史索引名称 */
    public static String currentEmployeeHistoryIndex()
    {
        return getEmployeeHistoryIndex(ZonedDateTime.now());
    }

    /** 获取下一个员工历史索引名称 */
    public static String nextEmployeeHistoryIndex()
    {
        return getEmployeeHistoryIndex(ZonedDateTime.now().plusDays(1));
    }

    private static String getEmployeeHistoryIndex(ZonedDateTime dt)
    {
        return GeneralHelper.genNameByDateTime(EMPLOYEE_HISTORY_INDEX_PREFIX, dt);
    }

}
