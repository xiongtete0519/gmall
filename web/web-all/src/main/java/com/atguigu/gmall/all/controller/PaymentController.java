package com.atguigu.gmall.all.controller;

import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.order.client.OrderFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@SuppressWarnings("all")
public class PaymentController {

    @Autowired
    private OrderFeignClient orderFeignClient;

    //显示支付页面
    @GetMapping("/pay.html")
    public String pay(Long orderId, Model model){
        OrderInfo orderInfo = orderFeignClient.getOrderInfo(orderId);
        model.addAttribute("orderInfo",orderInfo);
        return "payment/pay";
    }

}
