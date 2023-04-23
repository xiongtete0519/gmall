package com.atguigu.gmall.user.controller;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.util.IpUtil;
import com.atguigu.gmall.model.user.UserInfo;
import com.atguigu.gmall.user.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/user/passport")
public class PassportController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping("/login")
    public Result logout(@RequestBody UserInfo userInfo, HttpServletRequest request) {

        //认证
        UserInfo user = userService.login(userInfo);
        if (user != null) {
            //生成token
            String token = UUID.randomUUID().toString().replaceAll("-", "");
            //获取当前登录用户的ip
            String ip = IpUtil.getIpAddress(request);
            JSONObject object = new JSONObject();
            object.put("ip",ip);
            object.put("userId",user.getId());

            //存储到Redis
            redisTemplate.opsForValue().set(RedisConst.USER_LOGIN_KEY_PREFIX+token,
                    object.toJSONString(),
                    RedisConst.USERKEY_TIMEOUT,
                    TimeUnit.SECONDS);

            //返回页面信息
            Map<String,Object> resultMap=new HashMap<>();
            resultMap.put("nickName",user.getNickName());
            resultMap.put("token",token);

            return Result.ok(resultMap);
        } else {
            return Result.fail().message("用户名或者密码错误！！！");
        }

    }

}
