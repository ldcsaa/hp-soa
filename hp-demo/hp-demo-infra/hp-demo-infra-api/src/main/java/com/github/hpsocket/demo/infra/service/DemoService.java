package com.github.hpsocket.demo.infra.service;

import jakarta.validation.constraints.NotBlank;

public interface DemoService
{
	String sayHello(@NotBlank String name);
}
