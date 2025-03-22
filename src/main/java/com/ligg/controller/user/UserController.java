package com.ligg.controller.user;


import com.ligg.pojo.Result;
import com.ligg.pojo.User;
import com.ligg.pojo.Video;
import com.ligg.service.VideoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private VideoService videoService;


    /**
     * 登出
     */
    @PostMapping("/logout")
    public Result<String> logout(HttpSession session) {
        session.removeAttribute("loginUser");
        return Result.success();
    }

    /**
     * 上传视频
     */
    @PostMapping("/upload")
    public Result<Video> uploadVideo(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("videoFile") MultipartFile videoFile,
            @RequestParam(value = "coverFile", required = false) MultipartFile coverFile,
            HttpSession session) {
        // 获取当前登录用户
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return Result.error("请先登录");
        }

        try {
            // 调用服务层保存视频
            Video video = videoService.saveVideo(title, description, videoFile, coverFile, loginUser.getUserId());
            return Result.success(video);
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("视频上传失败");
        }
    }

    /**
     * 获取当前用户的视频列表
     */
    @GetMapping("/list")
    public Result<List<Video>> getUserVideos(HttpSession session) {
        // 获取当前登录用户
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return Result.error("请先登录");
        }

        // 调用服务层获取视频列表
        List<Video> videos = videoService.getUserVideos(loginUser.getUserId());
        return Result.success(videos);
    }

}
