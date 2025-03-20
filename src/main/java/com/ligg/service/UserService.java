package com.ligg.service;

import com.ligg.pojo.User;

public interface UserService {
    String Login(String username,String password);

    String register(User user);
}
