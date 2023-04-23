package com.atguigu.gmall.all.controller;

import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

    @ApiOperation("跳转到登录界面")
    @GetMapping("/login.html")
    public String toLogin(String originUrl, Model model){
        model.addAttribute("originUrl",originUrl);
        System.out.println(originUrl);
        return "login";
    }
}
