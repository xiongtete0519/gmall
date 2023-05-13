package com.atguigu.gmall.payment.controller;

import com.atguigu.gmall.payment.service.AlipayService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payment/alipay")
public class PaymentApiController {

    @Autowired
    private AlipayService alipayService;

    //http://api.gmall.com/api/payment/alipay/submit/99
    @ApiOperation("支付宝下单")
    @GetMapping("/submit/{orderId}")
    public String submitOrder(@PathVariable Long orderId){
       String from=alipayService.submitOrder(orderId);

        return from;
    }

}
