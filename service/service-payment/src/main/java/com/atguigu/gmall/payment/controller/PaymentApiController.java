package com.atguigu.gmall.payment.controller;

import com.atguigu.gmall.payment.config.AlipayConfig;
import com.atguigu.gmall.payment.service.AlipayService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/payment/alipay")
public class PaymentApiController {

    @Autowired
    private AlipayService alipayService;

    //http://api.gmall.com/api/payment/alipay/submit/99
    @ApiOperation("支付宝下单")
    @GetMapping("/submit/{orderId}")
    @ResponseBody
    public String submitOrder(@PathVariable Long orderId){
       String from=alipayService.submitOrder(orderId);

        return from;
    }

    //支付宝同步回调处理
    //http://api.gmall.com/api/payment/alipay/callback/return
    @RequestMapping("/callback/return")
    public String returnCallback(){
        return "redirect:"+ AlipayConfig.return_order_url;
    }

}
