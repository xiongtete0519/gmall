package com.atguigu.gmall.all.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.order.client.OrderFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Controller
@SuppressWarnings("all")
public class OrderController {

    @Autowired
    private OrderFeignClient orderFeignClient;

    //去结算
    @GetMapping("/trade.html")
    public String trade(Model model){
        Result<Map<String,Object>> trade = orderFeignClient.trade();

        //响应
        model.addAllAttributes(trade.getData());
        return "order/trade";
    }

}
