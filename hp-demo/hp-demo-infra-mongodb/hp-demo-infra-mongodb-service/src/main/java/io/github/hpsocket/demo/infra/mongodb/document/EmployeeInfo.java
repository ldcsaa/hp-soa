package io.github.hpsocket.demo.infra.mongodb.document;

import org.springframework.data.mongodb.core.mapping.Document;

import io.github.hpsocket.demo.infra.mongodb.config.AppConfig;
import lombok.NoArgsConstructor;

/** <b>员工信息文档</b><br>
 * <ul>
 *   <li>以 jobNumber 作为文档 ID</li>
 *  </ul>
 */
@NoArgsConstructor
@Document(AppConfig.EMPLOYEE_INFO_COL)
public class EmployeeInfo extends EmployeeBase
{
    public EmployeeInfo(String jobNumber)
    {
        setJobNumber(jobNumber);
    }
    
    @Override
    public void setJobNumber(String jobNumber)
    {
        // 把 jobNumber 设置为文档 ID
        setId(jobNumber);
        super.setJobNumber(jobNumber);
    }
}
