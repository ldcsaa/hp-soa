
package io.github.hpsocket.demo.infra.elasticsearch.config;

import java.time.OffsetDateTime;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.data.elasticsearch.config.EnableElasticsearchAuditing;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import io.github.hpsocket.soa.framework.core.util.GeneralHelper;
import io.github.hpsocket.soa.starter.data.elasticsearch.config.SoaElasticConfig;

/** <b>应用程序配置类</b> */
@AutoConfiguration
//激活审计功能（"offsetDateTimeProvider" Bean 作为日期时间提供者）
@EnableElasticsearchAuditing(dateTimeProviderRef = SoaElasticConfig.offsetDateTimeProviderBeanName)
//激活 Repository DAO Bean
@EnableElasticsearchRepositories(basePackages = {"${spring.elasticsearch.repositories-base-packages:${hp.soa.web.component-scan.base-package:}}"})
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
        return getEmployeeHistoryIndex(OffsetDateTime.now());
    }

    /** 获取下一个员工历史索引名称 */
    public static String nextEmployeeHistoryIndex()
    {
        return getEmployeeHistoryIndex(OffsetDateTime.now().plusDays(1));
    }

    private static String getEmployeeHistoryIndex(OffsetDateTime dt)
    {
        return GeneralHelper.genNameByDateTime(EMPLOYEE_HISTORY_INDEX_PREFIX, dt);
    }

}
