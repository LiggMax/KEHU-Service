package com.ligg.controller.admin;

import com.ligg.mapper.UserMapper;
import com.ligg.pojo.Admin;
import com.ligg.pojo.Result;
import com.ligg.pojo.User;
import com.ligg.service.admin.AdminService;
import jakarta.servlet.http.HttpSession;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/account")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private UserMapper userMapper;

    @PostMapping("/adminLogin")
    public Result adminLogin(@NonNull @RequestBody Map<String,Object> loginMap, HttpSession session) {
        String username = (String) loginMap.get("username");
        String password = (String) loginMap.get("password");

        Admin adminLogin = adminService.login(username, password);
        if (adminLogin == null){
            return Result.error("用户名或密码错误");
        }
        session.setAttribute("loginAdmin",adminLogin);
        return Result.success(adminLogin);
    }

    @GetMapping("/adminInfo")
    public Result getAdminInfo(HttpSession session) {
        Admin loginAdmin = (Admin) session.getAttribute("loginAdmin");
        if (loginAdmin == null) {
            return Result.error("未登录");
        }
        // 出于安全考虑，清除密码信息
        loginAdmin.setPassword(null);
        return Result.success(loginAdmin);
    }

    @DeleteMapping("/logout")
    public Result<?> logout(HttpSession session) {
        session.removeAttribute("loginAdmin");
        return Result.success();
    }

    @GetMapping("/userList")
    public Result getUserList(HttpSession session) {
        // 检查管理员是否登录
        Admin loginAdmin = (Admin) session.getAttribute("loginAdmin");
        if (loginAdmin == null) {
            return Result.error("未登录");
        }

        try {
            List<User> users = userMapper.getAllUsers();
            // 出于安全考虑，清除所有用户的密码信息
            users.forEach(user -> user.setPassword(null));
            return Result.success(users);
        } catch (Exception e) {
            log.error("获取用户列表失败", e);
            return Result.error("获取用户列表失败");
        }
    }
}
