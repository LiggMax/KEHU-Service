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
    public User Login(String username, String password) {
        return userMapper.getUser(username, password); // 直接返回用户对象，如果未找到则为null
    }

    @Override
    public String register(String username, String password) {
        if (userMapper.getUserByUsername(username) != null) {
            return "用户名已存在";
        }
        userMapper.register(username, password);
        return null;
    }

}
