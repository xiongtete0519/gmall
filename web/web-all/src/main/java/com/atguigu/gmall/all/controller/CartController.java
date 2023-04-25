package com.atguigu.gmall.all.controller;

import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.product.client.ProductFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CartController {

    @Autowired
    private ProductFeignClient productFeignClient;
    /**
     * //添加购物车后跳转页面
     *     //商品图片
     *     //商品名称
     *     //商品数量
     *     //商品id
     */
    @GetMapping("/addCart.html")
    public String addCart(Long skuId, Integer skuNum, Model model){
        SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
        //响应
        model.addAttribute("skuInfo",skuInfo);
        model.addAttribute("skuNum",skuNum);

        return "cart/addCart";
    }

    //跳转到展示页面
    @GetMapping("/cart.html")
    public String index(){
        return "cart/index";
    }


}
