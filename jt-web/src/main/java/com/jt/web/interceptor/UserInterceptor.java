package com.jt.web.interceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.jt.common.po.User;
import com.jt.common.util.MapperUtil;
import com.jt.common.util.UserThreadLocalUtil;

import redis.clients.jedis.JedisCluster;

public class UserInterceptor implements HandlerInterceptor{
	@Autowired
	private JedisCluster jedisCluster;
	//所谓handler（处理器）就是从控制层到dao层的业务代码
	//此方法执行在请求进入controller之前 
	//返回值: true代表请求放行   false代表请求拦截
	/**
	 * 实现思路：
	 * 1.通过request对象获取cookie
	 * 2.从cookie中获取token数据
	 * 如果没有数据则证明用户没有登录
	 * 3.从token数据中获取redis中的用户数据 如果redis中没有数据
	 * 则证明用户没有登录 重定向到登录页面
	 * 4.如果redis中有数据，则证明用户已经登录 页面正确跳转
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		//1.获取cookie
		Cookie[] cookies = request.getCookies();
		String token = null;
		for(Cookie cookie : cookies) {
			if("JT_TICKET".equals(cookie.getName())) {
				token = cookie.getValue();
				break;
			}
		}
		//2.判断是否有记录
		if(!StringUtils.isEmpty(token)) {
			//判断redis中是否有记录数据
			String userJSON = jedisCluster.get(token);
			if(!StringUtils.isEmpty(userJSON)) {
				User user = (User) MapperUtil.toObject(userJSON, User.class);
				Long userId = user.getId();
				user.setId(userId);
				//动态获取用户id  用户每发起一次请求都会重新创建一个request对象  
				//每次都会往session中注册一个新对象 数据都是不同的
				//request.getSession().setAttribute("JT_WEB_USER", userId);
				//使用threadlocal储存数据
				UserThreadLocalUtil.set(user);
				return true;
			}
		} 
		//3.证明用户没有登录 需要重定向到用户的登录页面
		response.sendRedirect("/user/login.html");
		return false;
	}
	//此方法执行在handler之后  就是业务代码完成之后
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		
		
	}
	//此方法执行在视图解析器之前
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
//	request.getSession().removeAttribute("JT_WEB_USER");
	UserThreadLocalUtil.remove();	
	}
	
}
