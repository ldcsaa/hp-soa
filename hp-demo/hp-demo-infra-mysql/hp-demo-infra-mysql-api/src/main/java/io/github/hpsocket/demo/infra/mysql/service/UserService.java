package io.github.hpsocket.demo.infra.mysql.service;

import io.github.hpsocket.demo.infra.mysql.bo.UserBo;

public interface UserService
{
	UserBo getDefaultUser();
	UserBo getMasterUser();
	UserBo getSlaveUser();
	UserBo getSlave1User();
	UserBo getSlave2User();
	
	boolean saveUser(UserBo userBo);
}
