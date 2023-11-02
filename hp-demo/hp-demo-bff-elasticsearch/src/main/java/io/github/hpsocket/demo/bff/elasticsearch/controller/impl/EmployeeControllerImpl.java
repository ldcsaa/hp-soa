package io.github.hpsocket.demo.bff.elasticsearch.controller.impl;

import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.github.hpsocket.demo.bff.elasticsearch.contract.req.FindEmployeeRequest;
import io.github.hpsocket.demo.bff.elasticsearch.contract.req.SaveEmployeeRequest;
import io.github.hpsocket.demo.bff.elasticsearch.contract.resp.FindEmployeeResponse;
import io.github.hpsocket.demo.bff.elasticsearch.contract.resp.SaveEmployeeResponse;
import io.github.hpsocket.demo.bff.elasticsearch.controller.EmployeeController;
import io.github.hpsocket.demo.bff.elasticsearch.converter.EmployeeConverter;
import io.github.hpsocket.demo.infra.elasticsearch.bo.FindEmployeeRequestBo;
import io.github.hpsocket.demo.infra.elasticsearch.bo.FindEmployeeResponseBo;
import io.github.hpsocket.demo.infra.elasticsearch.bo.SaveEmployeeRequestBo;
import io.github.hpsocket.demo.infra.elasticsearch.bo.SaveEmployeeResponseBo;
import io.github.hpsocket.demo.infra.elasticsearch.service.EmployeeService;
import io.github.hpsocket.soa.framework.web.annotation.AccessVerification;
import io.github.hpsocket.soa.framework.web.annotation.AccessVerification.Type;
import io.github.hpsocket.soa.framework.web.model.Response;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@AccessVerification(Type.NO_LOGIN)
public class EmployeeControllerImpl implements EmployeeController
{
    /** 员工服务 */
    @DubboReference
    EmployeeService employeeService;
    
    /** Bean 转换器 */
    @Autowired
    EmployeeConverter converter;
    
    @Override
    public Response<SaveEmployeeResponse> save(@RequestBody @Valid SaveEmployeeRequest req)
    {
        log.info("[保存员工信息请求] -> {}", req);
        
        SaveEmployeeRequestBo reqBo   = converter.toBo(req);
        SaveEmployeeResponseBo respBo = employeeService.save(reqBo);
        SaveEmployeeResponse resp     = converter.fromBo(respBo);
        
        log.info("[保存员工信息结果] -> {}", resp);
        
        return new Response<>(resp);
    }
    
    @Override
    public Response<FindEmployeeResponse> find(@RequestBody @Valid FindEmployeeRequest req)
    {
        log.info("[查找员工信息请求] -> {}", req);
        
        // 检查参数
        checkFindParams(req);
        
        FindEmployeeRequestBo reqBo   = converter.toBo(req);
        FindEmployeeResponseBo respBo = employeeService.findEmployee(reqBo);
        FindEmployeeResponse resp     = converter.fromBo(respBo);
        
        log.info("[查找员工信息结果] -> {}", resp);
        
        return new Response<>(resp);
    }

    private void checkFindParams(FindEmployeeRequest req)
    {
        Integer type = req.getFindType();
        
        if(type == 0 && StringUtils.isBlank(req.getId()))
            throw new IllegalArgumentException("'id' arg can't be null or empty while find type is " + req.getFindType());
        else if(type == 1 && (req.getSalary() == null || req.getResign() == null))
            throw new IllegalArgumentException("'salary' or 'resign' arg can't be null or empty while find type is " + req.getFindType());
        else if(type == 2 && (StringUtils.isBlank(req.getDeptNumber()) || StringUtils.isBlank(req.getName())))
            throw new IllegalArgumentException("'deptNumber' or 'name' param can't be null or empty while find type is " + req.getFindType());
    }

}
