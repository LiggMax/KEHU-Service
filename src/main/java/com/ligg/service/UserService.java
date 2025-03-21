package com.ligg.service;

import com.ligg.pojo.User;

public interface UserService {
    User Login(String username, String password);

    String register(String username, String password);
}
