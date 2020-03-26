package com.jt.web.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jt.common.po.User;
import com.jt.common.service.HttpClientService;
import com.jt.common.vo.SysResult;

@Service
public class UserServiceImpl implements UserService{
	@Autowired
	private HttpClientService httpClient;
	@Autowired
	private ObjectMapper objectMapper;
	//发起的httpclient post请求
	@Override
	public void saveUser(User user) {
		// TODO 定义HttpClient post请求的url
		String url = "http://sso.jt.com/user/register";
		Map<String, String> params = new HashMap<String, String>();
		try {
			params.put("username", user.getUsername());
			String md5Pass = 
					DigestUtils.md5DigestAsHex(user.getPassword().getBytes());
			params.put("password", md5Pass);
			params.put("phone", user.getPhone());
			params.put("email", user.getEmail());
			//发送请求 获得回执
			String result = httpClient.doPost(url, params);
			//获取数据后，校验后台执行情况 200表示正确 201表示失败
			SysResult sysResult = 
			objectMapper.readValue(result, SysResult.class);
			if(sysResult.getStatus()!=200) {
				System.out.println("表示后台执行有错"+result);
				throw new RuntimeException();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}
	@Override
	public String findUserByUP(User user) {
		String token = null;
		//定义后台请求url
		String url = "http://sso.jt.com/user/login";
		//定义请求参数  封装为map
		Map<String, String> params = new HashMap<>();
		//在发起httpClient请求之前  将密码进行加密处理
		String md5Pass = DigestUtils.md5DigestAsHex(user.getPassword().getBytes());
		params.put("username", user.getUsername());
		params.put("password", md5Pass);
		try {
			String result = httpClient.doPost(url, params);
			SysResult sysResult = 
			objectMapper.readValue(result, SysResult.class);
			 if(sysResult.getStatus()==200) {
				 token = (String) sysResult.getData();
			 }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return token;
	}

}
