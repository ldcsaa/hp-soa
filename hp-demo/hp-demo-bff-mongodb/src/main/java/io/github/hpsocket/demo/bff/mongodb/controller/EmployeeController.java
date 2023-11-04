package io.github.hpsocket.demo.bff.mongodb.controller;

import io.github.hpsocket.demo.bff.mongodb.contract.req.FindEmployeeRequest;
import io.github.hpsocket.demo.bff.mongodb.contract.req.SaveEmployeeRequest;
import io.github.hpsocket.demo.bff.mongodb.contract.resp.FindEmployeeResponse;
import io.github.hpsocket.demo.bff.mongodb.contract.resp.SaveEmployeeResponse;
import io.github.hpsocket.soa.framework.web.model.Response;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**<b>员工控制器接口</b> */
@RequestMapping(value = "/employee", method = {RequestMethod.POST})
public interface EmployeeController
{
    /**
     * <b>保存员工信息</b><p>
     * 
     * @param req 保存员工信息请求对象
     * @return 保存员工信息响应对象
     */
    @PostMapping(value = "/save")
    public Response<SaveEmployeeResponse> save(@RequestBody @Valid SaveEmployeeRequest req);
    
    /**
     * <b>查找员工信息</b><p>
     * 
     * @param req 查找员工信息请求对象
     * @return 查找员工信息响应对象
     */
    @PostMapping(value = "/find")
    public Response<FindEmployeeResponse> find(@RequestBody @Valid FindEmployeeRequest req);
}
