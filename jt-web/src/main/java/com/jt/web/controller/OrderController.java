package com.jt.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.jt.common.po.Cart;
import com.jt.common.po.User;
import com.jt.common.util.UserThreadLocalUtil;
import com.jt.web.service.CartService;

@Controller
@RequestMapping("/order")
public class OrderController {
	@Autowired
	private CartService cartService;
	@RequestMapping("/create")
	public String create(Model model) {
		//准备用户的购物车记录
		Long userId = UserThreadLocalUtil.get().getId();
		//根据userId获取购物车记录
		List<Cart> cartList = cartService.findCartListByUserId(userId);
		model.addAttribute("carts",cartList);
		return "order-cart";
	}
}
