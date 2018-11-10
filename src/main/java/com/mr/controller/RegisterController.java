package com.mr.controller;

import com.mr.model.TMallUserAccount;
import com.mr.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by shangruijie on 2018/11/5.
 */
@Controller
public class RegisterController {
    @Autowired
    private UserService userService;

    //注册
    @RequestMapping("register")
    public void register(TMallUserAccount user){
        userService.register(user);
    }

}
