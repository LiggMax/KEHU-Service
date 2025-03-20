package com.ligg.service.impl;

import com.ligg.mapper.UserMapper;
import com.ligg.pojo.User;
import com.ligg.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public String Login(String username, String password) {
        User user = userMapper.getUser(username,password);
        if (user == null) {
            return "用户名或密码错误";
        }
        return null;
    }

    @Override
    public String register(User user) {
        userMapper.register(user);
        return null;
    }
}
