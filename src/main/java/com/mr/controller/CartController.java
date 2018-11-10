package com.mr.controller;
import com.mr.model.TMallShoppingcar;
import com.mr.model.TMallUserAccount;
import com.mr.service.CartService;
import com.mr.util.MyCookieUtils;
import com.mr.util.MyJsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Created by shangruijie on 2018/11/7.
 */
@Controller
public class CartController {
    @Autowired
    private CartService cartService;
    @Autowired
    private RedisTemplate redisTemplate;
    //获取合计值
    public static Double getHj(TMallShoppingcar cart){
        BigDecimal jg = new BigDecimal(cart.getSkuJg() + "");
        BigDecimal thShl = new BigDecimal(cart.getTjshl());
        double hj = thShl.multiply(jg).doubleValue();
        return hj;
    }
    //添加购物车
    @RequestMapping("saveCart")     //@CookieValue从cookie对象中获取名为key的cookie的值
    public String saveCart(ModelMap map, TMallShoppingcar cart, HttpSession session, @CookieValue(value = "cookieCartList",required = false) String cookieCartList,HttpServletRequest request , HttpServletResponse response){
        /*BigDecimal jg = new BigDecimal(cart.getSkuJg() + "");
        BigDecimal tjshl = new BigDecimal(cart.getTjshl());*/
        cart.setHj(getHj(cart));
        //定义购物车集合
        List<TMallShoppingcar> cartList = new ArrayList<TMallShoppingcar>();
        //判断是否登陆
        TMallUserAccount user = (TMallUserAccount)session.getAttribute("user");
        if (user==null) {//未登录
            if (StringUtils.isBlank(cookieCartList)) {
                cartList.add(cart);
            }else {
                cartList = MyJsonUtil.jsonToList(cookieCartList,TMallShoppingcar.class);
                boolean b1 = false;
                for (int i = 0; i < cartList.size(); i++) {
                    if (cartList.get(i).getSkuId() == cart.getSkuId()) {
                        b1 = true;
                    }
                }
                if (b1) {
                    for (int i = 0; i < cartList.size(); i++) {
                        if (cartList.get(i).getSkuId() == cart.getSkuId()) {
                            cartList.get(i).setTjshl(cartList.get(i).getTjshl() + cart.getTjshl());
                            /*BigDecimal jg1 = new BigDecimal(cartList.get(i).getSkuJg() + "");
                            BigDecimal shl1 = new BigDecimal(cartList.get(i).getTjshl());*/
                            cartList.get(i).setHj(getHj(cartList.get(i)));
                        }
                    }
                }else {
                    cartList.add(cart);
                }
            }
            MyCookieUtils.setCookie(request,response,"cookieCartList",MyJsonUtil.objectToJson(cartList),3*24*60*60,true);
        }else {//登陆
            //判断数据库中是否有数据,当前用户
            //获取数据
            cartList = cartService.listCartByUserId(user.getId());
            if (cartList != null && cartList.size() > 0) {//有数据
                boolean b2 = false;
                //循环遍历,如果存在
                for (int i = 0; i < cartList.size(); i++) {
                    if (cartList.get(i).getSkuId() == cart.getSkuId()) {
                        b2 = true;
                    }
                }
                if (b2) {//存在数据
                    //更新数据
                    for (int i = 0; i < cartList.size(); i++) {
                        if (cartList.get(i).getSkuId() == cart.getSkuId()) {
                            Map<String,Object> cartMap = new HashMap<>();
                            cartMap.put("skuId",cartList.get(i).getSkuId());
                            cartMap.put("userId",user.getId());
                            cartMap.put("tjshl",cartList.get(i).getTjshl() + cart.getTjshl());
                            cartList.get(i).setTjshl(cartList.get(i).getTjshl() + cart.getTjshl());
                            /*BigDecimal jg = new BigDecimal(cartList.get(i).getSkuJg() + "");
                            BigDecimal shl = new BigDecimal(cartList.get(i).getTjshl());*/
                            cartMap.put("hj",getHj(cartList.get(i)));
                            cartService.updateCartBySkuIdAndUserId(cartMap);
                        }
                    }
                }else {
                    //添加数据
                    cart.setYhId(user.getId());
                    cartService.saveCart(cart);
                }
            }else {
                //添加数据
                cart.setYhId(user.getId());
                cartService.saveCart(cart);
            }
            //更新redis (清除redis中cart的list，当前用户)
            redisTemplate.delete("redisCartListUser"+user.getId());

            //用户登陆之后，将数据保存在redis中
            //当前用户的key如何确定
            //redisTemplate.opsForValue().set("redisCartListUser"+user.getId(),cartList);
        }
        map.put("cart",cart);
        return "cart-success";
    }
    //迷你购物车
    @RequestMapping("findMiniCart")
    public String findMiniCart(HttpSession session, ModelMap map,@CookieValue(value = "cookieCartList",required = false) String cookieCartList){
        List<TMallShoppingcar> cartList = new ArrayList<>();
        //判断是否登录
        TMallUserAccount user = (TMallUserAccount)session.getAttribute("user");
        if(user == null){//未登录
            cartList = MyJsonUtil.jsonToList(cookieCartList, TMallShoppingcar.class);
        }else {//登陆
            //从redis中获取数据
            cartList = (List<TMallShoppingcar>)redisTemplate.opsForValue().get("redisCartListUser"+user.getId());
            if (cartList == null || cartList.size() == 0) {//没有数据
                cartList = cartService.listCartByUserId(user.getId());
                redisTemplate.opsForValue().set("redisCartListUser"+user.getId(),cartList);
                //直接将数据返回
                //cartList = (List<TMallShoppingcar>)redisTemplate.opsForValue().get("redisCartListUser" + user.getId());
            }else {//没有，同步redis
                //查询数据库，通过用户
                //cartList = cartService.listCartByUserId(user.getId());
                //redisTemplate.opsForValue().set("redisCartListUser"+user.getId(),cartList);
            }
        }
        Integer countNum = 0;
        for (int i = 0; i < cartList.size(); i++) {
            countNum += cartList.get(i).getTjshl();
        }
        map.put("cartList",cartList);
        map.put("countNum",countNum);
        map.put("hjSum",getSum(cartList));
        return "miniCartInner";
    }
    //BigDecimal
    public BigDecimal getSum(List<TMallShoppingcar> cartList){
        BigDecimal sum = new BigDecimal("0");
        for (int i = 0; i < cartList.size(); i++) {
            if (cartList.get(i).getShfxz().equals("1")) {//如果选中

                sum = sum.add(new BigDecimal(cartList.get(i).getHj() + ""));
            }
        }
        return sum;
    }
    //去购物车页面
    @RequestMapping("toCartPage")
    public String toCartPage(ModelMap map, HttpSession session, @CookieValue(value = "cookieCartList",required = false) String cookieCartList){
        List<TMallShoppingcar> cartList = new ArrayList<>();
        //判断是否登录
        TMallUserAccount user = (TMallUserAccount)session.getAttribute("user");
        if(user == null){//未登录
            cartList = MyJsonUtil.jsonToList(cookieCartList, TMallShoppingcar.class);
        }else {//登陆
            //从redis中获取数据
            cartList = (List<TMallShoppingcar>)redisTemplate.opsForValue().get("redisCartListUser"+user.getId());
            if (cartList == null || cartList.size() == 0) {//没有数据

                cartList = cartService.listCartByUserId(user.getId());
                redisTemplate.opsForValue().set("redisCartListUser"+user.getId(),cartList);

                //直接将数据返回
                //cartList = (List<TMallShoppingcar>)redisTemplate.opsForValue().get("redisCartListUser" + user.getId());

            }else {//没有，同步redis
                //查询数据库，通过用户
                //cartList = cartService.listCartByUserId(user.getId());
                //redisTemplate.opsForValue().set("redisCartListUser"+user.getId(),cartList);
            }
        }
        /*Integer countNum = 0;
        for (int i = 0; i < cartList.size(); i++) {
            countNum += cartList.get(i).getTjshl();
        }*/
        map.put("cartList",cartList);
        //map.put("countNum",countNum);
        map.put("hjSum",getSum(cartList));
        return "cartList";
    }

    /*@RequestMapping("changeShfxz")
    public String changeShfxz(HttpSession session, HttpServletResponse response, HttpServletRequest request, Integer skuId, String shfxz, ModelMap map, @CookieValue(value = "cookieCartList",required = false) String cookieCartList){
        List<TMallShoppingcar> cartList = new ArrayList<>();
        //判断是否登录
        TMallUserAccount user = (TMallUserAccount)session.getAttribute("user");
        if (user != null) {//登陆

        }else {//未登录
            cartList = MyJsonUtil.jsonToList(cookieCartList,TMallShoppingcar.class);
            for (int i = 0; i < cartList.size(); i++) {
                //如果skuId一样，修改该对象的状态
                if (cartList.get(i).getSkuId() == skuId) {
                    cartList.get(i).setShfxz(shfxz);
                }
            }
            //更新cookie
            MyCookieUtils.setCookie(request,response,"cookieCartList",MyJsonUtil.objectToJson(cartList),3*24*60*60,true);
        }
        map.put("cartList",cartList);
        map.put("hjSum",getSum(cartList));
        return "cartListInner";
    }*/
    @RequestMapping("changeShfxz")
    public String changeShfxz(HttpServletResponse response ,HttpServletRequest request,
                              int skuId , String shfxz , ModelMap map,HttpSession session,
                              @CookieValue(value = "cookieCartList",required = false) String cookieCartList){
        List<TMallShoppingcar> cartList = new ArrayList<>();
        //判断是否登录
        TMallUserAccount user = (TMallUserAccount)session.getAttribute("user");
        if(user != null){//登录
            //通过skuId 修改 cart
            //从reids中获取到数据
            cartList =  (List<TMallShoppingcar>)redisTemplate.opsForValue().get("redisCartListUser"+user.getId());
            //更新数据库
            for (int i = 0; i < cartList.size(); i++) {
                if(cartList.get(i).getSkuId() == skuId){
                    //修改数据库的状态
                    cartService.updateCartShfxzBySkuIdAndUserId(skuId,user.getId(),shfxz);
                    //修改
                    cartList.get(i).setShfxz(shfxz);
                }
            }
            //同步redis中
            redisTemplate.opsForValue().set("redisCartListUser"+user.getId(),cartList);
        }else{//未登录
            cartList = MyJsonUtil.jsonToList(cookieCartList,TMallShoppingcar.class);
            for (int i = 0; i < cartList.size(); i++) {
                //如果skuId一样的话，修改该对象的状态。
                if(cartList.get(i).getSkuId() == skuId){
                    cartList.get(i).setShfxz(shfxz);
                }
            }
            //更新cookie
            MyCookieUtils.setCookie(request,response,"cookieCartList",
                    MyJsonUtil.objectToJson(cartList),3*24*60*60,true);
        }
        map.put("cartList",cartList);
        map.put("hjSum",getSum(cartList));
        return "cartListInner";
    }


}
