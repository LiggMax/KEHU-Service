package com.ligg.controller.admin;

import com.ligg.mapper.UserMapper;
import com.ligg.mapper.VideoMapper;
import com.ligg.mapper.CommentMapper;
import com.ligg.pojo.Admin;
import com.ligg.pojo.Comment;
import com.ligg.pojo.Result;
import com.ligg.pojo.User;
import com.ligg.pojo.Video;
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

    @Autowired
    private VideoMapper videoMapper;
    
    @Autowired
    private CommentMapper commentMapper;

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

    /**
     * 获取视频列表
     */
    @GetMapping("/videoList")
    public Result getVideoList(@RequestParam(required = false) String title, HttpSession session) {
        // 检查管理员是否登录
        Admin loginAdmin = (Admin) session.getAttribute("loginAdmin");
        if (loginAdmin == null) {
            return Result.error("未登录");
        }

        try {
            List<Video> videos;
            if (title != null && !title.trim().isEmpty()) {
                // 如果提供了标题，进行模糊搜索
                videos = videoMapper.searchVideosByTitle(title.trim());
            } else {
                // 否则获取所有视频
                videos = videoMapper.getAllVideos();
            }
            return Result.success(videos);
        } catch (Exception e) {
            log.error("获取视频列表失败", e);
            return Result.error("获取视频列表失败");
        }
    }

    /**
     * 根据ID获取视频信息
     */
    @GetMapping("/getVideoById")
    public Result<Video> getVideoById(@RequestParam Integer videoId, HttpSession session) {
        // 检查管理员是否登录
        if (session.getAttribute("loginAdmin") == null) {
            return Result.error("未登录");
        }
        
        try {
            Video video = videoMapper.getVideoById(videoId);
            if (video != null) {
                return Result.success(video);
            } else {
                return Result.error("视频不存在");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取视频信息失败: " + e.getMessage());
        }
    }

    /**
     * 删除视频
     */
    @DeleteMapping("/videos/{id}")
    public Result deleteVideo(@PathVariable Integer id, HttpSession session) {
        // 检查管理员是否登录
        Admin loginAdmin = (Admin) session.getAttribute("loginAdmin");
        if (loginAdmin == null) {
            return Result.error("未登录");
        }

        try {
            // 先获取视频信息，检查视频是否存在
            Video video = videoMapper.getVideoById(id);
            if (video == null) {
                return Result.error("视频不存在");
            }

            // 删除视频
            int rows = videoMapper.deleteById(id);
            if (rows > 0) {
                return Result.success("删除成功");
            } else {
                
                return Result.error("删除失败");
            }
        } catch (Exception e) {
            log.error("删除视频失败", e);
            return Result.error("删除视频失败: " + e.getMessage());
        }
    }

    /**
     * 获取评论列表
     */
    @GetMapping("/commentList")
    public Result getCommentList(@RequestParam(required = false) String content, HttpSession session) {
        // 检查管理员是否登录
        Admin loginAdmin = (Admin) session.getAttribute("loginAdmin");
        if (loginAdmin == null) {
            return Result.error("未登录");
        }

        try {
            List<Comment> comments;
            if (content != null && !content.trim().isEmpty()) {
                // 如果提供了内容，进行模糊搜索
                comments = commentMapper.searchCommentsByContent(content.trim());
            } else {
                // 否则获取所有评论
                comments = commentMapper.getAllComments();
            }
            return Result.success(comments);
        } catch (Exception e) {
            log.error("获取评论列表失败", e);
            return Result.error("获取评论列表失败");
        }
    }

    /**
     * 根据ID获取评论信息
     */
    @GetMapping("/getCommentById")
    public Result<Comment> getCommentById(@RequestParam Integer commentId, HttpSession session) {
        // 检查管理员是否登录
        if (session.getAttribute("loginAdmin") == null) {
            return Result.error("未登录");
        }
        
        try {
            Comment comment = commentMapper.getCommentById(commentId);
            if (comment != null) {
                return Result.success(comment);
            } else {
                return Result.error("评论不存在");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取评论信息失败: " + e.getMessage());
        }
    }

    /**
     * 删除评论
     */
    @DeleteMapping("/comments/{id}")
    public Result deleteComment(@PathVariable Integer id, HttpSession session) {
        // 检查管理员是否登录
        Admin loginAdmin = (Admin) session.getAttribute("loginAdmin");
        if (loginAdmin == null) {
            return Result.error("未登录");
        }

        try {
            // 先获取评论信息，检查评论是否存在
            Comment comment = commentMapper.getCommentById(id);
            if (comment == null) {
                return Result.error("评论不存在");
            }

            // 删除评论
            int rows = commentMapper.deleteById(id);
            if (rows > 0) {
                return Result.success("删除成功");
            } else {
                return Result.error("删除失败");
            }
        } catch (Exception e) {
            log.error("删除评论失败", e);
            return Result.error("删除评论失败: " + e.getMessage());
        }
    }
}
