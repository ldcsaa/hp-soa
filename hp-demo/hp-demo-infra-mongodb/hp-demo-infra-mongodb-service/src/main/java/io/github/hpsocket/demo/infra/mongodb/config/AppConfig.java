
package io.github.hpsocket.demo.infra.mongodb.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/** <b>应用程序配置类</b> */
@AutoConfiguration
//激活审计功能（"zonedDateTimeProvider" Bean 作为日期时间提供者）
@EnableMongoAuditing(dateTimeProviderRef = "zonedDateTimeProvider")
//激活 Repository DAO Bean
@EnableMongoRepositories(basePackages = {"${spring.data.mongodb.repositories-base-packages:${hp.soa.web.component-scan.base-package:}}"})
public class AppConfig
{
    /** 员工信息集合名称*/
    public static final String EMPLOYEE_INFO_COL = "employee_info";
    /** 员工历史集合名称*/
    public static final String EMPLOYEE_HISTORY_COL = "employee_history";

}