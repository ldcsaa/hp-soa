package io.github.hpsocket.demo.infra.mongodb.document;

import org.springframework.data.mongodb.core.mapping.Document;

import io.github.hpsocket.demo.infra.mongodb.config.AppConfig;

/** <b>员工历史文档</b> */
@Document(AppConfig.EMPLOYEE_HISTORY_COL)
public class EmployeeHistory extends EmployeeBase
{
    
}
