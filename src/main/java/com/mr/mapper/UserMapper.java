package com.mr.mapper;

import com.mr.model.TMallUserAccount;
import org.apache.ibatis.annotations.Param;

/**
 * Created by shangruijie on 2018/11/5.
 */
public interface UserMapper {
    TMallUserAccount queryUser(@Param("userName") String userName, @Param("userPswd") String userPswd);

    void saveUser(TMallUserAccount user);

    TMallUserAccount findUserById(Integer id);
}
