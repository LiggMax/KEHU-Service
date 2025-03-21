package com.ligg.controller;


import com.ligg.pojo.Result;
import com.ligg.pojo.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/current-user")
    public Result<User> getCurrentUser(HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser != null) {
            return Result.success(loginUser);
        }
        return Result.error("未登录");
    }

    /**
     * 登出
     */
    @PostMapping("/logout")
    public Result<String> logout(HttpSession session) {
        session.removeAttribute("loginUser");
        return Result.success();
    }
}
