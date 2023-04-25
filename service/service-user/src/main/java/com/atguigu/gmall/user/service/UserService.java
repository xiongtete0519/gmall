package com.atguigu.gmall.user.service;

import com.atguigu.gmall.model.user.UserAddress;
import com.atguigu.gmall.model.user.UserInfo;

import java.util.List;

public interface UserService {
    //用户登录
    UserInfo login(UserInfo userInfo);

    //查询用户地址列表
    List<UserAddress> findUserAddressListByUserId(Long userId);
}
