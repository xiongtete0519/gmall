package com.atguigu.gmall.cart.controller;

import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.util.AuthContextHolder;
import com.atguigu.gmall.model.cart.CartInfo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartApiController {

    @Autowired
    private CartService cartService;

    @ApiOperation("更新选中状态")
    @GetMapping("/checkCart/{skuId}/{isChecked}")
    public Result checkCart(@PathVariable Long skuId,
                            @PathVariable Integer isChecked,
                            HttpServletRequest request){
        //获取用户id
        String userId = AuthContextHolder.getUserId(request);
        //判断
        if(StringUtils.isEmpty(userId)){
            userId=AuthContextHolder.getUserTempId(request);
        }
        //实现状态的更改
        cartService.checkCart(userId,skuId,isChecked);


        return Result.ok();
    }

    @ApiOperation("展示购物车")
    @GetMapping("/cartList")
    public Result cartList(HttpServletRequest request){
        //获取用户id
        String userId = AuthContextHolder.getUserId(request);
        //获取用户临时id
        String userTempId = AuthContextHolder.getUserTempId(request);
        //查询购物车
        List<CartInfo> cartInfoList=cartService.cartList(userId,userTempId);

        return Result.ok(cartInfoList);
    }

    @ApiOperation("加入购物车")
    @GetMapping("/addToCart/{skuId}/{skuNum}")
    public Result addToCart(@PathVariable Long skuId,
                            @PathVariable Integer skuNum,
                            HttpServletRequest request){

        //获取用户id
        String userId = AuthContextHolder.getUserId(request);
        //判断
        if(StringUtils.isEmpty(userId)){
            userId=AuthContextHolder.getUserTempId(request);
        }
        //加入购物车
        cartService.addToCart(skuId,skuNum,userId);

        return Result.ok();
    }

}
