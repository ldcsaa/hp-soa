package io.github.hpsocket.demo.infra.mongodb.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import io.github.hpsocket.demo.infra.mongodb.document.EmployeeInfo;

/** <b>员工信息存储库</b><p>
 * 用于管理员工信息索引以及对索引文档执行 CRUD 等操作。
 */
@Repository
public interface EmployeeInfoRepository extends MongoRepository<EmployeeInfo, String>
{
    /** 根据 salary 和 resign 字段查询员工信息 */
    Iterable<EmployeeInfo> findBySalaryGreaterThanEqualAndResign(Integer salary, Boolean resign);

    /** 根据 department.number 和 name 字段查询员工信息 */
    @Query  ("""
                {"department.number": "?0", "name": {$regex: /?1/i}}
            """)
    Iterable<EmployeeInfo> findByDepartmentAndName(String deptNumber, String name);

}
