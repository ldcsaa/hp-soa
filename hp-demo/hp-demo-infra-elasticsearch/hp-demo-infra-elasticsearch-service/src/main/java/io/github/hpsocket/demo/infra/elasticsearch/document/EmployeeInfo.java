package io.github.hpsocket.demo.infra.elasticsearch.document;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Dynamic;

import io.github.hpsocket.demo.infra.elasticsearch.config.AppConfig;
import lombok.NoArgsConstructor;

/** <b>员工信息文档</b><br>
 * <ul>
 *   <li>启动时不自动创建索引</li>
 *   <li>索引由外部预先创建好</li>
 *   <li>以 jobNumber 作为文档 ID</li>
 *  </ul>
 */
@NoArgsConstructor
@Document(createIndex = false, indexName = AppConfig.EMPLOYEE_INFO_INDEX, dynamic = Dynamic.RUNTIME)
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
