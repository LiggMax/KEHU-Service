package com.ligg.controller;

import com.ligg.pojo.Result;
import com.ligg.pojo.User;
import com.ligg.service.UserService;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping
public class AccountController {

    @Autowired
    private UserService userService;

    /**
     * 登录
     */
    @PostMapping("/login")
    public Result<String> login(String username, String password, HttpSession session) {
        User user = userService.Login(username, password);
        if (user == null){
            return Result.error("用户名或密码错误");
        }
        // 登录成功，将用户信息存入session
        session.setAttribute("loginUser", user);
        return Result.success();
    }

    /**
     * 注册
     */
    @PostMapping("/register")
    public Result<String> register(String username, String password) {
        String register = userService.register(username, password);
        if (register != null){
            return Result.error(register);
        }
        return Result.success();
    }



}
