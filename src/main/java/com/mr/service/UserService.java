package com.mr.service;

import com.mr.model.TMallUserAccount;

/**
 * Created by shangruijie on 2018/11/5.
 */
public interface UserService {
    TMallUserAccount queryUser(String userName, String userPswd);

    void register(TMallUserAccount user);
}
