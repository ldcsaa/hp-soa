package io.github.hpsocket.demo.infra.mongodb.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import io.github.hpsocket.demo.infra.mongodb.document.EmployeeHistory;

/** <b>员工历史存储库</b><p>
 * 注意：该存储库只能操作应用程序启动当天创建的索引分片，不能处理定期创建新索引的场景，因此应用程序并没有使用该存储库。
 */
@Repository
public interface EmployeeHistoryRepository extends MongoRepository<EmployeeHistory, String>
{

}
