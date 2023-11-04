package io.github.hpsocket.demo.infra.mongodb.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;

import io.github.hpsocket.demo.infra.mongodb.bo.FindEmployeeRequestBo;
import io.github.hpsocket.demo.infra.mongodb.bo.FindEmployeeResponseBo;
import io.github.hpsocket.demo.infra.mongodb.bo.SaveEmployeeRequestBo;
import io.github.hpsocket.demo.infra.mongodb.bo.SaveEmployeeResponseBo;
import io.github.hpsocket.demo.infra.mongodb.converter.EmployeeConverter;
import io.github.hpsocket.demo.infra.mongodb.document.EmployeeHistory;
import io.github.hpsocket.demo.infra.mongodb.document.EmployeeInfo;
import io.github.hpsocket.demo.infra.mongodb.repository.EmployeeHistoryRepository;
import io.github.hpsocket.demo.infra.mongodb.repository.EmployeeInfoRepository;
import io.github.hpsocket.demo.infra.mongodb.service.EmployeeService;

/** <b>员工服务实现</b> */
@DubboService
@EnableScheduling
public class EmployeeServiceImpl implements EmployeeService
{
    /** 员工信息存储库 */
    @Autowired
    private EmployeeInfoRepository infoRepository;
    
    /** 员工历史存储库 */
    @Autowired
    private EmployeeHistoryRepository historyRepository;
    
    ///** MongoDB 操作模版 */
    //@Autowired
    //private MongoTemplate mongoTemplate;
    
    /** Bean 转换器 */
    @Autowired
    private EmployeeConverter converter;
    
    @Override
    // 开启事务（注：单机部署不支持事务）
    //@Transactional
    public SaveEmployeeResponseBo save(SaveEmployeeRequestBo req)
    {
        // 员工信息文档对象 -> 员工历史文档对象
        EmployeeInfo info = converter.fromSaveEmployeeRequest(req);
        EmployeeHistory history = converter.toEmployeeHistory(info);
        
        // 保存（插入或更新）员工信息
        infoRepository.save(info);
        
        // 保存员工历史
        // 用 repository 保存
        historyRepository.save(history);
        /* 或者 */
        // 用 template 保存
        //mongoTemplate.save(history);
        
        return converter.toSaveEmployeeResponse(info);
    }

    @Override
    public FindEmployeeResponseBo findById(String id)
    {
        List<FindEmployeeResponseBo.Item> list = new ArrayList<>();
        EmployeeInfo info = infoRepository.findById(id).orElse(null);
        
        if(info != null)
            list.add(converter.toFindEmployeeResponseItem(info));
        
        return new FindEmployeeResponseBo(list);
    }

    @Override
    public FindEmployeeResponseBo findBySalaryGreaterThanEqualAndResign(Integer salary, Boolean resign)
    {
        Iterable<EmployeeInfo> infos = infoRepository.findBySalaryGreaterThanEqualAndResign(salary, resign);
        List<FindEmployeeResponseBo.Item> list = converter.toFindEmployeeResponseItems(infos);

        return new FindEmployeeResponseBo(list);
    }

    @Override
    public FindEmployeeResponseBo findByDepartmentAndName(String deptNumber, String name)
    {
        Iterable<EmployeeInfo> infos = infoRepository.findByDepartmentAndName(deptNumber, name);
        List<FindEmployeeResponseBo.Item> list = converter.toFindEmployeeResponseItems(infos);

        return new FindEmployeeResponseBo(list);
    }

    @Override
    public FindEmployeeResponseBo findEmployee(FindEmployeeRequestBo req)
    {
        FindEmployeeResponseBo resp = null;
        
        // 根据查找类型，调用相应查询接口
        switch(req.getFindType())
        {
        case 0 -> resp = findById(req.getId());
        case 1 -> resp = findBySalaryGreaterThanEqualAndResign(req.getSalary(), req.getResign());
        case 2 -> resp = findByDepartmentAndName(req.getDeptNumber(), req.getName());
        default -> throw new IllegalArgumentException("Unexpected value: " + req.getFindType());
        }
        
        return resp;
    }
}
