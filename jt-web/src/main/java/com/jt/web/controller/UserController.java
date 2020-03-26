package com.jt.web.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.util.JSONPObject;
import com.jt.common.po.User;
import com.jt.common.vo.SysResult;
import com.jt.web.service.UserService;

import redis.clients.jedis.JedisCluster;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserService userService;
	@Autowired
	private JedisCluster jedisCluster;
	//实现用户页面的通用跳转
	@RequestMapping("/{moduleName}")
	public String index(@PathVariable String moduleName) {
		
		return moduleName;
	}
	//实现用户的登出操作  
		/**
		 * 思路：1.我们需要把redis的用户信息删除 2.cookie的数据也要删除
		 */
		@RequestMapping("/logout")
		public String logout(HttpServletRequest request,HttpServletResponse resposne) {
			Cookie[] cookies = request.getCookies();
			for (Cookie cookie : cookies) {
				if("JT_TICKET".equals(cookie.getName())) {
					String token = cookie.getValue();
					jedisCluster.del(token);
					cookie.setMaxAge(0);
					resposne.addCookie(cookie);
				}
			}
			//重定向时 .html后缀必须要写   因为重定向时重新发起了一次请求
			return "redirect:/index.html";
		}
	/**
	 * springMVC接受参数，可以使用对象接受
	 * 适用场景：当前端使用表单传参  或者可以说前端提交的参数过多的时候
	 * 使用对象封装参数  以减少代码复杂程度
	 * 1.springMVC可以接收string和基本类型
	 * 2.也可以接受对象类型
	 * 原理：先通过request对象的获取参数的方法，然后调用对象的set方法为对象的
	 * 属性赋值
	 * 3.SpringMVC可以为对象的引用赋值
	 * 对象的引用：对象里面的对象
	 * @param user
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/doRegister")
	public SysResult saveUser(User user) {
		try {
			userService.saveUser(user);
			return SysResult.oK();//表示程序执行成功
		} catch (Exception e) {
			e.printStackTrace();
		}
		return SysResult.build(201, "用户注册失败");
	}
	///service/user/doLogin?r=" + Math.random(),
	//实现jt的用户登录
	@RequestMapping("/doLogin")
	@ResponseBody
	public SysResult doLogin(User user,HttpServletResponse response) {
		try {
			//调用业务层方法 返回秘钥
			String token = 
			userService.findUserByUP(user);
			//判断秘钥是否为空 不为空是保存到kookie
			if(!StringUtils.isEmpty(token)) {
				Cookie cookie = new Cookie("JT_TICKET", token);
				cookie.setMaxAge(7*24*3600);
				//cookie使用权限的配置 (使用范围   "/"表示整个应用都可以使用)
				cookie.setPath("/");
				response.addCookie(cookie);
				//操作无误后正确返回
				return SysResult.oK();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return SysResult.build(201, "用户登录失败");
	} 
	
	// 登录之后通过js代码返回首页   
	//当我们重新访问首页时   发起ajax请求来实现用户信息的回显（具体js代码在login.js和jt.js）
	//请求地址为"http://sso.jt.com/user/query/" + _ticket,
	//拓展   ： @ResponseBody注解的作用
	//在RequestMapping中 return返回值默认解析为跳转路径，
	//如果你此时想让Controller返回一个字符串或者对象到前台 就会报404 not response的错误。
	//当加上@ResponseBody注解后不会解析成跳转地址 会解析成相应的json格式的对象 集合 字符串或者xml等直接返回给前台 
	//可以通过 ajax 的“success”：fucntion(data){} data直接获取到。
	//总结  ：  如果后台返回的是对象   那么就会解析成json格式的对象  总之   你封装的是什么格式就会解析成相应的格式
	@RequestMapping("/query/{token}")
	public JSONPObject findUserByToken(String callback,
			@PathVariable String token) {
		String userJSON = jedisCluster.get(token);
		SysResult sysResult = SysResult.oK(userJSON);
		return new JSONPObject(callback, sysResult);
	}
	
	
}
