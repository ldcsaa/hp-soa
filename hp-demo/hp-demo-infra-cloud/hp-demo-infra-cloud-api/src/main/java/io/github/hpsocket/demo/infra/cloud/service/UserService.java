package io.github.hpsocket.demo.infra.cloud.service;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import io.github.hpsocket.demo.infra.cloud.bo.UserBo;
import jakarta.validation.Valid;

public interface UserService
{
    @GetMapping(value = "/user/{id}")
    UserBo getUser(@PathVariable Long id);
    
    @PostMapping(value = "/user/save")
    boolean saveUser(@RequestBody @Valid UserBo userBo);
}
