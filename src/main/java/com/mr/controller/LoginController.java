package com.mr.controller;

import com.mr.model.TMallShoppingcar;
import com.mr.model.TMallUserAccount;
import com.mr.service.CartService;
import com.mr.service.UserService;
import com.mr.util.MyCookieUtils;
import com.mr.util.MyJsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by shangruijie on 2018/11/5.
 */
@Controller
public class LoginController {
    @Autowired
    private UserService userService;

    @Autowired
    private CartService cartService;

    @Autowired
    private RedisTemplate redisTemplate;

    //登陆
    @RequestMapping("login")
    public String login(String userName, String userPswd, @CookieValue(value = "cookieCartList",required = false) String cookieCartList, HttpSession session, HttpServletRequest request, HttpServletResponse response){
        //通过用户名、密码查询对象
        TMallUserAccount user = userService.queryUser(userName,userPswd);
        if (user==null) {//登陆失败
            return "redirect:toLoginPage.do";
        }else {//登陆成功
            session.setAttribute("user",user);
            String yhMch = user.getYhMch();
            //将用户名称放入cookie对象中
            MyCookieUtils.setCookie(request,response,"yhMch",yhMch,24*60*60,true);
            //更新购物车
            //从cookie中查询数据
            if (!StringUtils.isBlank(cookieCartList)) {//有数据
                //将cookie中的数据添加到数据库
                List<TMallShoppingcar> cartListCookie = MyJsonUtil.jsonToList(cookieCartList, TMallShoppingcar.class);
                List<TMallShoppingcar> cartListDb = cartService.listCartByUserId(user.getId());
                //循环cookie
                for (int i = 0; i < cartListCookie.size(); i++) {//cookie
                    //如何判断当前对象是否重复
                    //根据当前对象的skuid和用户id查询数据
                    TMallShoppingcar cart = cartService.findCartBySkuIdAndUserId(cartListCookie.get(i).getSkuId(),user.getId());
                    if (cart != null) {//重复
                        //更新
                        Map<String,Object> cartMap = new HashMap<>();
                        cartMap.put("skuId",cartListCookie.get(i).getSkuId());
                        cartMap.put("userId",user.getId());
                        //修改对象的数量
                        cartMap.put("tjshl",cartListCookie.get(i).getTjshl() + cart.getTjshl());
                        cartListCookie.get(i).setTjshl(cartListCookie.get(i).getTjshl()+cart.getTjshl());

                        /*BigDecimal jg = new BigDecimal(cartListCookie.get(i).getSkuJg() + "");
                        BigDecimal shl = new BigDecimal(cartListCookie.get(i).getTjshl() + cart.getTjshl());*/

                        cartMap.put("hj",CartController.getHj(cartListCookie.get(i)));
                        cartService.updateCartBySkuIdAndUserId(cartMap);
                    }else {
                        cartListCookie.get(i).setYhId(user.getId());
                        cartService.saveCart(cartListCookie.get(i));
                    }
                }
                //清空cookie中的购物车
                MyCookieUtils.deleteCookie(request,response,"cookieCartList");
                //清空redis中的购物车
                redisTemplate.delete("redisCartListUser"+user.getId());
            }
            return "redirect:toIndexPage.do";
        }
    }
    //登出
    @RequestMapping("logout")
    public String logout(HttpSession session){
        session.removeAttribute("user");
        return "redirect:toLoginPage.do";
    }

}
