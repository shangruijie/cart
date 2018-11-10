package com.mr.service.Impl;

import com.mr.mapper.UserMapper;
import com.mr.model.TMallUserAccount;
import com.mr.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by shangruijie on 2018/11/5.
 */
@Service
public class UserServiceImpl implements UserService{
    @Autowired
    private UserMapper userMapper;

    @Override
    public TMallUserAccount queryUser(String userName, String userPswd) {
        return userMapper.queryUser(userName,userPswd);
    }

    @Override
    public void register(TMallUserAccount user) {
        TMallUserAccount user1 = userMapper.findUserById(user.getId());
        if (user1 == null) {
            userMapper.saveUser(user);
        }else {
            return;
        }
    }
}
