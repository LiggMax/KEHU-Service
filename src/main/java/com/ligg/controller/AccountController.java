package com.ligg.controller;

import com.ligg.pojo.Result;
import com.ligg.pojo.User;
import com.ligg.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class AccountController {

    @Autowired
    private UserService userService;

    /**
     * 登录
     */
    @PostMapping("/login")
    public Result<String> login(User user) {
        String login = userService.Login(user.getUsername(), user.getPassword());
        if (login != null){
            return Result.error(login);
        }
        return Result.success();
    }

    /**
     * 注册
     */
    @PostMapping("/register")
    public Result<String> register(User user) {
        userService.register(user);
        return Result.success();
    }
}
