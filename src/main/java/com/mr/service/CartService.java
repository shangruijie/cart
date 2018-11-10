package com.mr.service;

import com.mr.model.TMallShoppingcar;

import java.util.List;
import java.util.Map;

/**
 * Created by shangruijie on 2018/11/9.
 */
public interface CartService {
    List<TMallShoppingcar> listCartByUserId(Integer id);

    void saveCart(TMallShoppingcar cart);

    void updateCartBySkuIdAndUserId(Map<String, Object> cartMap);

    void saveCartList(List<TMallShoppingcar> cartList, Integer id);

    TMallShoppingcar findCartBySkuIdAndUserId(Integer skuId, Integer id);

    void updateCartShfxzBySkuIdAndUserId(Integer skuId, Integer id, String shfxz);
}
