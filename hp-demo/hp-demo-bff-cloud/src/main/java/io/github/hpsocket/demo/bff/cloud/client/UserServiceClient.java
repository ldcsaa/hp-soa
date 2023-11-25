package io.github.hpsocket.demo.bff.cloud.client;

import org.springframework.cloud.openfeign.FeignClient;

import io.github.hpsocket.demo.infra.cloud.service.UserService;

@FeignClient(UserServiceClient.SERVICE_NAME)
public interface UserServiceClient extends UserService
{
    String SERVICE_NAME = "hp-demo-infra-cloud-service";
}
