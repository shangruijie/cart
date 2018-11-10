package com.mr.service.Impl;

import com.mr.mapper.CartMapper;
import com.mr.model.TMallShoppingcar;
import com.mr.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by shangruijie on 2018/11/9.
 */
@Service
public class CartServiceImpl implements CartService{

    @Autowired
    private CartMapper cartMapper;

    @Override
    public List<TMallShoppingcar> listCartByUserId(Integer id) {
        return cartMapper.listCartByUserId(id);
    }

    @Override
    public void saveCart(TMallShoppingcar cart) {
        cartMapper.saveCart(cart);
    }

    @Override
    public void updateCartBySkuIdAndUserId(Map<String, Object> cartMap) {
        cartMapper.updateCartBySkuIdAndUserId(cartMap);
    }

    @Override
    public void saveCartList(List<TMallShoppingcar> cartList, Integer userId) {
        Map<String,Object> map = new HashMap<>();
        map.put("cartList",cartList);
        map.put("userId",userId);
        cartMapper.saveCartList(map);
    }

    @Override
    public TMallShoppingcar findCartBySkuIdAndUserId(Integer skuId, Integer userId) {
        return cartMapper.findCartBySkuIdAndUserId(skuId,userId);
    }

    @Override
    public void updateCartShfxzBySkuIdAndUserId(Integer skuId, Integer userId, String shfxz) {
        Map<String,Object> map = new HashMap<>();
        map.put("skuId",skuId);
        map.put("userId",userId);
        map.put("shfxz",shfxz);
        cartMapper.updateCartShfxzBySkuIdAndUserId(map);
    }
}
