package io.github.hpsocket.demo.infra.elasticsearch.repository;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import io.github.hpsocket.demo.infra.elasticsearch.document.EmployeeInfo;

/** <b>员工信息存储库</b><p>
 * 用于管理员工信息索引以及对索引文档执行 CRUD 等操作。<br>
 * 关于 {@linkplain ElasticsearchRepository} 功能及其接口方法签名规范可参考网上资料。<br>
 * @see <a href="https://docs.spring.io/spring-data/elasticsearch/docs/current/reference/html/#repositories.core-concepts">spring-data - elasticsearch</a>
 * @see <a href="https://segmentfault.com/a/1190000018625101">Spring Boot整合ElasticSearch - 1</a>
 * @see <a href="https://juejin.cn/post/6976253744342122504">Spring Boot整合ElasticSearch - 2</a>
 */
@Repository
public interface EmployeeInfoRepository extends ElasticsearchRepository<EmployeeInfo, String>
{
    /** 根据 salary 和 resign 字段查询员工信息 */
    Iterable<EmployeeInfo> findBySalaryGreaterThanEqualAndResign(Integer salary, Boolean resign);

    /** 根据 department.number 和 name 字段查询员工信息 */
    @Query  ("""
                {
                    "bool": {
                        "must": [{
                            "term": {
                                "department.number": "?0"
                            }
                        }, {
                            "wildcard": {
                                "name": "*?1*"
                            }
                        }]
                    }
                }
            """)
    Iterable<EmployeeInfo> findByDepartmentAndName(String deptNumber, String name);

}
