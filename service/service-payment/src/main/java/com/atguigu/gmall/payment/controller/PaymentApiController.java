package com.atguigu.gmall.payment.controller;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.enums.PaymentType;
import com.atguigu.gmall.model.payment.PaymentInfo;
import com.atguigu.gmall.payment.config.AlipayConfig;
import com.atguigu.gmall.payment.service.AlipayService;
import com.atguigu.gmall.payment.service.PaymentService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/api/payment/alipay")
public class PaymentApiController {

    @Autowired
    private AlipayService alipayService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private RedisTemplate redisTemplate;

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

    //异步回调
    // http://n8sa9h.natappfree.cc/api/payment/alipay/callback/notify
    @PostMapping("/callback/notify")
    public String notifyCallBack(@RequestParam Map<String, String> paramsMap){

        //实现验签
        boolean signVerified = false; //调用SDK验证签名
        try {
            signVerified = AlipaySignature.rsaCheckV1(paramsMap,
                    AlipayConfig.alipay_public_key,
                    AlipayConfig.charset,
                    AlipayConfig.sign_type);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        //获取订单交易号
        String outTradeNo = paramsMap.get("out_trade_no");
        //获取总金额
        String totalAmount = paramsMap.get("total_amount");
        //获取appId
        String appId = paramsMap.get("app_id");
        //获取当前支付的状态
        String tradeStatus = paramsMap.get("trade_status");
        //获取notify_id
        String notifyId = paramsMap.get("notify_id");

        if(signVerified){
            // TODO 验签成功后，按照支付结果异步通知中的描述，对支付结果中的业务内容进行二次校验，校验成功后在response中返回success并继续商户自身业务处理，校验失败返回failure
            PaymentInfo paymentInfo=paymentService.getPaymentInfo(outTradeNo, PaymentType.ALIPAY.name());
            //二次校验
            if(paymentInfo!=null&&new BigDecimal("0.01").compareTo(new BigDecimal(totalAmount))==0
                    &&AlipayConfig.app_id.equals(appId)){
                //设置支付记录状态
                //只有交易通知状态为 TRADE_SUCCESS 或 TRADE_FINISHED 时，支付宝才会认定为买家付款成功。
                if("TRADE_SUCCESS".equals(tradeStatus)||"TRADE_FINISHED".equals(tradeStatus)){

                    //存储notify_id保证异步处理的幂等性   setnx命令
                    Boolean flag = redisTemplate.opsForValue().setIfAbsent(notifyId, notifyId, 1462, TimeUnit.MINUTES);

                    if (flag) {
                        paymentService.updatePaymentInfo(outTradeNo, PaymentType.ALIPAY.name(),paramsMap);
                        return "success";
                    }else{
                        return "success";
                    }
                }
            }
        }else{
            // TODO 验签失败则记录异常日志，并在response中返回failure.
            return "failure";
        }

        return "failure";
    }

    @ApiOperation("发起退款")
    @GetMapping("/refund/{orderId}")
    @ResponseBody
    public Result refund(@PathVariable Long orderId){
        boolean flag = alipayService.refund(orderId);

        return Result.ok(flag);
    }


}
