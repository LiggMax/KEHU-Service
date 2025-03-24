package com.ligg.controller.admin;

import com.ligg.pojo.Admin;
import com.ligg.service.admin.AdminService;
import com.ligg.pojo.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @PostMapping("/login")
    public Result login(@RequestBody Map<String, String> loginForm, HttpServletRequest request) {
        String username = loginForm.get("username");
        String password = loginForm.get("password");

        if (username == null || password == null) {
            return Result.error("用户名和密码不能为空");
        }

        // 登录验证
        Admin admin = adminService.login(username, password);
        if (admin == null) {
            return Result.error("用户名或密码错误");
        }

        // 更新登录信息
        String ip = request.getRemoteAddr();
        adminService.updateLastLoginInfo(admin.getId(), ip);

        // 返回管理员信息（不包含密码）
        Map<String, Object> data = new HashMap<>();
        data.put("id", admin.getId());
        data.put("username", admin.getUsername());
        data.put("nickname", admin.getNickname());
        data.put("avatar", admin.getAvatar());
        data.put("role", "admin");

        return Result.success(data);
    }

    @GetMapping("/info")
    public Result getAdminInfo(@RequestParam Long id) {
        Admin admin = adminService.getById(id);
        if (admin == null) {
            return Result.error("管理员不存在");
        }

        // 返回管理员信息（不包含密码）
        Map<String, Object> data = new HashMap<>();
        data.put("id", admin.getId());
        data.put("username", admin.getUsername());
        data.put("nickname", admin.getNickname());
        data.put("avatar", admin.getAvatar());
        data.put("email", admin.getEmail());
        data.put("phone", admin.getPhone());
        data.put("role", "admin");

        return Result.success(data);
    }
} 