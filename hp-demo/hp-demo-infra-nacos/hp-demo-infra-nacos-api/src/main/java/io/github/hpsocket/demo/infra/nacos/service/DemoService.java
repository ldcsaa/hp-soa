package io.github.hpsocket.demo.infra.nacos.service;

import jakarta.validation.constraints.NotBlank;

public interface DemoService
{
    String sayHello(@NotBlank(message="姓名不能为空") String name);
}
