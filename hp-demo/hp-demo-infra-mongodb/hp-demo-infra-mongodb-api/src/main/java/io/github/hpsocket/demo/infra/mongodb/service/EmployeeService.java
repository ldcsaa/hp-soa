package io.github.hpsocket.demo.infra.mongodb.service;

import io.github.hpsocket.demo.infra.mongodb.bo.FindEmployeeRequestBo;
import io.github.hpsocket.demo.infra.mongodb.bo.FindEmployeeResponseBo;
import io.github.hpsocket.demo.infra.mongodb.bo.SaveEmployeeRequestBo;
import io.github.hpsocket.demo.infra.mongodb.bo.SaveEmployeeResponseBo;

/** <b>员工服务接口</b> */
public interface EmployeeService
{
    /** 保存员工信息 */
    SaveEmployeeResponseBo save(SaveEmployeeRequestBo req);
    /** 根据查询类型和条件查询员工信息 */
    FindEmployeeResponseBo findEmployee(FindEmployeeRequestBo req);
    /** 根据文档 ID 查询员工信息 */
    FindEmployeeResponseBo findById(String id);
    /** 根据文档 salary 和 resign 查询员工信息 */
    FindEmployeeResponseBo findBySalaryGreaterThanEqualAndResign(Integer salary, Boolean resign);
    /** 根据文档 department.number 和 name 查询员工信息 */
    FindEmployeeResponseBo findByDepartmentAndName(String deptNumber, String name);
}
