package com.atguigu.gmall.all.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CartController {

    //跳转到展示页面
    @GetMapping("/cart.html")
    public String index(){
        return "cart/index";
    }


}
