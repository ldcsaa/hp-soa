package io.github.hpsocket.demo.infra.cloud.service;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import io.github.hpsocket.demo.infra.cloud.bo.UserBo;
import jakarta.validation.Valid;

@RequestMapping(value = "/user")
public interface UserService
{
    @GetMapping(value = "/{id}")
    UserBo getUser(@PathVariable Long id);
    
    @PostMapping(value = "/save")
    boolean saveUser(@RequestBody @Valid UserBo userBo);
}
