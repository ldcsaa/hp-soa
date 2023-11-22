package io.github.hpsocket.demo.infra.mysql.service.impl;

import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.hpsocket.demo.infra.mysql.bo.UserBo;
import io.github.hpsocket.demo.infra.mysql.converter.UserConverter;
import io.github.hpsocket.demo.infra.mysql.entity.User;
import io.github.hpsocket.demo.infra.mysql.mapper.UserMapper;
import io.github.hpsocket.demo.infra.mysql.service.UserService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RefreshScope
@DubboService
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService
{
    @Autowired
    private UserConverter userConverter;
        
    @Override
    public UserBo getDefaultUser()
    {
        User user = getOne(Wrappers.<User>lambdaQuery().eq(User::getId, 1L));
        log.info("get user: {}", user);
        
        return userConverter.toBo(user);
    }

    @Override
    @DS("master")
    public UserBo getMasterUser()
    {
        User user = getOne(Wrappers.<User>lambdaQuery().eq(User::getId, 1L));
        log.info("get user: {}", user);
        
        return userConverter.toBo(user);
    }

    @Override
    @DS("slave")
    public UserBo getSlaveUser()
    {
        User user = getOne(Wrappers.<User>lambdaQuery().eq(User::getId, 1L));
        log.info("get user: {}", user);
        
        return userConverter.toBo(user);
    }

    @Override
    @DS("slave_01")
    public UserBo getSlave1User()
    {
        User user = getOne(Wrappers.<User>lambdaQuery().eq(User::getId, 1));
        log.info("get user: {}", user);
        
        return userConverter.toBo(user);
    }

    @Override
    @DS("slave_02")
    public UserBo getSlave2User()
    {
        User user = getOne(Wrappers.<User>lambdaQuery().eq(User::getId, 1));
        log.info("get user: {}", user);
        
        return userConverter.toBo(user);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public boolean saveUser(UserBo userBo)
    {
        User user = userConverter.fromBo(userBo);
        log.info("save user: {}", user);
        
        if(user.getId() != null)
        {
            User user2 = getOne(Wrappers.<User>lambdaQuery().eq(User::getId, user.getId()));
            if(user2 != null)
            {
                user.setVersion(user2.getVersion());
                return updateById(user);
            }
        }
        
        return save(user);
    }

}
