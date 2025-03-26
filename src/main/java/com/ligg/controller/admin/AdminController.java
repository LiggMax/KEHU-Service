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
    public Result getUserList(@RequestParam(required = false) String username, HttpSession session) {
        // 检查管理员是否登录
        Admin loginAdmin = (Admin) session.getAttribute("loginAdmin");
        if (loginAdmin == null) {
            return Result.error("未登录");
        }

        try {
            List<User> users;
            if (username != null && !username.trim().isEmpty()) {
                // 如果提供了用户名，进行模糊搜索
                users = userMapper.searchUsersByUsername(username.trim());
            } else {
                // 否则获取所有用户
                users = userMapper.getAllUsers();
            }
            // 出于安全考虑，清除所有用户的密码信息
            users.forEach(user -> user.setPassword(null));
            return Result.success(users);
        } catch (Exception e) {
            log.error("获取用户列表失败", e);
            return Result.error("获取用户列表失败");
        }
    }

    /**
     * 更新用户信息
     */
    @PostMapping("/updateUser")
    public Result<String> updateUser(@RequestBody User user, HttpSession session) {
        // 检查管理员是否登录
        if (session.getAttribute("loginAdmin") == null) {
            return Result.error("未登录");
        }
        
        try {
            if (user.getUserId() == null) {
                return Result.error("用户ID不能为空");
            }
            
            // 检查用户是否存在
            User existingUser = userMapper.getUserById(user.getUserId());
            if (existingUser == null) {
                return Result.error("用户不存在");
            }
            
            // 设置不允许修改的字段
            user.setPassword(null); // 不通过这个接口修改密码
            
            // 更新用户信息
            int result = userMapper.update(user);
            if (result > 0) {
                return Result.success("更新成功");
            } else {
                return Result.error("更新失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("更新用户信息失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID获取用户信息
     */
    @GetMapping("/getUserById")
    public Result<User> getUserById(@RequestParam Integer userId, HttpSession session) {
        // 检查管理员是否登录
        if (session.getAttribute("loginAdmin") == null) {
            return Result.error("未登录");
        }
        
        try {
            User user = userMapper.getUserById(userId);
            if (user != null) {
                // 出于安全考虑，不返回密码
                user.setPassword(null);
                return Result.success(user);
            } else {
                return Result.error("用户不存在");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取用户信息失败: " + e.getMessage());
        }
    }
}
