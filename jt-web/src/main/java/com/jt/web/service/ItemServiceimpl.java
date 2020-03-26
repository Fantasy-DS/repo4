package com.jt.web.service;

import java.util.HashMap;
import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jt.common.po.Item;
import com.jt.common.po.ItemDesc;
import com.jt.common.service.HttpClientService;

@Service
public class ItemServiceimpl implements ItemService{
	@Autowired
	private HttpClientService httpClient;
	@Autowired
	private ObjectMapper objectMapper;
	public Item findItemById(Long itemId) {
		Item item = null;
		//定义远程访问后台的url
		String url = "http://manage.jt.com/web/item/findItemById";
		//定义请求的参数
		Map<String, String> params = new HashMap<>();
		params.put("itemId", itemId+"");
		try {
			String result = httpClient.doGet(url, params);
			System.out.println(result);
			/*
			 * 因为后台给我们返回的是一个json串
			 * 所以我们需要将json串转化为对象
			 * json转对象是调用对象的set方法   对象转json是调用对象的get方法
			 */
			item = objectMapper.readValue(result, Item.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return item;
	}
	public ItemDesc findItemDesc(Long itemId) {
		ItemDesc itemDesc = null;
		//定义远程访问后台的url
		String url = "http://manage.jt.com/web/item/findItemDescById";
		//定义请求的参数
		Map<String, String> params = new HashMap<>();
		params.put("itemId", itemId+"");
		try {
			String result = httpClient.doGet(url, params);
			System.out.println(result);
			/*
			 * 因为后台给我们返回的是一个json串
			 * 所以我们需要将json串转化为对象
			 * json转对象是调用对象的set方法   对象转json是调用对象的get方法
			 */
			itemDesc = objectMapper.readValue(result, ItemDesc.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return itemDesc;
	}
}
