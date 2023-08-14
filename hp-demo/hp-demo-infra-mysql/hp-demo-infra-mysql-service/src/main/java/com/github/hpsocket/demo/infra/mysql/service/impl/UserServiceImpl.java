package com.github.hpsocket.demo.infra.mysql.service.impl;

import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.hpsocket.demo.infra.mysql.bo.UserBo;
import com.github.hpsocket.demo.infra.mysql.converter.UserConverter;
import com.github.hpsocket.demo.infra.mysql.entity.User;
import com.github.hpsocket.demo.infra.mysql.mapper.UserMapper;
import com.github.hpsocket.demo.infra.mysql.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RefreshScope
@DubboService
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService
{
	@Override
	public UserBo getDefaultUser()
	{
		User user = getOne(Wrappers.<User>lambdaQuery().eq(User::getId, 1L));
		log.info("return user: ", user);
		
		return UserConverter.INSTANCE.toBo(user);
	}

	@Override
	@DS("master")
	public UserBo getMasterUser()
	{
		User user = getOne(Wrappers.<User>lambdaQuery().eq(User::getId, 1L));
		log.info("return user: ", user);
		
		return UserConverter.INSTANCE.toBo(user);
	}

	@Override
	@DS("slave")
	public UserBo getSlaveUser()
	{
		User user = getOne(Wrappers.<User>lambdaQuery().eq(User::getId, 1L));
		log.info("return user: ", user);
		
		return UserConverter.INSTANCE.toBo(user);
	}

	@Override
	@DS("slave_01")
	public UserBo getSlave1User()
	{
		User user = getOne(Wrappers.<User>lambdaQuery().eq(User::getId, 1));
		log.info("return user: ", user);
		
		return UserConverter.INSTANCE.toBo(user);
	}

	@Override
	@DS("slave_02")
	public UserBo getSlave2User()
	{
		User user = getOne(Wrappers.<User>lambdaQuery().eq(User::getId, 1));
		log.info("return user: ", user);
		
		return UserConverter.INSTANCE.toBo(user);
	}

	@Override
	public boolean addUser(UserBo userBo)
	{
		User user = UserConverter.INSTANCE.fromBo(userBo);
		log.info("insert user: ", user);
		
		return save(user);
	}

}
