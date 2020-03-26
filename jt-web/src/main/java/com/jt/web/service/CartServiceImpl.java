package com.jt.web.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jt.common.po.Cart;
import com.jt.common.service.HttpClientService;
import com.jt.common.util.MapperUtil;
import com.jt.common.vo.SysResult;

@Service
public class CartServiceImpl implements CartService {
	@Autowired
	private HttpClientService httpClient;

	@SuppressWarnings("unchecked")
	@Override
	public List<Cart> findCartListByUserId(Long userId) {
		String url = "http://cart.jt.com/cart/query/"+userId ;
		List<Cart> carList = null;
		try {
			String result = httpClient.doGet(url);
			System.out.println(result);
			SysResult sysResult = (SysResult) MapperUtil.toObject(result, SysResult.class);
			carList = (List<Cart>) sysResult.getData();
		} catch (Exception e) {
		
			e.printStackTrace();
		}
		return carList;
	}
	//实现购物车数量的刷新
	@Override
	public void updateCart(Cart cart) {
		Long itemId = cart.getItemId();
		Long userId = cart.getUserId();
		Integer num = cart.getNum(); 
		String url = "http://cart.jt.com/cart/update/num/"+itemId+"/"+userId+"/"+num;
		try {
			httpClient.doGet(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//购物车新增
	@Override
	public void saveCart(Cart cart) {
		String url = "http://cart.jt.com/cart/save";
		String json = MapperUtil.toJSON(cart);
		Map<String, String> params = new HashMap<String, String>();
		params.put("cart", json);
		
		try {
			httpClient.doPost(url, params);
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
	}
}
