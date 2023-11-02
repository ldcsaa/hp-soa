package io.github.hpsocket.demo.infra.elasticsearch.document;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Dynamic;

/** <b>员工历史文档</b><br>
 * <ul>
 *   <li>启动时自动创建索引</li>
 *   <li>索引名称由 {@linkplain AppConfig#currentEmployeeHistoryIndex()} 确定</li>
 *  </ul>
 */
@Document(createIndex = true, indexName = "#{T(io.github.hpsocket.demo.infra.elasticsearch.config.AppConfig).currentEmployeeHistoryIndex()}", dynamic = Dynamic.RUNTIME)
public class EmployeeHistory extends EmployeeBase
{
    
}
