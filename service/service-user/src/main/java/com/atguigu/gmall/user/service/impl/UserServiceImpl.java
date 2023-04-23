package com.atguigu.gmall.user.service.impl;

import com.atguigu.gmall.model.user.UserInfo;
import com.atguigu.gmall.user.mapper.UserInfoMapper;
import com.atguigu.gmall.user.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

@Service
@SuppressWarnings("all")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserInfoMapper userInfoMapper;

    //用户登录
    @Override
    public UserInfo login(UserInfo userInfo) {
        //select * from userinfo where login_name=? and passwd=?

        //处理密码加密
        String newPass = DigestUtils.md5DigestAsHex(userInfo.getPasswd().getBytes());
        //封装条件
        LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserInfo::getLoginName,userInfo.getLoginName())
                .eq(UserInfo::getPasswd,newPass);
        UserInfo user = userInfoMapper.selectOne(wrapper);

        return user;
    }
}
