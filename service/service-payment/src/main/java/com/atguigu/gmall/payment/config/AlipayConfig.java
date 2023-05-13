package com.atguigu.gmall.payment.config;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AlipayConfig {
    //支付宝网关
    @Value("${alipay_url}")
    private String alipay_url;
    //应用id
    @Value("${app_id}")
    private String app_id;
    //应用私钥
    @Value("${app_private_key}")
    private String app_private_key;
    //数据格式
    public static final String format="json";
    //字符编码集
    public static final String charset="UTF-8";
    //支付宝公钥
    @Value("${alipay_public_key}")
    private String alipay_public_key;
    //签名算法
   public static final String sign_type="RSA2";

    @Bean
    public AlipayClient alipayClient(){
        return new DefaultAlipayClient(alipay_url,
                app_id,
                app_private_key,
                format,
                charset,
                alipay_public_key,
                sign_type);
    }
}
