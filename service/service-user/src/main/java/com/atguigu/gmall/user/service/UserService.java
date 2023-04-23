package com.atguigu.gmall.user.service;

import com.atguigu.gmall.model.user.UserInfo;

public interface UserService {
    //用户登录
    UserInfo login(UserInfo userInfo);
}
