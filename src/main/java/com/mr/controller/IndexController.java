package com.mr.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by shangruijie on 2018/11/5.
 */
@Controller
public class IndexController {

    //去主页面
    @RequestMapping("toIndexPage")
    public String toIndexPage(){
        return "index";
    }

    //去登陆页面
    @RequestMapping("toLoginPage")
    public String toLoginPage(){
        return "login";
    }

    //去注册页面
    @RequestMapping("toRegisterPage")
    public String toRegisterPage(){
        return "register";
    }

}
