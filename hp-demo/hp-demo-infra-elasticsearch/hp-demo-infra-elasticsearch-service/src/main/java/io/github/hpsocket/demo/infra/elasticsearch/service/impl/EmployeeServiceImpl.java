package io.github.hpsocket.demo.infra.elasticsearch.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import io.github.hpsocket.demo.infra.elasticsearch.bo.FindEmployeeRequestBo;
import io.github.hpsocket.demo.infra.elasticsearch.bo.FindEmployeeResponseBo;
import io.github.hpsocket.demo.infra.elasticsearch.bo.SaveEmployeeRequestBo;
import io.github.hpsocket.demo.infra.elasticsearch.bo.SaveEmployeeResponseBo;
import io.github.hpsocket.demo.infra.elasticsearch.config.AppConfig;
import io.github.hpsocket.demo.infra.elasticsearch.converter.EmployeeConverter;
import io.github.hpsocket.demo.infra.elasticsearch.document.EmployeeHistory;
import io.github.hpsocket.demo.infra.elasticsearch.document.EmployeeInfo;
import io.github.hpsocket.demo.infra.elasticsearch.repository.EmployeeInfoRepository;
import io.github.hpsocket.demo.infra.elasticsearch.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;

/** <b>员工服务实现</b> */
@Slf4j
@DubboService
@EnableScheduling
public class EmployeeServiceImpl implements EmployeeService
{
    /** 员工信息存储库 */
    @Autowired
    private EmployeeInfoRepository infoRepository;
    
    ///** 员工历史存储库（没有使用） */
    //@Autowired
    //private EmployeeHistoryRepository historyRepository;
    
    /** ES 操作模版 */
    @Autowired
    private ElasticsearchTemplate esTemplate;
    
    /** Bean 转换器 */
    @Autowired
    private EmployeeConverter converter;
    
    /** <b>员工历史索引检查Job</b><p>
     * 功能：每天凌晨 23-24 点，每隔 10 分钟检查一次下一个“员工历史索引”是否已存在，如果不存在则创建。<br>
     * 说明：这是一个可选功能，即使不预先创建下一个“员工历史索引”，也会在写入数据时检查并自动创建，但创建索引可能会产生秒级阻塞，
     * 预先创建索引可以避免这种阻塞。
     */
    @Scheduled(cron = "0 */10 23 * * *")
    public void checkNextEmployeeHistoryIndex()
    {
        String index = AppConfig.nextEmployeeHistoryIndex();

        log.info("check next employee history index '{}'", index);
        
        IndexOperations ops = esTemplate.indexOps(IndexCoordinates.of(index));
        
        if(!ops.exists())
            ops.create();
    }
    
    @Override
    public SaveEmployeeResponseBo save(SaveEmployeeRequestBo req)
    {
        // 员工信息文档对象 -> 员工历史文档对象
        EmployeeInfo info = converter.fromSaveEmployeeRequest(req);
        EmployeeHistory history = converter.toEmployeeHistory(info);
        
        // 保存（插入或更新）员工信息
        infoRepository.save(info);
        
        // 不能用 historyRepository 保存员工历史
        //historyRepository.save(history);
        // 保存员工历史到当前索引分片
        esTemplate.save(history, IndexCoordinates.of(AppConfig.currentEmployeeHistoryIndex()));
        
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
