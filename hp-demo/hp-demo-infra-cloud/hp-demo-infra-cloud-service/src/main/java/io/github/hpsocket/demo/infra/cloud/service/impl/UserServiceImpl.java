package io.github.hpsocket.demo.infra.cloud.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import io.github.hpsocket.demo.infra.cloud.bo.UserBo;
import io.github.hpsocket.demo.infra.cloud.converter.UserConverter;
import io.github.hpsocket.demo.infra.cloud.entity.User;
import io.github.hpsocket.demo.infra.cloud.mapper.UserMapper;
import io.github.hpsocket.demo.infra.cloud.service.UserService;
import io.github.hpsocket.soa.framework.web.advice.RequestContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RestController
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService
{
    @Autowired
    private UserConverter userConverter;
        
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

    @Override
    @DS("slave")
    public UserBo getUser(Long id)
    {
        /* 通过 RequestContext.getXxx() 获取 Request Context 相关信息 */
        System.out.printf("HAPI-INS - clientId: %s, requestId: %s\n", RequestContext.getClientId(), RequestContext.getRequestId());
        
        /*
        if(id == 200)
            throw new RuntimeException("test RuntimeException");
        if(id == 300)
            throw new ServiceException("test ServiceException", 555);
        */
        
        User user = getOne(Wrappers.<User>lambdaQuery().eq(User::getId, id));
        log.info("get user: {}", user);
        
        return userConverter.toBo(user);
    }

}
